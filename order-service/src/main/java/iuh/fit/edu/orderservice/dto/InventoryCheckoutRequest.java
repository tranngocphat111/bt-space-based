package iuh.fit.edu.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dữ liệu gửi tới inventory-service POST /decrease để giảm tồn kho.
 *
 * Matches với:
 *   public class CheckoutRequest { private List<Item> items; }
 *   public class Item { private Long productId; private int quantity; }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckoutRequest {

    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long productId;
        private int quantity;
    }
}
