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
import com.example.stockservice.event.model.received.WaitingStockReleaseEvent;
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
@PactTestFor(providerName = "pactflow-waiting-stock-kafka", providerType = ProviderType.ASYNCH)
public class WaitingStockReleasePactTest {

    @Autowired
    private WaitingStockReleaseMessageConsumer waitingStockReleaseMessageConsumer;


    @Pact(consumer = "pactflow-waiting-stock-kafka")
    MessagePact waitingReleasedStockValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("stockId", "123456");
        body.numberType("reservedStockVersion", 1);


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-waiting-stock-kafka")
    MessagePact waitingReleasedStockWithoutOrderIdStockInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId1", "utoy");
        body.numberType("numberOfItemsReleased", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-waiting-stock-kafka")
    MessagePact waitingReleasedStockWithoutOrderId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("numberOfItemsReleased", 100);
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "waitingReleasedStockValidMessage")
    void waitingReleasedStockValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            WaitingStockReleaseEvent waitingStockReleaseEvent = waitingStockReleaseMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(waitingStockReleaseEvent.getStockId(), jsonMessage.getString("stockId"), "stockId parameter is not equal between message and event object");
            assertEquals(waitingStockReleaseEvent.getReservedStockVersion(), jsonMessage.getInt("reservedStockVersion"), "reservedStockVersion parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for released stock event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "waitingReleasedStockWithoutOrderIdStockInvalidMessage")
    void waitingReleasedStockInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        try{
            waitingStockReleaseMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for released stock event");
        }catch (Exception exp){
        }
    }

    @Test
    @PactTestFor(pactMethod = "waitingReleasedStockWithoutOrderId")
    void waitingReleasedhStockNullOrderIdShouldNotConsume(List<Message> messages) throws Exception {
        try{
            waitingStockReleaseMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null order id for released stock event");
        }catch (Exception exp){
        }
    }


}