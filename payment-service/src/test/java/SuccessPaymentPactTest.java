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
import com.example.paymentservice.consumers.PaymentSucceededMessageConsumer;
import com.example.paymentservice.event.model.published.PaymentSuccessEvent;
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
@PactTestFor(providerName = "pactflow-success-payment-kafka", providerType = ProviderType.ASYNCH)
public class SuccessPaymentPactTest {

    @Autowired
    private PaymentSucceededMessageConsumer paymentSucceededMessageConsumer;

    @Pact(consumer = "pactflow-success-payment-kafka")
    MessagePact successPaymentValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId", "123123");
        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("success payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-success-payment-kafka")
    MessagePact successPaymentInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderIdx", "123123");
        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("success payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-success-payment-kafka")
    MessagePact successPaymentWithoutOrderId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("success payment message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "successPaymentValidMessage")
    void successPaymentValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            PaymentSuccessEvent paymentSuccessEvent = paymentSucceededMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(paymentSuccessEvent.getOrderId(), jsonMessage.getString("orderId"), "orderId parameter is not equal between message and event object");
            assertEquals(paymentSuccessEvent.getAmount(), jsonMessage.getLong("amount"), "amount parameter is not equal between message and event object");
            assertEquals(paymentSuccessEvent.getCardInformation(), jsonMessage.getString("cardInformation"), "cardInformation parameter is not equal between message and event object");
            assertEquals(paymentSuccessEvent.getPaymentAddress(), jsonMessage.getString("paymentAddress"), "paymentAddress parameter is not equal between message and event object");

        }catch (Exception exp){
            fail("invalid kafka message for success payment event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "successPaymentInvalidMessage")
    void successPaymentInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {
        try{
            paymentSucceededMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("invalid kafka message for success payment event");
        }catch (Exception exp){
        }
    }

    @Test
    @PactTestFor(pactMethod = "successPaymentWithoutOrderId")
    void successPaymentNullStockIdShouldNotConsume(List<Message> messages) throws Exception {
        try{
            paymentSucceededMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
            fail("null payment name for success payment event");
        }catch (Exception exp){
        }
    }


}
