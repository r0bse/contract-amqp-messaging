package com.contract.messaging.consumer.contracts.verifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * MessageListenerAccessor with RoutingKey awareness
 * <p>
 * <p>
 * Copy from {@link org.springframework.cloud.contract.verifier.messaging.amqp.MessageListenerAccessor}
 */
public class MessageListenerAccessor {

    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private final List<SimpleMessageListenerContainer> simpleMessageListenerContainers;
    private final List<Binding> bindings;

    MessageListenerAccessor(RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry,
                            List<SimpleMessageListenerContainer> simpleMessageListenerContainers, List<Binding> bindings) {
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.simpleMessageListenerContainers = simpleMessageListenerContainers;
        this.bindings = bindings;
    }

    List<SimpleMessageListenerContainer> getListenerContainersForDestination(String destination, String routingKey) {
        List<SimpleMessageListenerContainer> listenerContainers = collectListenerContainers();
        //we interpret the destination as exchange name and collect all the queues bound to this exchange
        Set<String> queueNames = collectQueuesBoundToDestination(destination, routingKey);
        return getListenersByBoundQueues(listenerContainers, queueNames);
    }

    private List<SimpleMessageListenerContainer> getListenersByBoundQueues(List<SimpleMessageListenerContainer> listenerContainers, Set<String> queueNames) {
        List<SimpleMessageListenerContainer> matchingContainers = new ArrayList<>();
        for (SimpleMessageListenerContainer listenerContainer : listenerContainers) {
            if (listenerContainer.getQueueNames() != null) {
                for (String queueName : listenerContainer.getQueueNames()) {
                    if (queueNames.contains(queueName)) {
                        matchingContainers.add(listenerContainer);
                        break;
                    }
                }
            }
        }
        return matchingContainers;
    }

    private Set<String> collectQueuesBoundToDestination(String destination, String routingKey) {
        Set<String> queueNames = new HashSet<>();
        for (Binding binding : this.bindings) {
            if (destination.equals(binding.getExchange())
                    && (routingKey == null || routingKey.equals(binding.getRoutingKey()))
                    && Binding.DestinationType.QUEUE.equals(binding.getDestinationType())
                    ) {
                queueNames.add(binding.getDestination());
            }
        }
        return queueNames;
    }

    private List<SimpleMessageListenerContainer> collectListenerContainers() {
        List<SimpleMessageListenerContainer> listenerContainers = new ArrayList<>();
        if (this.simpleMessageListenerContainers != null) {
            listenerContainers.addAll(this.simpleMessageListenerContainers);
        }
        if (this.rabbitListenerEndpointRegistry != null) {
            for (MessageListenerContainer listenerContainer : this.rabbitListenerEndpointRegistry.getListenerContainers()) {
                if (listenerContainer instanceof SimpleMessageListenerContainer) {
                    listenerContainers.add((SimpleMessageListenerContainer) listenerContainer);
                }
            }
        }
        return listenerContainers;
    }
}
