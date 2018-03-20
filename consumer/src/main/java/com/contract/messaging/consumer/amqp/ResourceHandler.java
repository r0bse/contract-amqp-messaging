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


    @RabbitListener(id="resources.created", queues = "${amqp.resource.created.queue}")
    public void handleResourceCreated(@Payload @Valid ResourceCreated resource) {

        log.info("Incoming event {}", resource);
    }

    @RabbitListener(id="resources.updated", queues = "${amqp.resource.updated.queue}")
    public void handleResourceUpdated(@Payload @Valid ResourceUpdated resource) {

        log.info("Incoming event {}", resource);
    }
}