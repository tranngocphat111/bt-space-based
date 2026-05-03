package iuh.fit.edu.orderservice.controllers;

import iuh.fit.edu.orderservice.dto.CheckoutRequest;
import iuh.fit.edu.orderservice.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getStock(productId));
    }

    @PostMapping("/decrease")
    public ResponseEntity<String> decreaseStock(@RequestBody CheckoutRequest request) {
        boolean success = inventoryService.decreaseStock(request);
        if (success) {
            return ResponseEntity.ok("Thành công");
        } else {
            return ResponseEntity.badRequest().body("Thất bại: Không đủ tồn kho");
        }
    }
}
