package com.contract.messaging.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AmqpproducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmqpproducerApplication.class, args);
	}
}
