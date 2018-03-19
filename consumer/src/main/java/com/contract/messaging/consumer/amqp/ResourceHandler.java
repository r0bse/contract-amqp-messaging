package com.contract.messaging.consumer.amqp;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);


    @RabbitListener(id="created", queues = "${amqp.resource.created.queue}")
//    @RabbitListener(bindings = @QueueBinding(
//        value = @Queue(value = "${amqp.resource.created.queue}", durable = "true", ignoreDeclarationExceptions = "true"),
//        key = "${amqp.resource.created.routingkey}",
//        exchange = @Exchange(value = "${amqp.exchange}", type = ExchangeTypes.TOPIC, durable = "true")
//    )
//    )
    public void handleResourceCreated(@Payload @Valid ResourceCreated resource) {

        log.info("Incoming event {}", resource);
    }

    @RabbitListener(id="updated", queues = "${amqp.resource.updated.queue}")
//    @RabbitListener(bindings = @QueueBinding(
//        value = @Queue(value = "${amqp.resource.updated.queue}", durable = "true", ignoreDeclarationExceptions = "true"),
//        key = "${amqp.resource.updated.routingkey}",
//        exchange = @Exchange(value = "${amqp.exchange}", type = ExchangeTypes.TOPIC, durable = "true")))
    public void handleResourceUpdated(@Payload @Valid ResourceUpdated resource) {

        log.info("Incoming event {}", resource);
    }
}