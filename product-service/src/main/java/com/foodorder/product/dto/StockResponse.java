package com.foodorder.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stock Response DTO
 * Contains inventory information stored separately from product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {

    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("updatedAt")
    private String updatedAt;
}
