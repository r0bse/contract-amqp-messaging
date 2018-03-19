package com.contract.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.Validator;

/**
 * Configuration for AMQP related Beans
 */
@Configuration
@EnableRabbit
public class AmqpConfiguration implements RabbitListenerConfigurer {


    private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    private static final String DEAD_LETTER_POSTFIX = ".dlx";

    @Value("${amqp.exchange}")
    private String resourceExchange;

    @Value("${amqp.resource.created.queue}")
    private String resourceCreatedQueue;

    @Value("${amqp.resource.created.routingkey}")
    private String resourceCreatedRoutingkey;

    @Value("${amqp.resource.updated.queue}")
    private String resourceUpdatedQueue;

    @Value("${amqp.resource.updated.routingkey}")
    private String resourceUpdatedRoutingkey;

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
        jackson2JsonMessageConverter.setCreateMessageIds(true);
//        jackson2JsonMessageConverter.setTypePrecedence(TypePrecedence.INFERRED);
        return jackson2JsonMessageConverter;
    }

    @Bean
    public TopicExchange resourceExchange() {
        return (TopicExchange) ExchangeBuilder.topicExchange(resourceExchange).durable(true).build();
    }

    @Bean
    public Binding resourceCreatedBinding() {
        return BindingBuilder.bind(resourceCreatedQueue()).to(resourceExchange())
            .with(resourceCreatedRoutingkey);
    }

    @Bean
    public Binding resourceUpdatedBinding() {
        return BindingBuilder.bind(resourceUpdatedQueue()).to(resourceExchange())
            .with(resourceUpdatedRoutingkey);
    }

    @Bean
    public Queue resourceCreatedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put(X_DEAD_LETTER_EXCHANGE, resourceExchange);
        args.put(X_DEAD_LETTER_ROUTING_KEY, resourceCreatedRoutingkey + DEAD_LETTER_POSTFIX);
        return QueueBuilder.durable(resourceCreatedQueue).withArguments(args).build();
    }
    @Bean
    public Queue resourceUpdatedQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put(X_DEAD_LETTER_EXCHANGE, resourceExchange);
        args.put(X_DEAD_LETTER_ROUTING_KEY, resourceUpdatedRoutingkey + DEAD_LETTER_POSTFIX);
        return QueueBuilder.durable(resourceUpdatedQueue).withArguments(args).build();
    }

//    @Bean
//    public Queue resourceCreatedDlxQueue() {
//        return QueueBuilder.durable(resourceCreatedQueue + DEAD_LETTER_POSTFIX).build();
//    }
//
//    @Bean
//    public Binding bindResourceCreatedQueueDlx() {
//        return BindingBuilder.bind(resourceCreatedDlxQueue()).to(resourceExchange())
//            .with(resourceCreatedRoutingkey + DEAD_LETTER_POSTFIX);
//    }
//
//    @Bean
//    public Queue resourceUpdatedDlxQueue() {
//        return QueueBuilder.durable(resourceUpdatedQueue + DEAD_LETTER_POSTFIX).build();
//    }
//
//    @Bean
//    public Binding bindResourceUpdatedQueueDlx() {
//        return BindingBuilder.bind(resourceUpdatedDlxQueue()).to(resourceExchange())
//            .with(resourceUpdatedRoutingkey + DEAD_LETTER_POSTFIX);
//    }

    /**
     * Override default Configuration of RabbitListeners to add validation
     * @param registrar
     */
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerFactory());
    }

    @Autowired
    private Validator validator;

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setValidator(validator);
        return factory;
    }
}
