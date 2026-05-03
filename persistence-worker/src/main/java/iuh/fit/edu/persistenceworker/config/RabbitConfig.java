package iuh.fit.edu.persistenceworker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for Persistence Worker
 * Configures both 'mq' (checkout) and 'mq-read' (cache recovery) queues
 */
@Configuration
public class RabbitConfig {

    // Queue for write operations (checkout messages)
    @Bean
    public Queue checkoutQueue() {
        return QueueBuilder.durable("mq").build();
    }

    // Queue for read operations (cache miss recovery)
    @Bean
    public Queue cacheMissQueue() {
        return QueueBuilder.durable("mq-read").build();
    }

    // JSON Message Converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
