package iuh.fit.edu.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Biểu diễn Order lưu trong Redis (Data Grid) sau khi checkout.
 * Redis key: "order:{id}"
 *
 * Fields khớp với bảng orders + order_items trong SQL:
 *   orders     : id, session_id, status, total_amount, created_at, updated_at
 *   order_items: product_id, quantity, unit_price
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {

    // ── orders ────────────────────────────────────────────
    private Long id;                    // orders.id
    private String sessionId;           // orders.session_id
    private String status;              // orders.status: pending | confirmed | cancelled
    private BigDecimal totalAmount;     // orders.total_amount
    private LocalDateTime createdAt;    // orders.created_at
    private LocalDateTime updatedAt;    // orders.updated_at

    // ── order_items ───────────────────────────────────────
    private List<OrderItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto implements Serializable {
        private Long productId;         // order_items.product_id
        private int quantity;           // order_items.quantity
        private BigDecimal unitPrice;   // order_items.unit_price
        private String productName;     // tiện ích hiển thị, không lưu DB
    }
}
