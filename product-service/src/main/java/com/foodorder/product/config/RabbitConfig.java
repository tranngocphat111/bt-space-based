package com.foodorder.product.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Configures queues, exchanges, and bindings for Persistence Service communication
 */
@Configuration
public class RabbitConfig {

    private final AppProperties appProperties;

    public RabbitConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    // Request Queue (Product Service -> Persistence Service)
    @Bean
    public Queue requestQueue() {
        return QueueBuilder.durable(appProperties.getPersistence().getRequestQueue())
                .build();
    }

    // Reply Queue (Persistence Service -> Product Service)
    @Bean
    public Queue replyQueue() {
        return QueueBuilder.durable(appProperties.getPersistence().getReplyQueue())
                .build();
    }

    // Exchange
    @Bean
    public TopicExchange persistenceExchange() {
        return new TopicExchange(appProperties.getPersistence().getExchange(), true, false);
    }

    // Binding for request queue
    @Bean
    public Binding requestBinding(Queue requestQueue, TopicExchange persistenceExchange) {
        return BindingBuilder.bind(requestQueue)
                .to(persistenceExchange)
                .with(appProperties.getPersistence().getRoutingKey().getListProducts() + ".#");
    }

    // Binding for reply queue
    @Bean
    public Binding replyBinding(Queue replyQueue, TopicExchange persistenceExchange) {
        return BindingBuilder.bind(replyQueue)
                .to(persistenceExchange)
                .with(appProperties.getPersistence().getRoutingKey().getProductById() + ".#");
    }

    // Message Converter for JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate for RPC-style communication
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setReceiveTimeout(appProperties.getRabbitmqMqRequestTimeoutMs());
        return template;
    }
}
