package com.contract.messaging.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AmqpconsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmqpconsumerApplication.class, args);
	}
}
