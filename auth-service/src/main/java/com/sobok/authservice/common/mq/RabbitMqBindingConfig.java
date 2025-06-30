package com.sobok.authservice.common.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.sobok.authservice.common.util.Constants.*;

@Configuration
public class RabbitMqBindingConfig {
    // AMQP EXCHANGE, QUEUE, ROUTING_KEY 이름 Convention
    // EXCHANGE : (from Domain).exchange
    // QUEUE : (to Domain).queue
    // ROUTING_KEY : (from).(to).(task)

    //region Exchange

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(AUTH_EXCHANGE);
    }
    // endregion

    //region Queue
    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE);
    }

    @Bean
    public Queue deliveryQueue() {
        return new Queue(DELIVERY_QUEUE);
    }

    @Bean
    public Queue shopQueue() {
        return new Queue(SHOP_QUEUE);
    }
    //endregion

    //region Binding
    @Bean
    public Binding userSignUpBinding() {
        return BindingBuilder
                .bind(userQueue())
                .to(authExchange())
                .with(USER_SIGNUP_ROUTING_KEY);
    }

    @Bean
    public Binding riderSignUpBinding() {
        return BindingBuilder
                .bind(deliveryQueue())
                .to(authExchange())
                .with(RIDER_SIGNUP_ROUTING_KEY);
    }

    @Bean
    public Binding hubSignUpBinding() {
        return BindingBuilder
                .bind(shopQueue())
                .to(authExchange())
                .with(HUB_SIGNUP_ROUTING_KEY);
    }
    //endregion
}