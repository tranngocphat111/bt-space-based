package iuh.fit.edu.orderservice.services;

import iuh.fit.edu.orderservice.dto.CheckoutRequest;
import iuh.fit.edu.orderservice.dto.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Lấy số lượng tồn kho từ Redis dựa trên productId.
     * Key: inventory:{productId}
     */
    public Integer getStock(Long productId) {
        String stockStr = redisTemplate.opsForValue().get("inventory:" + productId);
        return stockStr != null ? Integer.parseInt(stockStr) : 0;
    }

    /**
     * Giảm tồn kho cho một danh sách các sản phẩm.
     * Sử dụng Lua Script để đảm bảo tính nguyên tử (Atomic).
     * Nếu có bất kỳ sản phẩm nào không đủ tồn kho, toàn bộ giao dịch sẽ không thực hiện.
     */
    public boolean decreaseStock(CheckoutRequest request) {
        String luaScript = 
            "for i=1, #KEYS do " +
            "  local stock = redis.call('get', KEYS[i]) " +
            "  if not stock or tonumber(stock) < tonumber(ARGV[i]) then " +
            "    return 0 " +
            "  end " +
            "end " +
            "for i=1, #KEYS do " +
            "  redis.call('decrby', KEYS[i], ARGV[i]) " +
            "end " +
            "return 1";

        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();

        for (Item item : request.getItems()) {
            keys.add("inventory:" + item.getProductId());
            args.add(String.valueOf(item.getQuantity()));
        }

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long result = redisTemplate.execute(redisScript, keys, args.toArray());

        return result != null && result == 1L;
    }
}
