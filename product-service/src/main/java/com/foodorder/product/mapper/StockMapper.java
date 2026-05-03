package com.foodorder.product.mapper;

import com.foodorder.product.dto.StockResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Stock Mapper
 * Maps inventory/stock data from persistence service
 * Stock is stored separately from product in Redis (stock:{productId})
 * and in database (inventory table)
 */
@Component
@Slf4j
public class StockMapper {

    /**
     * Map stock data to StockResponse DTO
     * @param productId product id
     * @param stock quantity in stock
     * @param updatedAt last update timestamp
     * @return StockResponse DTO
     */
    public StockResponse toStockResponse(
            Long productId,
            Integer stock,
            String updatedAt) {
        
        StockResponse response = StockResponse.builder()
                .productId(productId)
                .stock(stock != null ? stock : 0)
                .updatedAt(updatedAt)
                .build();
        
        log.debug("Mapped stock for product {}: stock={}, updatedAt={}", 
                productId, stock, updatedAt);
        
        return response;
    }

    /**
     * Map stock data with default values
     * @param productId product id
     * @param stock quantity in stock
     * @return StockResponse DTO
     */
    public StockResponse toStockResponse(Long productId, Integer stock) {
        return toStockResponse(productId, stock, null);
    }
}
