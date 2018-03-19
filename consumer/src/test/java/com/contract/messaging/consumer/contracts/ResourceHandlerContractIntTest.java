package com.contract.messaging.consumer.contracts;

import com.contract.messaging.consumer.AmqpconsumerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmqpconsumerApplication.class)
@AutoConfigureStubRunner(ids = "com.contract.messaging:producer:+:stubs", workOffline = true)
public class ResourceHandlerContractIntTest {

    @Autowired
    private StubFinder stubFinder;

    @Test
    public void resourceCreated(){
        stubFinder.trigger("resource.created");
    }

    @Test
    public void resourceUpdated(){
        stubFinder.trigger("resource.updated");
    }
}
