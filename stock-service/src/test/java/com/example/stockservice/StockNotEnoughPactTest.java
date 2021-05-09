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
import com.example.stockservice.event.model.published.StockNotEnoughEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.json.*;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@PactFolder("../pact-message-consumer/target/pacts")
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@PactTestFor(providerName = "pactflow-not-enough-stock-kafka", providerType = ProviderType.ASYNCH)
public class StockNotEnoughPactTest {

    @Autowired
    private StockNotEnoughMessageConsumer notEnoughMessageConsumer;


    @Pact(consumer = "pactflow-not-enough-stock-kafka")
    MessagePact notEnoughStockValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId", "test");
        body.numberType("numberOfItemsSold", 100);
        body.stringType("eventType", "OP_SUCCESS");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-not-enough-stock-kafka")
    MessagePact notEnoughStockInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId1", "utoy");
        body.numberType("numberOfItemsSold", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-not-enough-stock-kafka")
    MessagePact notEnoughStockWithoutOrderId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("numberOfItemsSold", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "notEnoughStockValidMessage")
    void notEnoughStockValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            StockNotEnoughEvent stockCreatedEvent = notEnoughMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(stockCreatedEvent.getNumberOfItemsSold(), jsonMessage.getLong("numberOfItemsSold"), "numberOfItemsSold parameter is not equal between message and event object");
            assertEquals(stockCreatedEvent.getOrderId(), jsonMessage.getString("orderId"), "orderId parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for create stock event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "notEnoughStockInvalidMessage")
    void notEnoughStockInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    notEnoughMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
        });
    }

    @Test
    @PactTestFor(pactMethod = "notEnoughStockWithoutOrderId")
    void notEnoughStockNullOrderIdShouldNotConsume(List<Message> messages) throws Exception {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    notEnoughMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
        });
    }


}