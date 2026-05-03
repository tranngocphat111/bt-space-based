package com.foodorder.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Message Request DTO
 * Sent via RabbitMQ to Persistence Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMessageRequest {

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("requestType")
    private String requestType; // "LIST_PRODUCTS" or "GET_BY_ID"

    @JsonProperty("productId")
    private Long productId; // null for LIST_PRODUCTS

    @JsonProperty("replyTo")
    private String replyTo;

    @JsonProperty("timestamp")
    private Long timestamp;
}
