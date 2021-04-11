package com.example.stockservice;

import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.example.stockservice.entity.Stock;
import com.example.stockservice.event.handler.ReserveStockEventHandler;
import com.example.stockservice.event.model.received.ReserveStockEvent;
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

@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@PactTestFor(providerName = "pactflow-example-provider-java-kafka", providerType = ProviderType.ASYNCH)
public class PactTest {

    @Autowired
    ReserveStockEventHandler listener;

    @Pact(consumer = "pactflow-example-consumer-java-kafka")
    MessagePact createPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringType("name", "product name");
        body.stringType("type", "product series");
        body.stringType("id", "5cc989d0-d800-434c-b4bb-b1268499e850");
        body.stringMatcher("version", "v[a-zA-z0-9]+", "v1");
        body.stringMatcher("event", "^(CREATED|UPDATED|DELETED)$", "CREATED");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");
        metadata.put("kafka_topic", "products");

        return builder.expectsToReceive("a product created event").withMetadata(metadata).withContent(body).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void test(List<Message> messages) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
       // System.out.println("Message received -> " + messages.get(0).toString());
        Stock stock = mapper.readValue(messages.get(0).toString(), Stock.class);
        ReserveStockEvent reserveStockEvent= ReserveStockEvent.builder()
                .numberOfItemsSold(1)
                .stockId("1")
                .build();
        assertDoesNotThrow(() -> {
            listener.execute(reserveStockEvent);
        });
    }
}