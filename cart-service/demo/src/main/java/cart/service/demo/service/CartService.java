package cart.service.demo.service;

import cart.service.demo.dto.AddCartRequest;
import cart.service.demo.dto.CartItemResponse;
import cart.service.demo.dto.CartResponse;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {
    private static final String CART_KEY_PREFIX = "cart:";

    private final Map<String, CartResponse> localCache = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;

    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public synchronized CartResponse addToCart(AddCartRequest request) {
        validateAddRequest(request);

        String userId = request.getUserId().trim();
        String productId = request.getProductId().trim();
        CartResponse cart = getCartFromCacheOrRedis(userId);

        Optional<CartItemResponse> existingItem = cart.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().addQuantity(request.getQuantity());
        } else {
            cart.getItems().add(new CartItemResponse(
                    productId,
                    request.getProductName(),
                    request.getPrice(),
                    request.getQuantity()
            ));
        }

        cart.refreshTotals();

        // Cart is stored in Redis Data Grid, not database, to follow Space-Based Architecture.
        localCache.put(userId, cart);
        redisTemplate.opsForValue().set(redisKey(userId), cart);

        return cart;
    }

    public CartResponse getCart(String userId) {
        validateUserId(userId);

        String normalizedUserId = userId.trim();
        CartResponse cart = localCache.get(normalizedUserId);
        if (cart != null) {
            return cart;
        }

        cart = readCartFromRedis(normalizedUserId);
        if (cart != null) {
            localCache.put(normalizedUserId, cart);
            return cart;
        }

        return new CartResponse(normalizedUserId);
    }

    private CartResponse getCartFromCacheOrRedis(String userId) {
        CartResponse cart = localCache.get(userId);
        if (cart != null) {
            return cart;
        }

        cart = readCartFromRedis(userId);
        return cart == null ? new CartResponse(userId) : cart;
    }

    private CartResponse readCartFromRedis(String userId) {
        Object value = redisTemplate.opsForValue().get(redisKey(userId));
        if (value instanceof CartResponse cartResponse) {
            cartResponse.refreshTotals();
            return cartResponse;
        }
        return null;
    }

    private void validateAddRequest(AddCartRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        validateUserId(request.getUserId());
        if (request.getProductId() == null || request.getProductId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }
        if (request.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be greater than 0");
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
    }

    private String redisKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }
}
