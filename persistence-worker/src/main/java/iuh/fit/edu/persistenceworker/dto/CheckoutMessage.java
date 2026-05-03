package iuh.fit.edu.persistenceworker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutMessage {
    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("quantity")
    private Short  quantity;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
}
