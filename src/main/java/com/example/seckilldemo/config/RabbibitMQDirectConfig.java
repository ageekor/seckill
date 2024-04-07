package com.example.seckilldemo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbibitMQDirectConfig {
    private static final String QUEUE01 = "queue_direct01";
    private static final String QUEUE02 = "queue_direct02";
    private static final String EXCHANGE = "directExchange";
    private static final String ROUTINGKEY01 = "queue.red";
    private static final String ROUTINGKEY02 = "queue.green";

    @Bean
    public Queue queue03() {
        return new Queue(QUEUE01);
    }

    @Bean
    public Queue queue04() {
        return new Queue(QUEUE02);
    }
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }
    @Bean
    public Binding binging01() {
        return BindingBuilder.bind(queue03()).to(directExchange()).with(ROUTINGKEY01);
    }
    @Bean
    public Binding binging02() {
        return BindingBuilder.bind(queue04()).to(directExchange()).with(ROUTINGKEY02);
    }

}
