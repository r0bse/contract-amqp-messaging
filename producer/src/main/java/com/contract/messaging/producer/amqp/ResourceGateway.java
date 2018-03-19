package com.contract.messaging.producer.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Simple Gateway using one Rabbit exchange to send events to.
 * Each event has it's own routingkey to be sent to.
 */
@Service
public class ResourceGateway {

    private static final Logger log = LoggerFactory.getLogger(ResourceGateway.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${amqp.exchange}")
    private String exchange;

    @Value("${amqp.resource.created.routingkey}")
    private String createdRoutingKey;

    @Value("${amqp.resource.updated.routingkey}")
    private String updatedRoutingKey;

    @Autowired
    public ResourceGateway(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendResourceCreated(ResourceCreated resourceCreated) {

        log.info("Sending {} to exchange {} using key {}", resourceCreated, exchange, createdRoutingKey);
        rabbitTemplate.convertAndSend(exchange, createdRoutingKey, resourceCreated);
    }

    public void sendResourceUpdated(ResourceUpdated resourceUpdated) {

        log.info("Sending {} to exchange {} using key {}", resourceUpdated, exchange, updatedRoutingKey);
        rabbitTemplate.convertAndSend(exchange, updatedRoutingKey, resourceUpdated);
    }
}
