package iuh.fit.edu.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình URL của inventory-service, đọc từ application.properties.
 * Dùng để inject vào FeignClient configuration.
 */
@Configuration
public class FeignConfig {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public String getInventoryServiceUrl() {
        return inventoryServiceUrl;
    }
}
