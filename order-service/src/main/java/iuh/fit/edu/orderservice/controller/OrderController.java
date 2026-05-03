package iuh.fit.edu.orderservice.controller;

import iuh.fit.edu.orderservice.dto.CheckoutRequest;
import iuh.fit.edu.orderservice.dto.OrderDto;
import iuh.fit.edu.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Order Processing Unit (PU3) – REST API.
 *
 * POST /checkout          – Đặt hàng: đọc cart từ Redis → giảm stock → lưu order → bắn event
 * GET  /orders/{orderId}  – Tra cứu order từ Redis (không query DB)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Checkout.
     * Body: { "sessionId": "sess_abc123" }
     */
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto checkout(@Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(request);
    }

    /**
     * Lấy order từ Data Grid (Redis).
     * orderId tương ứng với orders.id
     */
    @GetMapping("/orders/{orderId}")
    public OrderDto getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }
}