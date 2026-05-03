package cart.service.demo.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponse {
    private String userId;
    private List<CartItemResponse> items = new ArrayList<>();
    private int totalQuantity;
    private long totalAmount;

    public CartResponse(String userId) {
        this.userId = userId;
    }

    public void refreshTotals() {
        int quantity = 0;
        long amount = 0;

        for (CartItemResponse item : items) {
            item.refreshSubtotal();
            quantity += item.getQuantity();
            amount += item.getSubtotal();
        }

        this.totalQuantity = quantity;
        this.totalAmount = amount;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }
}
