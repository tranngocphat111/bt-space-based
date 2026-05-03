package com.foodorder.product.util;

/**
 * Cache Key Utility
 * Generates consistent cache keys for Redis
 * Separates product data from stock/inventory data
 */
public class CacheKeyUtil {

    // Product Keys
    public static final String PRODUCT_LIST_KEY = "product:list";
    public static final String PRODUCT_DETAIL_KEY_PREFIX = "product:";
    
    // Stock/Inventory Keys (Separated from product data)
    public static final String STOCK_KEY_PREFIX = "stock:";

    /**
     * Get cache key for product detail
     * @param productId product id
     * @return cache key for product (e.g., "product:1")
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

    /**
     * Get cache key for stock/inventory (separated from product)
     * @param productId product id
     * @return cache key for stock (e.g., "stock:1")
     */
    public static String getStockKey(Long productId) {
        return STOCK_KEY_PREFIX + productId;
    }
}
