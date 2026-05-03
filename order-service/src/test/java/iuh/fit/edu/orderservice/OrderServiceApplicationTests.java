package iuh.fit.edu.orderservice;

import iuh.fit.edu.orderservice.client.InventoryClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@SpringBootTest
class OrderServiceApplicationTests {

    @MockBean
    RedisTemplate<String, Object> redisTemplate;

    @MockBean
    RabbitTemplate rabbitTemplate;

    @MockBean
    InventoryClient inventoryClient;

    @Test
    void contextLoads() {
    }
}
