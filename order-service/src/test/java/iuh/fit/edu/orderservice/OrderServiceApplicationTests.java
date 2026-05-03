package iuh.fit.edu.orderservice;

import iuh.fit.edu.orderservice.client.InventoryClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@SpringBootTest
class OrderServiceApplicationTests {

    @MockitoBean
    RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    RabbitTemplate rabbitTemplate;

    @MockitoBean
    InventoryClient inventoryClient;

    @Test
    void contextLoads() {
    }
}
