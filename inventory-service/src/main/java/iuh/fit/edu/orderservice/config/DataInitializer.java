package iuh.fit.edu.orderservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// @Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Bắt đầu nạp dữ liệu tồn kho mẫu vào Redis (Data Grid)...");

        // Dữ liệu mẫu dựa trên file sql.sql
        Map<String, String> initialStock = new HashMap<>();
        initialStock.put("inventory:1", "50");
        initialStock.put("inventory:2", "80");
        initialStock.put("inventory:3", "40");
        initialStock.put("inventory:4", "30");
        initialStock.put("inventory:5", "200");
        initialStock.put("inventory:6", "150");
        initialStock.put("inventory:7", "60");
        initialStock.put("inventory:8", "100");
        initialStock.put("inventory:9", "45");
        initialStock.put("inventory:10", "300");

        // Chỉ nạp nếu Key chưa tồn tại để tránh ghi đè dữ liệu đang test
        initialStock.forEach((key, value) -> {
            Boolean exists = redisTemplate.hasKey(key);
            if (Boolean.FALSE.equals(exists)) {
                redisTemplate.opsForValue().set(key, value);
                log.info("Đã nạp: {} = {}", key, value);
            }
        });

        log.info("Hoàn tất nạp dữ liệu lên Data Grid.");
    }
}
