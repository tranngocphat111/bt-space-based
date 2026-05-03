package iuh.fit.edu.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Mirror của CartResponse trong cart-service.
 * Order-service đọc object này từ Redis (key: "cart:{userId}").
 *
 * PHẢI khớp chính xác với cart.service.demo.dto.CartResponse:
 *   - userId        : String
 *   - items         : List<CartItemDto>  (mirror CartItemResponse)
 *   - totalQuantity : int
 *   - totalAmount   : long
 *
 * cart-service lưu với key: "cart:{userId}"
 * → order-service dùng field userId (không phải sessionId)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Serializable {

    /** Khớp với CartResponse.userId */
    private String userId;

    /** Khớp với CartResponse.items (List<CartItemResponse>) */
    private List<CartItemDto> items = new ArrayList<>();

    /** Khớp với CartResponse.totalQuantity */
    private int totalQuantity;

    /** Khớp với CartResponse.totalAmount */
    private long totalAmount;

    /**
     * Mirror của CartItemResponse trong cart-service.
     * Các field và kiểu dữ liệu phải khớp chính xác:
     *   productId   : String  (cart dùng String, không phải Long)
     *   productName : String
     *   price       : long    (cart dùng long, không phải BigDecimal)
     *   quantity    : int
     *   subtotal    : long
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDto implements Serializable {
        private String productId;    // String, khớp CartItemResponse.productId
        private String productName;
        private long price;          // long, khớp CartItemResponse.price
        private int quantity;
        private long subtotal;       // long, khớp CartItemResponse.subtotal
    }
}
