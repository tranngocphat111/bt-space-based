package com.foodorder.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Product Message Response DTO
 * Received via RabbitMQ from Persistence Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMessageResponse {

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("products")
    private List<ProductResponse> products;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("timestamp")
    private Long timestamp;
}
