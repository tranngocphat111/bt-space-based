package iuh.fit.edu.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body cho POST /checkout.
 * Chỉ cần sessionId – order-service đọc cart từ Redis key "cart:{sessionId}".
 *
 * Mapping với orders.session_id
 */
@Data
public class CheckoutRequest {

    @NotBlank(message = "sessionId is required")
    private String sessionId;
}
