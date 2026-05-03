package com.foodorder.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.product.config.AppProperties;
import com.foodorder.product.dto.ProductMessageResponse;
import com.foodorder.product.dto.ProductResponse;
import com.foodorder.product.exception.NotFoundException;
import com.foodorder.product.mapper.ProductMapper;
import com.foodorder.product.util.CacheKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Product Service
 * Handles business logic with Redis caching and MQ fallback
 * Implements Space-Based Architecture pattern
 */
@Service
@Slf4j
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductQueryViaMqService queryViaMqService;
    private final ProductMapper productMapper;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public ProductService(
            RedisTemplate<String, String> redisTemplate,
            ProductQueryViaMqService queryViaMqService,
            ProductMapper productMapper,
            AppProperties appProperties,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.queryViaMqService = queryViaMqService;
        this.productMapper = productMapper;
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Get all products
     * Logic: Try Redis cache only (no persistence service fallback)
     * Cache must be pre-populated during startup (PU1)
     * @return List of ProductResponse
     */
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all products from cache");

        String cacheKey = CacheKeyUtil.getProductListKey();

        // Try to get from Redis cache only
        try {
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.info("Cache HIT for product list");
                List<ProductResponse> products = objectMapper.readValue(
                        cachedData,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ProductResponse.class)
                );
                
                // Map lại để lấy đúng đường link ảnh và bổ sung thông tin tồn kho từ key inventory
                return products.stream()
                        .map(p -> {
                            // 1. Map lại image URL
                            ProductResponse mapped = productMapper.mapToResponse(p);
                            
                            // 2. Lấy stock từ key inventory:{id}
                            String inventoryKey = "inventory:" + mapped.getId();
                            String stockStr = redisTemplate.opsForValue().get(inventoryKey);
                            mapped.setStock(stockStr != null ? Integer.parseInt(stockStr) : 0);
                            
                            return mapped;
                        })
                        .collect(java.util.stream.Collectors.toList());
            }
        } catch (Exception ex) {
            log.warn("Error reading from Redis cache: {}", ex.getMessage());
        }

        // Cache miss - return empty list (data must be loaded during startup)
        log.warn("Cache MISS for product list - returning empty list");
        return List.of();
    }

    /**
     * Get product by ID
     * Logic: Try Redis cache only (no persistence service fallback)
     * Cache must be pre-populated during startup (PU1)
     * @param productId product id
     * @return ProductResponse
     */
    public ProductResponse getProductById(Long productId) {
        log.info("Getting product by id: {} from cache", productId);

        // Validate input
        if (productId == null || productId <= 0) {
            log.warn("Invalid product id: {}", productId);
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }

        String cacheKey = CacheKeyUtil.getProductDetailKey(productId);

        // Try to get from Redis cache only
        try {
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.info("Cache HIT for product id: {}", productId);
                ProductResponse product = objectMapper.readValue(cachedData, ProductResponse.class);
                
                // Map lại để lấy đúng đường link ảnh
                ProductResponse mapped = productMapper.mapToResponse(product);
                
                // Bổ sung thông tin tồn kho từ key inventory:{id}
                String inventoryKey = "inventory:" + mapped.getId();
                String stockStr = redisTemplate.opsForValue().get(inventoryKey);
                mapped.setStock(stockStr != null ? Integer.parseInt(stockStr) : 0);
                
                return mapped;
            }
        } catch (Exception ex) {
            log.warn("Error reading from Redis cache for product {}: {}", productId, ex.getMessage());
        }

        // Cache miss - product data must be loaded during startup (PU1)
        log.warn("Cache MISS for product id: {} - product not found in cache", productId);
        throw new NotFoundException("Product with id " + productId + " not found in cache. Please wait for cache initialization.");
    }

    /**
     * Invalidate product cache (for future use in updates/deletes)
     * @param productId product id
     */
    public void invalidateProductCache(Long productId) {
        try {
            String cacheKey = CacheKeyUtil.getProductDetailKey(productId);
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (deleted != null && deleted) {
                log.info("Cache invalidated for product id: {}", productId);
            }
        } catch (Exception ex) {
            log.warn("Error invalidating cache for product {}: {}", productId, ex.getMessage());
        }
    }

    /**
     * Invalidate product list cache
     */
    public void invalidateProductListCache() {
        try {
            String cacheKey = CacheKeyUtil.getProductListKey();
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (deleted != null && deleted) {
                log.info("Cache invalidated for product list");
            }
        } catch (Exception ex) {
            log.warn("Error invalidating product list cache: {}", ex.getMessage());
        }
    }
}
