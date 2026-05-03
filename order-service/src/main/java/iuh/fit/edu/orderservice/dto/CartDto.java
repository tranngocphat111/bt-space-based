package iuh.fit.edu.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Giỏ hàng lưu trong Redis (Data Grid) bởi Cart PU (PU2).
 * Redis key: "cart:{sessionId}"
 *
 * Mapping với schema:
 *   orders.session_id  ← sessionId
 *   order_items.product_id, quantity, unit_price ← CartItemDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Serializable {

    /** Session ID của người dùng (thay cho userId) */
    private String sessionId;

    private List<CartItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDto implements Serializable {
        private Long productId;
        private String productName;   // để hiển thị, không lưu DB
        private int quantity;
        private BigDecimal unitPrice;
    }
}
