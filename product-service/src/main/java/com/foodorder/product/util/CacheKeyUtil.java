package com.foodorder.product.util;

/**
 * Cache Key Utility
 * Generates consistent cache keys for Redis
 */
public class CacheKeyUtil {

    public static final String PRODUCT_LIST_KEY = "product:list";
    public static final String PRODUCT_DETAIL_KEY_PREFIX = "product:";

    /**
     * Get cache key for product detail
     * @param productId product id
     * @return cache key
     */
    public static String getProductDetailKey(Long productId) {
        return PRODUCT_DETAIL_KEY_PREFIX + productId;
    }

    /**
     * Get cache key for product list
     * @return cache key
     */
    public static String getProductListKey() {
        return PRODUCT_LIST_KEY;
    }
}
