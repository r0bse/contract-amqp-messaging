import org.springframework.cloud.contract.spec.Contract

Contract.make {
    label 'resource.updated'

    input {
        triggeredBy('triggerResourceUpdated()')
    }
    outputMessage {
        sentTo 'resources'
        headers {
            messagingContentType(applicationJson())
            header("amqp_receivedRoutingKey":"resource.updated") //entspricht dem routing key
        }
        body([
                id    : $(consumer("1"), producer(regex("\\d+"))),
                active: $(consumer(false), producer(regex(anyBoolean()))),
                text  : $(consumer("some updated text"), producer(regex(".+"))),
        ])
    }
}
