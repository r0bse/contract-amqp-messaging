package com.contract.messaging.consumer.contracts.verifier;

import static java.util.Collections.emptyList;

import java.util.List;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cloud contract does not support routingKeys
 * <p>
 * So we provide our own routingKey aware MessageVerifier and MessageListenerAccessor
 * <p>
 * <a href="https://github.com/spring-cloud/spring-cloud-contract/issues/585">Ticket for the issue</a>
 *
 * @see org.springframework.cloud.contract.verifier.messaging.amqp.ContractVerifierAmqpAutoConfiguration
 */
@Configuration
@ComponentScan
public class AmqpContractConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Autowired(required = false)
    private List<SimpleMessageListenerContainer> simpleMessageListenerContainers = emptyList();

    @Autowired(required = false)
    private List<Binding> bindings = emptyList();

    @Bean
    public MessageVerifier<Message> contractVerifierMessageExchange() {
        return new SpringAmqpStubMessages(
                this.rabbitTemplate,
                new MessageListenerAccessor(
                        this.rabbitListenerEndpointRegistry,
                        this.simpleMessageListenerContainers,
                        this.bindings
                )
        );
    }

}
