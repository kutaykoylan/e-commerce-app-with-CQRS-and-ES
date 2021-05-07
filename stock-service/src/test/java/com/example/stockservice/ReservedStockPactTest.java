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
import com.example.stockservice.event.model.published.StockReleasedEvent;
import com.example.stockservice.event.model.published.StockReservedEvent;
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
@PactTestFor(providerName = "pactflow-not-enough-stock-kafka", providerType = ProviderType.ASYNCH)
public class ReservedStockPactTest {

    @Autowired
    private StockReservedMessageConsumer stockReservedMessageConsumer;


    @Pact(consumer = "pactflow-released-stock-kafka")
    MessagePact reservedStockValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId", "test");
        body.numberType("numberOfItemsSold", 100);
        body.stringType("eventType", "OP_SUCCESS");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-released-stock-kafka")
    MessagePact reservedStockWithoutOrderIdStockInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId1", "utoy");
        body.numberType("numberOfItemsReleased", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-released-stock-kafka")
    MessagePact reservedStockWithoutOrderId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("numberOfItemsReleased", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "reservedStockValidMessage")
    void reservedStockValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            StockReservedEvent reservedStockEvent = stockReservedMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(reservedStockEvent.getNumberOfItemsSold(), jsonMessage.getLong("numberOfItemsSold"), "numberOfItemsSold parameter is not equal between message and event object");
            assertEquals(reservedStockEvent.getOrderId(), jsonMessage.getString("orderId"), "orderId parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for released stock event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "reservedStockWithoutOrderIdStockInvalidMessage")
    void reservedStockInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        try{
            stockReservedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for released stock event");
        }catch (Exception exp){
        }
    }

    @Test
    @PactTestFor(pactMethod = "reservedStockWithoutOrderId")
    void reservedhStockNullOrderIdShouldNotConsume(List<Message> messages) throws Exception {
        try{
            stockReservedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null order id for released stock event");
        }catch (Exception exp){
        }
    }


}