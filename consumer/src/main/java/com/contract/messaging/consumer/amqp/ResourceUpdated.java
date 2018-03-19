package com.contract.messaging.consumer.amqp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class ResourceUpdated {

    @NotNull
    private final Long id;

    @NotNull
    private final Boolean active;

    @NotBlank
    private final String text;

    @JsonCreator
    public ResourceUpdated(@JsonProperty("id") Long id,
                           @JsonProperty("active") Boolean active,
                           @JsonProperty("text") String text) {
        this.id = id;
        this.active = active;
        this.text = text;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getId() {
        return id;
    }
}