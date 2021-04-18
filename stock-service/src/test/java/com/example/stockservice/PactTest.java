package com.example.stockservice;

import au.com.dius.pact.core.model.annotations.PactFolder;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.example.stockservice.entity.Stock;
import com.example.stockservice.event.handler.ReserveStockEventHandler;
import com.example.stockservice.event.model.received.ReserveStockEvent;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
@PactFolder("../pact-message-consumer/target/pacts")
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@PactTestFor(providerName = "pactflow-example-provider-java-kafka", providerType = ProviderType.ASYNCH)
public class PactTest {


    @Autowired
    private MessageConsumer messageConsumer;

    @Autowired
    ReserveStockEventHandler listener;

    @Pact(consumer = "pactflow-example-consumer-java-kafka")
    MessagePact createPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringType("messageUuid");

        body.stringType("sender", "order-service-group");
        body.integerType("opDate", 1232312312);
        body.stringType("userContext", null);
        body.stringType("orderId", "order-service-group");

        body.object("event")
                .numberType("numberOfItemsSold", 42)
                .stringType("stockId", "sadas-1232sd-2362d-23321")
                .stringType("eventType", "OP_START")
                .closeObject();
        body.object("sender")
                .stringType("entityId", "sadas-1232sd-2362d-23321")
                .numberType("version", 1)
                .closeObject();
        body.object("context")
                .stringType("opId", "sadas-1232sd-2362d-23321")
                .stringType("parentOpId", null)
                .stringType("commandContext", "ProcessOrderCommandHandler")
                .numberType("commandTimeout", 10000)
                .numberType("startTime", 123123152)
                .closeObject();



        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void test(List<Message> messages) throws Exception {
        messageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
    }

}