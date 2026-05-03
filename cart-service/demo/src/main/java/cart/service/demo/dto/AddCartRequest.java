package cart.service.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddCartRequest {
    private String userId;
    private String productId;
    private String productName;
    private long price;
    private int quantity;
}
