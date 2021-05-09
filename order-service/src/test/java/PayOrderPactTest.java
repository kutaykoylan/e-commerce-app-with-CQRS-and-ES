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
import com.example.orderservice.consumers.OrderPaidMessageConsumer;
import com.example.orderservice.event.model.published.OrderPaidEvent;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@PactFolder("../pact-message-consumer/target/pacts")
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@PactTestFor(providerName = "pactflow-create-order-kafka", providerType = ProviderType.ASYNCH)
public class PayOrderPactTest {

    @Autowired
    private OrderPaidMessageConsumer orderPaidMessageConsumer;

    @Pact(consumer = "pactflow-create-order-kafka")
    MessagePact payOrderValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("paymentId", "121312");
        body.stringType("eventType", "OP_SUCCESS");


        return builder
                .expectsToReceive("order paid message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-create-order-kafka")
    MessagePact payOrderInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("paymentId1", "utoy");
        body.stringType("eventType", "OP_SUCCESS");


        return builder
                .expectsToReceive("order paid message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-create-order-kafka")
    MessagePact payOrderWithoutPaymentId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("eventType", "OP_SUCCESS");


        return builder
                .expectsToReceive("order paid message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "payOrderValidMessage")
    void payOrderValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            OrderPaidEvent orderPaidEvent = orderPaidMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(orderPaidEvent.getPaymentId(), jsonMessage.getString("paymentId"), "paymentId parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for order paid message");
        }
    }

    @Test
    @PactTestFor(pactMethod = "payOrderInvalidMessage")
    void payOrderInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    orderPaidMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
                });
    }

    @Test
    @PactTestFor(pactMethod = "payOrderWithoutPaymentId")
    void payOrderNullOrderNameShouldNotConsume(List<Message> messages) throws Exception {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    orderPaidMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
                });
    }


}
