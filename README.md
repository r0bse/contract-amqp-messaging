 
# Spring Cloud Contract - AMQP example

## Introduction

This project is an example of a Spring-Cloud-Contract Problem when trying to verify the communication between 2 Microservices which communicate via AMQP. 

The Producer-Service produces 2 types of messages to the **exchange** "*resources*".

* A Message ResourceCreated with the **routingkey** *resource.created* to the **queue** *resource.created*
* A Message ResourceUpdated with the **routingkey** *resource.updated* to the **queue** *resource.updated*

The consumer listenes on both queues, validates the message with JSR303 and provides a simple log message when a message is succesfully received.

When running both services, everything works as expected.

## Problem Description

When implementing Spring Cloud Contract the producer side passes.

However, the Consumer Tests are failing. The reason for that seems to be that the message which is sent to the exchange, triggers both RabbitListeners (since the routing key and the queue is ignored in the contract)
The expected behaviour would be that only one Listener is triggerd.

## Prerequisities

To be able to run the application you need:

* Docker and Docker-Compose installed
* local running Rabbit Docker Container

To be able to run the contractTest you need:

* a local Maven repository

## Start Application

```
cd docker
docker-compose up
```

```
cd producer
./gradlew bootRun
```
```
cd consumer
./gradlew bootRun
```