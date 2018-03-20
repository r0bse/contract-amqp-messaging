import org.springframework.cloud.contract.spec.Contract

Contract.make {
    label 'resource.created'

    input {
        triggeredBy('triggerResourceCreated()')
    }
    outputMessage {
        sentTo 'resources'
        headers {
            messagingContentType(applicationJson())
            header("amqp_receivedRoutingKey":"resource.created")
        }
        body([
                active: $(consumer(true), producer(regex(anyBoolean()))),
                text  : $(consumer("some informative text"), producer(regex(".+"))),
        ])
    }
}
