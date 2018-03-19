package com.contract.messaging.producer.contracts;

import com.contract.messaging.producer.AmqpproducerApplication;
import com.contract.messaging.producer.amqp.ResourceCreated;
import com.contract.messaging.producer.amqp.ResourceGateway;
import com.contract.messaging.producer.amqp.ResourceUpdated;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmqpproducerApplication.class)
@AutoConfigureMessageVerifier
public abstract class ContractVerifierBase {

    @Autowired
    private ResourceGateway resourceGateway;

    protected void triggerResourceCreated() {
        ResourceCreated resourceCreated = new ResourceCreated(false, "blubb");
        resourceGateway.sendResourceCreated(resourceCreated);
    }

    protected void triggerResourceUpdated() {
        ResourceUpdated resourceUpdated = new ResourceUpdated(1L, false, "blubb");
        resourceGateway.sendResourceUpdated(resourceUpdated);
    }
}
