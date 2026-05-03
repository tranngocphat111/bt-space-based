package com.foodorder.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cache properties configuration
 * Maps cache-related properties from application.properties
 */
@Component
@ConfigurationProperties(prefix = "app.cache")
@Data
public class CacheProperties {
    private Long ttlSeconds = 120L;
}
