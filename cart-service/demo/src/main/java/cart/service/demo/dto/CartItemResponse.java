package cart.service.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemResponse {
    private String productId;
    private String productName;
    private long price;
    private int quantity;
    private long subtotal;

    public CartItemResponse(String productId, String productName, long price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        refreshSubtotal();
    }

    public void addQuantity(int addedQuantity) {
        this.quantity += addedQuantity;
        refreshSubtotal();
    }

    public void refreshSubtotal() {
        this.subtotal = price * quantity;
    }

    public void setPrice(long price) {
        this.price = price;
        refreshSubtotal();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        refreshSubtotal();
    }
}
