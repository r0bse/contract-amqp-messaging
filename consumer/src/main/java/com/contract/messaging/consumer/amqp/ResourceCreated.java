package com.contract.messaging.consumer.amqp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
public class ResourceCreated {

    @NotNull
    private final Boolean active;

    @NotBlank
    private final String text;

    @JsonCreator
    public ResourceCreated(@JsonProperty("active") Boolean active,
                           @JsonProperty("text") String text) {
        this.active = active;
        this.text = text;
    }

    public Boolean getActive() {
        return active;
    }

    public String getText() {
        return text;
    }
}