package com.contract.messaging.producer.rest;

import com.contract.messaging.producer.amqp.ResourceCreated;
import com.contract.messaging.producer.amqp.ResourceGateway;
import com.contract.messaging.producer.amqp.ResourceUpdated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RequestMapping(path = "/create")
@RestController
public class Controller {

    @Autowired
    private final ResourceGateway resourceGateway;

    @Autowired
    public Controller(ResourceGateway resourceGateway) {
        this.resourceGateway = resourceGateway;
    }

    @GetMapping
    public void triggerCreate(){
        ResourceCreated resourceCreated = new ResourceCreated(false, "created via REST-controller");
        resourceGateway.sendResourceCreated(resourceCreated);
    }

    @PutMapping
    public void triggerUpdate(){
        ResourceUpdated resourceUpdated = new ResourceUpdated(1L, true, "created via REST-controller");
        resourceGateway.sendResourceUpdated(resourceUpdated);
    }
}
