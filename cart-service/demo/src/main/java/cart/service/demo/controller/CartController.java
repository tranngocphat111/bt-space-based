package cart.service.demo.controller;

import cart.service.demo.dto.AddCartRequest;
import cart.service.demo.dto.CartResponse;
import cart.service.demo.service.CartService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cart/add")
    public CartResponse addToCart(@RequestBody AddCartRequest request) {
        return cartService.addToCart(request);
    }

    @GetMapping("/cart")
    public CartResponse getCart(@RequestParam String userId) {
        return cartService.getCart(userId);
    }
}
