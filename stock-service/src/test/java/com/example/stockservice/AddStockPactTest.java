package com.example.stockservice;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.example.stockservice.event.handler.ReserveStockEventHandler;
import com.example.stockservice.event.model.published.StockAddedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.json.*;

import java.util.List;

@PactFolder("../pact-message-consumer/target/pacts")
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@PactTestFor(providerName = "pactflow-create-stock-kafka", providerType = ProviderType.ASYNCH)
public class AddStockPactTest {

    @Autowired
    private AddStockMessageConsumer addStockMessageConsumer;

    @Autowired
    ReserveStockEventHandler listener;

    @Pact(consumer = "pactflow-add-stock-kafka")
    MessagePact addStockValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("addedStock", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-add-stock-kafka")
    MessagePact addStockInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("addedStock2", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-add-stock-kafka")
    MessagePact addStockWithoutEventType(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("addedStock", 100);
        body.stringType("eventType1", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "addStockValidMessage")
    void createStockValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            StockAddedEvent stockAddEvent = addStockMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(stockAddEvent.getAddedStock(), jsonMessage.getLong("addedStock"), "stockName parameter is not equal between message and event object");
            //assertEquals(stockAddEvent.getRemainingStock(), jsonMessage.getLong("remainingStock"), "remainingStock parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for create stock event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "addStockInvalidMessage")
    void addStockNullAddedStocksShouldNotConsume(List<Message> messages) throws Exception {
        try {
            StockAddedEvent stockAddEvent = addStockMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for add stock event " + stockAddEvent.getAddedStock());
        } catch (Exception exp) {
        }
    }

    @Test
    @PactTestFor(pactMethod = "addStockWithoutEventType")
    void addStockNullStockNameShouldNotConsume(List<Message> messages) throws Exception {
        try {
            addStockMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null event type for add stock event");
        } catch (Exception exp) {
        }
    }
}

