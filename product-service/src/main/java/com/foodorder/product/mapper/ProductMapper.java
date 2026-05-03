package com.foodorder.product.mapper;

import com.foodorder.product.config.AppProperties;
import com.foodorder.product.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Product Mapper
 * Maps product data with image URL transformation
 */
@Component
@Slf4j
public class ProductMapper {

    private final AppProperties appProperties;

    public ProductMapper(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Build full image URL from imageName
     * @param imageName simple image name (e.g., "iphone15.jpg")
     * @return full S3 URL
     */
    public String buildImageUrl(String imageName) {
        if (imageName == null || imageName.isBlank()) {
            return null;
        }
        String baseUrl = appProperties.getS3().getBaseUrl();
        String fullUrl = baseUrl + "/" + imageName;
        log.debug("Mapped image: {} -> {}", imageName, fullUrl);
        return fullUrl;
    }

    /**
     * Map product data with image URL
     * @param id product id
     * @param name product name
     * @param description product description
     * @param price product price
     * @param stock current stock
     * @param imageName image file name
     * @param category product category
     * @param createdAt created timestamp
     * @param updatedAt updated timestamp
     * @return ProductResponse DTO
     */
    public ProductResponse toProductResponse(
            Long id,
            String name,
            String description,
            Double price,
            Integer stock,
            String imageName,
            String category,
            String createdAt,
            String updatedAt) {

        return ProductResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .imageUrl(buildImageUrl(imageName))
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
