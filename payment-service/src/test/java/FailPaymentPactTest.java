package com.example.paymentservice;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.example.paymentservice.consumers.PaymentFailedMessageConsumer;
import com.example.paymentservice.event.model.published.PaymentFailedEvent;
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
@PactTestFor(providerName = "pactflow-fail-payment-kafka", providerType = ProviderType.ASYNCH)
public class FailPaymentPactTest {

    @Autowired
    private PaymentFailedMessageConsumer paymentFailedMessageConsumer;

    @Pact(consumer = "pactflow-fail-payment-kafka")
    MessagePact failPaymentValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId", "123123");
        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("fail payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-fail-payment-kafka")
    MessagePact failPaymentInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderIdx", "123123");
        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("fail payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-fail-payment-kafka")
    MessagePact failPaymentWithoutOrderId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("fail payment message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "failPaymentValidMessage")
    void failPaymentValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            PaymentFailedEvent paymentFailedEvent = paymentFailedMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(paymentFailedEvent.getOrderId(), jsonMessage.getString("orderId"), "orderId parameter is not equal between message and event object");
            assertEquals(paymentFailedEvent.getAmount(), jsonMessage.getLong("amount"), "amount parameter is not equal between message and event object");
            assertEquals(paymentFailedEvent.getCardInformation(), jsonMessage.getString("cardInformation"), "cardInformation parameter is not equal between message and event object");
            assertEquals(paymentFailedEvent.getPaymentAddress(), jsonMessage.getString("paymentAddress"), "paymentAddress parameter is not equal between message and event object");

        }catch (Exception exp){
            fail("invalid kafka message for fail payment event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "failPaymentInvalidMessage")
    void failPaymentInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        try{
            paymentFailedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for fail payment event");
        }catch (Exception exp){
        }
    }

    @Test
    @PactTestFor(pactMethod = "failPaymentWithoutOrderId")
    void failPaymentNullStockIdShouldNotConsume(List<Message> messages) throws Exception {
        try{
            paymentFailedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null order id for fail payment event");
        }catch (Exception exp){
        }
    }


}
