package iuh.fit.edu.orderservice.client;

import iuh.fit.edu.orderservice.dto.InventoryCheckoutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client giao tiếp với Inventory Processing Unit (PU4).
 *
 * Gọi POST /decrease để giảm tồn kho khi người dùng checkout.
 * url đọc từ application.properties: inventory.service.url
 */
@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient {

    /**
     * Giảm tồn kho cho danh sách sản phẩm.
     *
     * @param request danh sách { productId, quantity } cần giảm
     * @return ResponseEntity với status 200 nếu thành công
     */
    @PostMapping("/api/stock/decrease")
    ResponseEntity<String> decreaseStock(@RequestBody InventoryCheckoutRequest request);
}
