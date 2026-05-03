package com.foodorder.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application properties configuration for Product Service
 * Maps properties from application.properties with prefix "app"
 */
@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private Cache cache = new Cache();
    private S3 s3 = new S3();
    private Persistence persistence = new Persistence();
    private Long rabbitmqMqRequestTimeoutMs = 5000L;

    @Data
    public static class Cache {
        private Long ttlSeconds = 120L;
    }

    @Data
    public static class S3 {
        private String baseUrl = "https://food-service-images.s3.ap-southeast-1.amazonaws.com/products";
    }

    @Data
    public static class Persistence {
        private String requestQueue = "persistence.product.request";
        private String replyQueue = "persistence.product.reply";
        private String exchange = "persistence.exchange";
        private RoutingKey routingKey = new RoutingKey();

        @Data
        public static class RoutingKey {
            private String listProducts = "persistence.product.list";
            private String productById = "persistence.product.get-by-id";
            // Stock/Inventory routing keys (separated from product)
            private String getStock = "persistence.stock.get";
            private String updateStock = "persistence.stock.update";
        }
    }
}
