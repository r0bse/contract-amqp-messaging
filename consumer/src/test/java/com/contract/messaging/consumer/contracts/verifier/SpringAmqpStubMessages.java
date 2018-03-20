package com.contract.messaging.consumer.contracts.verifier;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;
import static org.springframework.amqp.support.converter.DefaultClassMapper.DEFAULT_CLASSID_FIELD_NAME;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.util.Assert;

/**
 * MessageVerifier with RoutingKey awareness
 * <p>
 * <p>
 * Copy from {@link org.springframework.cloud.contract.verifier.messaging.amqp.SpringAmqpStubMessages}
 */
public class SpringAmqpStubMessages implements MessageVerifier<Message> {

    private static final Logger log = LoggerFactory
        .getLogger(org.springframework.cloud.contract.verifier.messaging.amqp.SpringAmqpStubMessages.class);

    private final RabbitTemplate rabbitTemplate;

    private final MessageListenerAccessor messageListenerAccessor;

    public SpringAmqpStubMessages(RabbitTemplate rabbitTemplate,
                                  MessageListenerAccessor messageListenerAccessor) {
        Assert.notNull(rabbitTemplate);
        Assert.isTrue(mockingDetails(rabbitTemplate).isSpy() || mockingDetails(rabbitTemplate)
            .isMock()); //we get send messages by capturing arguments on the spy
        this.rabbitTemplate = rabbitTemplate;
        this.messageListenerAccessor = messageListenerAccessor;
    }

    @Override
    public <T> void send(T payload,
                         Map<String, Object> headers,
                         String destination) {
        Message message = org.springframework.amqp.core.MessageBuilder
            .withBody(((String) payload).getBytes())
            .andProperties(
                MessagePropertiesBuilder.newInstance()
                    .setContentType((String) headers.get("contentType"))
                    .copyHeaders(headers).build())
            .build();
        if (headers.containsKey(DEFAULT_CLASSID_FIELD_NAME)) {
            message.getMessageProperties().setHeader(DEFAULT_CLASSID_FIELD_NAME,
                headers.get(DEFAULT_CLASSID_FIELD_NAME));
        }

        if (headers.containsKey(AmqpHeaders.RECEIVED_ROUTING_KEY)) {
            message.getMessageProperties()
                .setReceivedRoutingKey((String) headers.get(AmqpHeaders.RECEIVED_ROUTING_KEY));
        }
        send(message, destination);
    }

    @Override
    public void send(Message message,
                     String destination) {
        final String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        List<SimpleMessageListenerContainer> listenerContainers = this.messageListenerAccessor
            .getListenerContainersForDestination(destination, routingKey);

        if (listenerContainers.isEmpty()) {
            throw new IllegalStateException(
                "no listeners found for destination \"" + destination + "\" and routingKey \"" + routingKey + "\"");
        }
        for (SimpleMessageListenerContainer listenerContainer : listenerContainers) {
            MessageListener messageListener = (MessageListener) listenerContainer.getMessageListener();
            messageListener.onMessage(message);
        }
    }

    @Override
    public Message receive(String destination,
                           long timeout,
                           TimeUnit timeUnit) {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.rabbitTemplate, atLeastOnce()).send(eq(destination),
            routingKeyCaptor.capture(),
            messageCaptor.capture(),
            any(CorrelationData.class));

        if (messageCaptor.getAllValues().isEmpty()) {
            log.info("no messages found on destination {}", destination);
            return null;
        } else if (messageCaptor.getAllValues().size() > 1) {
            log.info("multiple messages found on destination {} returning last one - {}", destination);
        }

        final Message message = messageCaptor.getValue();
        if (!routingKeyCaptor.getValue().isEmpty()) {
            message.getMessageProperties().setReceivedRoutingKey(routingKeyCaptor.getValue());
        }
        return message;
    }

    @Override
    public Message receive(String destination) {
        return receive(destination, 5, TimeUnit.SECONDS);
    }


}
