package com.example.orderservice;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.example.orderservice.consumers.OrderCreatedMessageConsumer;
import com.example.orderservice.event.model.published.OrderCreatedEvent;
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
@PactTestFor(providerName = "pactflow-create-order-kafka", providerType = ProviderType.ASYNCH)
public class CreateOrderPactTest {

    @Autowired
    private OrderCreatedMessageConsumer createOrderMessageConsumer;

    @Pact(consumer = "pactflow-create-order-kafka")
    MessagePact createOrderValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("stockId", "utoy");
        body.numberType("orderAmount", 50);
        body.stringType("description", "test description");
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("create order message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-create-order-kafka")
    MessagePact createOrderInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("stockIdx", "utoy");
        body.numberType("orderAmount", 50);
        body.stringType("description", "test description");
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("create order message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-create-order-kafka")
    MessagePact createOrderWithoutStockId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("orderAmount", 50);
        body.stringType("description", "test description");
        body.stringType("eventType", "OP_SINGLE");


        return builder
                .expectsToReceive("create order message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createOrderValidMessage")
    void createOrderValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            OrderCreatedEvent orderCreatedEvent = createOrderMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(orderCreatedEvent.getStockId(), jsonMessage.getString("stockId"), "stockId parameter is not equal between message and event object");
            assertEquals(orderCreatedEvent.getOrderAmount(), jsonMessage.getLong("orderAmount"), "orderAmount parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for create order event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "createOrderInvalidMessage")
    void createOrderInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        try{
            createOrderMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for create order event");
        }catch (Exception exp){
        }
    }

    @Test
    @PactTestFor(pactMethod = "createOrderWithoutStockId")
    void createOrderNullStockIdShouldNotConsume(List<Message> messages) throws Exception {
        try{
            createOrderMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null order name for create order event");
        }catch (Exception exp){
        }
    }


}
