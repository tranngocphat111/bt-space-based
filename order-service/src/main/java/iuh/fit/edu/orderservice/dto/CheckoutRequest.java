package iuh.fit.edu.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body cho POST /checkout.
 *
 * Chỉ cần userId – order-service đọc cart từ Redis key "cart:{userId}",
 * khớp với cách cart-service lưu: redisTemplate.opsForValue().set("cart:" + userId, cart)
 */
@Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutRequest {

    @NotBlank(message = "userId is required")
    private String userId;
}
