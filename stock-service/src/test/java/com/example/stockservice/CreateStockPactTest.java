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
import com.example.stockservice.event.model.published.StockCreatedEvent;
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
public class CreateStockPactTest {

    @Autowired
    private CreateStockMessageConsumer createStockMessageConsumer;

    @Autowired
    ReserveStockEventHandler listener;

    @Pact(consumer = "pactflow-create-stock-kafka")
    MessagePact createStockValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("stockName", "utoy");
        body.numberType("remainingStock", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-create-stock-kafka")
    MessagePact createStockInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("stockName1", "utoy");
        body.numberType("remainingStock", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-create-stock-kafka")
    MessagePact createStockWithoutStockName(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("remainingStock", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createStockValidMessage")
    void createStockValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            StockCreatedEvent stockCreatedEvent = createStockMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(stockCreatedEvent.getStockName(), jsonMessage.getString("stockName"), "stockName parameter is not equal between message and event object");
            assertEquals(stockCreatedEvent.getRemainingStock(), jsonMessage.getLong("remainingStock"), "remainingStock parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for create stock event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "createStockInvalidMessage")
    void createStockInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        try{
            createStockMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for create stock event");
        }catch (Exception exp){
        }
    }

    @Test
    @PactTestFor(pactMethod = "createStockWithoutStockName")
    void createStockNullStockNameShouldNotConsume(List<Message> messages) throws Exception {
        try{
            createStockMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null stock name for create stock event");
        }catch (Exception exp){
        }
    }


}
