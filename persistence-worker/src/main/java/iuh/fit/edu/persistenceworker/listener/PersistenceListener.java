package iuh.fit.edu.persistenceworker.listener;

import iuh.fit.edu.persistenceworker.dto.CacheMissRequest;
import iuh.fit.edu.persistenceworker.dto.CheckoutMessage;
import iuh.fit.edu.persistenceworker.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ Listeners for Persistence Operations
 * Handles both Write (mq) and Read (mq-read) queue operations
 */
@Component
@Slf4j
public class PersistenceListener {

    private final PersistenceService persistenceService;

    public PersistenceListener(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Listens to 'mq' queue for checkout messages
     * Persists order and inventory updates to database
     */
    @RabbitListener(queues = "mq")
    public void handleCheckoutMessage(CheckoutMessage message) {
        Integer startTime = Math.toIntExact(System.currentTimeMillis());
        
        try {
            log.info("Received checkout message: sessionId={}, productId={}, quantity={}, totalAmount={}",
                    message.getSessionId(), message.getProductId(), message.getQuantity(), message.getTotalAmount());

            persistenceService.persistCheckout(
                    message.getSessionId(),
                    message.getProductId(),
                    message.getQuantity(),
                    message.getTotalAmount(),
                    message.getUnitPrice()
            );

            Integer duration = Math.toIntExact(System.currentTimeMillis() - startTime);
            log.info("Checkout message processed successfully in {}ms", duration);

        } catch (Exception e) {
            log.error("Error processing checkout message: {}", message, e);
            throw new RuntimeException("Failed to process checkout message", e);
        }
    }

    /**
     * Listens to 'mq-read' queue for cache miss recovery requests.
     * PU2 sends {"product_id": <id>} when a product is not found in Redis.
     */
    @RabbitListener(queues = "mq-read")
    public void handleCacheMissRecovery(CacheMissRequest request) {
        Integer startTime = Math.toIntExact(System.currentTimeMillis());
        
        try {
            log.info("Received cache miss recovery request for productId: {}", request.getProductId());
            persistenceService.loadProductToRedis(request.getProductId());
            Integer duration = Math.toIntExact(System.currentTimeMillis() - startTime);
            log.info("Cache recovery completed in {}ms for productId: {}", duration, request.getProductId());
        } catch (Exception e) {
            log.error("Error handling cache miss recovery for productId: {}", request.getProductId(), e);
        }
    }
}
