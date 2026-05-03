package iuh.fit.edu.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.edu.orderservice.client.InventoryClient;
import iuh.fit.edu.orderservice.config.AppConfig;
import iuh.fit.edu.orderservice.dto.CartDto;
import iuh.fit.edu.orderservice.dto.CheckoutRequest;
import iuh.fit.edu.orderservice.dto.InventoryCheckoutRequest;
import iuh.fit.edu.orderservice.dto.OrderDto;
import iuh.fit.edu.orderservice.exception.BadRequestException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Order Processing Unit (PU3) – Space-Based Architecture.
 *
 * Luồng checkout (không đụng DB):
 *  1. Đọc CartDto từ Redis  →  key: "cart:{userId}"
 *  2. Gọi inventory-service POST /decrease qua OpenFeign
 *  3. Build OrderDto (khớp bảng orders + order_items)
 *  4. Lưu OrderDto vào Redis  →  key: "order:{id}"  (TTL 24h)
 *  5. Xoá cart khỏi Redis
 *  6. Bắn event "order_created" lên RabbitMQ
 *
 * Schema mapping:
 *   orders     : session_id, status, total_amount, created_at, updated_at
 *   order_items: product_id, quantity, unit_price
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String CART_KEY_PREFIX = "cart:";
    private static final String ORDER_KEY_PREFIX = "order:";
    private static final long   ORDER_TTL_HOURS  = 24;

    private final RedisTemplate<String, String> redisTemplate;
    private final InventoryClient inventoryClient;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private final AtomicLong orderIdSequence = new AtomicLong(System.currentTimeMillis());

    // ── Checkout ──────────────────────────────────────────────────────────────

    public OrderDto checkout(CheckoutRequest request) {
        String userId = request.getUserId();

        // 1. Đọc cart từ Redis
        CartDto cart = getCartFromRedis(userId);

        // 2. Giảm tồn kho qua OpenFeign → inventory-service POST /decrease
        decreaseStock(cart);

        // 3. Tạo OrderDto (khớp schema: session_id, status, total_amount, items)
        OrderDto order = buildOrder(cart);

        // 4. Lưu order vào Redis (Data Grid)
        saveOrderToRedis(order);

        // 5. Xoá cart sau khi đặt hàng thành công
        redisTemplate.delete(CART_KEY_PREFIX + userId);
        log.debug("Cart deleted from Redis: userId={}", userId);

        // 6. Bắn event order_created → RabbitMQ
        rabbitTemplate.convertAndSend(AppConfig.ORDER_CREATED_QUEUE, order);
        log.info("Event 'order_created' published: orderId={}, userId={}", order.getId(), userId);

        return order;
    }

    // ── Query ─────────────────────────────────────────────────────────────────

    public OrderDto getOrder(Long orderId) {
        String key = ORDER_KEY_PREFIX + orderId;
        String raw = redisTemplate.opsForValue().get(key);
        if (raw == null) {
            throw new BadRequestException("Order not found in Data Grid: " + orderId);
        }
        try {
            return objectMapper.readValue(raw, OrderDto.class);
        } catch (Exception e) {
            log.error("Error deserializing order from Redis: {}", e.getMessage());
            throw new BadRequestException("Error reading order data");
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private CartDto getCartFromRedis(String userId) {
        String raw = redisTemplate.opsForValue().get(CART_KEY_PREFIX + userId);
        if (raw == null) {
            throw new BadRequestException("Cart not found for userId: " + userId);
        }
        try {
            CartDto cart = objectMapper.readValue(raw, CartDto.class);
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new BadRequestException("Cart is empty for userId: " + userId);
            }
            log.debug("Cart loaded: userId={}, items={}", userId, cart.getItems().size());
            return cart;
        } catch (Exception e) {
            log.error("Error deserializing cart from Redis for userId {}: {}", userId, e.getMessage());
            throw new BadRequestException("Error reading cart data from Redis");
        }
    }

    private void decreaseStock(CartDto cart) {
        List<InventoryCheckoutRequest.Item> items = cart.getItems().stream()
                .map(i -> {
                    // Cần parse productId (String) của cart sang Long của inventory-service
                    Long productId;
                    try {
                        productId = Long.parseLong(i.getProductId());
                    } catch (NumberFormatException e) {
                        log.error("Invalid productId format in cart: {}", i.getProductId());
                        throw new BadRequestException("Invalid productId format in cart: " + i.getProductId());
                    }
                    return new InventoryCheckoutRequest.Item(productId, i.getQuantity());
                })
                .collect(Collectors.toList());

        try {
            ResponseEntity<String> resp = inventoryClient.decreaseStock(new InventoryCheckoutRequest(items));
            if (resp == null || !resp.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException("Inventory service error when decreasing stock");
            }
        } catch (FeignException e) {
            log.error("Feign error calling inventory /decrease: {}", e.getMessage());
            throw new BadRequestException("Thất bại: Không đủ tồn kho");
        }
        log.info("Stock decreased for {} product(s)", items.size());
    }

    private OrderDto buildOrder(CartDto cart) {
        LocalDateTime now = LocalDateTime.now();
        Long id = orderIdSequence.incrementAndGet();

        // Tính total_amount từ order_items
        List<OrderDto.OrderItemDto> orderItems = cart.getItems().stream()
                .map(item -> {
                     Long productId;
                     try {
                         productId = Long.parseLong(item.getProductId());
                     } catch (NumberFormatException e) {
                         throw new BadRequestException("Invalid productId format: " + item.getProductId());
                     }
                     return new OrderDto.OrderItemDto(
                        productId,
                        item.getQuantity(),
                        BigDecimal.valueOf(item.getPrice()), // Convert long price to BigDecimal
                        item.getProductName()
                     );
                })
                .collect(Collectors.toList());

        BigDecimal total = BigDecimal.valueOf(cart.getTotalAmount()); // Use totalAmount from CartResponse

        return new OrderDto(
                id,
                cart.getUserId(),      // Lấy userId làm session_id cho DB
                "pending",             // orders.status  (enum: pending | confirmed | cancelled)
                total,                 // orders.total_amount
                now,                   // orders.created_at
                now,                   // orders.updated_at
                orderItems
        );
    }

    private void saveOrderToRedis(OrderDto order) {
        try {
            String key = ORDER_KEY_PREFIX + order.getId();
            String json = objectMapper.writeValueAsString(order);
            redisTemplate.opsForValue().set(key, json, ORDER_TTL_HOURS, TimeUnit.HOURS);
            log.info("Order saved to Redis: key={}", key);
        } catch (Exception e) {
            log.error("Error saving order to Redis: {}", e.getMessage());
        }
    }
}