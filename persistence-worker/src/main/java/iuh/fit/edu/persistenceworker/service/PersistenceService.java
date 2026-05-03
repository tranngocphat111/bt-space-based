package iuh.fit.edu.persistenceworker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.edu.persistenceworker.dto.OrderDto;
import iuh.fit.edu.persistenceworker.dto.ProductWithStockDto;
import iuh.fit.edu.persistenceworker.entity.Inventory;
import iuh.fit.edu.persistenceworker.entity.Order;
import iuh.fit.edu.persistenceworker.entity.OrderItem;
import iuh.fit.edu.persistenceworker.entity.Product;
import iuh.fit.edu.persistenceworker.repository.InventoryRepository;
import iuh.fit.edu.persistenceworker.repository.OrderItemRepository;
import iuh.fit.edu.persistenceworker.repository.OrderRepository;
import iuh.fit.edu.persistenceworker.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PersistenceService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public PersistenceService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // ============ WRITE OPERATIONS ============

    @Transactional
    public void persistOrder(OrderDto orderDto) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("Processing OrderDto persistence: id={}, sessionId={}, items={}",
                    orderDto.getId(), orderDto.getSessionId(), orderDto.getItems().size());

            // 1. Save Order (khớp id từ Redis để đồng bộ)
            Order order = Order.builder()
                    .id(orderDto.getId()) // Giữ nguyên ID từ Redis
                    .sessionId(orderDto.getSessionId())
                    .status(Order.OrderStatus.valueOf(orderDto.getStatus()))
                    .totalAmount(orderDto.getTotalAmount())
                    .createdAt(orderDto.getCreatedAt())
                    .updatedAt(orderDto.getUpdatedAt())
                    .build();
            
            // Dùng save() của JpaRepository, nếu ID đã tồn tại nó sẽ update, nhưng ở đây là tạo mới
            Order savedOrder = orderRepository.save(order);

            // 2. Save OrderItems & Update Inventory
            for (OrderDto.OrderItemDto itemDto : orderDto.getItems()) {
                // Lưu OrderItem
                OrderItem item = OrderItem.builder()
                        .orderId(savedOrder.getId())
                        .productId(itemDto.getProductId().intValue())
                        .quantity((short) itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .build();
                orderItemRepository.save(item);

                // Cập nhật Inventory trong DB (giảm số lượng)
                inventoryRepository.findByProductId(itemDto.getProductId().intValue())
                        .ifPresent(inventory -> {
                            inventory.setStock(inventory.getStock() - itemDto.getQuantity());
                            inventory.setUpdatedAt(LocalDateTime.now());
                            inventoryRepository.save(inventory);
                        });
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Order {} persisted successfully in {}ms", orderDto.getId(), duration);

        } catch (Exception e) {
            log.error("Error persisting order: {}", orderDto.getId(), e);
            throw new RuntimeException("Failed to persist order", e);
        }
    }

    // ============ READ OPERATIONS (Data Loading to Redis) ============

    public void bootstrapDataToRedis() {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Starting data bootstrap to Redis...");

            // Get all products
            List<Product> products = productRepository.findAll();
            log.debug("Found {} products from database", products.size());

            // Get all inventory records
            List<Inventory> inventoryList = inventoryRepository.findAll();
            Map<Integer, Integer> stockMap = inventoryList.stream()
                    .collect(Collectors.toMap(Inventory::getProductId, Inventory::getStock));

            // Build DTOs with stock info
            List<ProductWithStockDto> productsWithStock = products.stream()
                    .map(product -> ProductWithStockDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .imageUrl(product.getImageUrl())
                            .category(product.getCategory())
                            .stock(stockMap.getOrDefault(product.getId(), 0))
                            .build())
                    .collect(Collectors.toList());

            // Store in Redis - Option 1: List toàn bộ sản phẩm (dùng cho GET /api/products)
            String productListKey = "product:list";
            String productsJson = objectMapper.writeValueAsString(productsWithStock);
            redisTemplate.opsForValue().set(productListKey, productsJson);
            log.debug("Stored product:list with {} entries", productsWithStock.size());

            // Store in Redis - Option 2: Từng sản phẩm riêng lẻ (dùng cho GET /api/products/{id})
            for (ProductWithStockDto product : productsWithStock) {
                String productKey = "product:" + product.getId();
                String productJson = objectMapper.writeValueAsString(product);
                redisTemplate.opsForValue().set(productKey, productJson);
                
                // Option 3: Lưu stock riêng (nếu product-service cần fetch riêng)
                String stockKey = "inventory:" + product.getId();
                redisTemplate.opsForValue().set(stockKey, product.getStock().toString());
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Data bootstrap completed successfully in {}ms. Loaded {} products to Redis",
                    duration, productsWithStock.size());

        } catch (Exception e) {
            log.error("Error during bootstrap data loading", e);
            throw new RuntimeException("Failed to bootstrap data to Redis", e);
        }
    }

    public ProductWithStockDto loadProductToRedis(Integer productId) {
        try {
            log.info("Recovering product {} to Redis due to cache miss", productId);

            // Get product from DB
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            // Get inventory (dùng query trực tiếp thay vì scan toàn bộ)
            int stock = inventoryRepository.findByProductId(productId)
                    .map(Inventory::getStock)
                    .orElse(0);

            // Build DTO
            ProductWithStockDto productWithStock = ProductWithStockDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .imageUrl(product.getImageUrl())
                    .category(product.getCategory())
                    .stock(stock)
                    .build();

            // Store in Redis (không lưu stock vào JSON product)
            String productKey = "product:" + productId;
            ProductWithStockDto productOnly = ProductWithStockDto.builder()
                    .id(productWithStock.getId())
                    .name(productWithStock.getName())
                    .description(productWithStock.getDescription())
                    .price(productWithStock.getPrice())
                    .imageUrl(productWithStock.getImageUrl())
                    .category(productWithStock.getCategory())
                    .stock(null)
                    .build();
            
            String productJson = objectMapper.writeValueAsString(productOnly);
            redisTemplate.opsForValue().set(productKey, productJson);
            
            // Lưu stock riêng vào key inventory
            String stockKey = "inventory:" + productId;
            redisTemplate.opsForValue().set(stockKey, String.valueOf(stock));

            log.debug("Product {} recovered to Redis", productId);
            return productWithStock;

        } catch (Exception e) {
            log.error("Error recovering product {} to Redis", productId, e);
            throw new RuntimeException("Failed to recover product to Redis", e);
        }
    }
}
