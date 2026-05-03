package com.foodorder.product.service;

import com.foodorder.product.config.AppProperties;
import com.foodorder.product.dto.ProductMessageRequest;
import com.foodorder.product.dto.ProductMessageResponse;
import com.foodorder.product.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Product Query Via MQ Service
 * Handles RPC-style communication with Persistence Service via RabbitMQ
 */
@Service
@Slf4j
public class ProductQueryViaMqService {

    private final RabbitTemplate rabbitTemplate;
    private final AppProperties appProperties;

    public ProductQueryViaMqService(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
    }

    /**
     * Query list of products from Persistence Service
     * @return ProductMessageResponse containing list of products
     */
    public ProductMessageResponse queryProductList() {
        log.info("Querying product list from Persistence Service via MQ");

        String correlationId = UUID.randomUUID().toString();
        ProductMessageRequest request = ProductMessageRequest.builder()
                .correlationId(correlationId)
                .requestType("LIST_PRODUCTS")
                .replyTo(appProperties.getPersistence().getReplyQueue())
                .timestamp(System.currentTimeMillis())
                .build();

        try {
            log.debug("Sending MQ request with correlationId: {} for LIST_PRODUCTS", correlationId);

            Object response = rabbitTemplate.convertSendAndReceive(
                    appProperties.getPersistence().getExchange(),
                    appProperties.getPersistence().getRoutingKey().getListProducts(),
                    request
            );

            if (response == null) {
                log.error("No response from Persistence Service for LIST_PRODUCTS (timeout)");
                throw new ServiceUnavailableException(
                        "Persistence Service not responding. Please try again later."
                );
            }

            ProductMessageResponse result = (ProductMessageResponse) response;
            log.info("Received response from Persistence Service for correlationId: {}", correlationId);

            if (!result.getSuccess()) {
                log.error("Persistence Service returned error: {}", result.getErrorMessage());
                throw new ServiceUnavailableException(
                        "Persistence Service error: " + result.getErrorMessage()
                );
            }

            return result;

        } catch (Exception ex) {
            if (ex instanceof ServiceUnavailableException) {
                throw ex;
            }
            log.error("Error querying product list via MQ: {}", ex.getMessage(), ex);
            throw new ServiceUnavailableException(
                    "Failed to query products: " + ex.getMessage()
            );
        }
    }

    /**
     * Query product by ID from Persistence Service
     * @param productId product id
     * @return ProductMessageResponse containing single product
     */
    public ProductMessageResponse queryProductById(Long productId) {
        log.info("Querying product by id: {} from Persistence Service via MQ", productId);

        String correlationId = UUID.randomUUID().toString();
        ProductMessageRequest request = ProductMessageRequest.builder()
                .correlationId(correlationId)
                .requestType("GET_BY_ID")
                .productId(productId)
                .replyTo(appProperties.getPersistence().getReplyQueue())
                .timestamp(System.currentTimeMillis())
                .build();

        try {
            log.debug("Sending MQ request with correlationId: {} for GET_BY_ID (productId: {})", 
                    correlationId, productId);

            Object response = rabbitTemplate.convertSendAndReceive(
                    appProperties.getPersistence().getExchange(),
                    appProperties.getPersistence().getRoutingKey().getProductById(),
                    request
            );

            if (response == null) {
                log.error("No response from Persistence Service for GET_BY_ID (timeout)");
                throw new ServiceUnavailableException(
                        "Persistence Service not responding. Please try again later."
                );
            }

            ProductMessageResponse result = (ProductMessageResponse) response;
            log.info("Received response from Persistence Service for correlationId: {}", correlationId);

            if (!result.getSuccess()) {
                log.error("Persistence Service returned error: {}", result.getErrorMessage());
                throw new ServiceUnavailableException(
                        "Persistence Service error: " + result.getErrorMessage()
                );
            }

            return result;

        } catch (Exception ex) {
            if (ex instanceof ServiceUnavailableException) {
                throw ex;
            }
            log.error("Error querying product by id via MQ: {}", ex.getMessage(), ex);
            throw new ServiceUnavailableException(
                    "Failed to query product: " + ex.getMessage()
            );
        }
    }
}
