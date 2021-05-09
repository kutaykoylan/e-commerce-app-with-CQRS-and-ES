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
import com.example.paymentservice.consumers.PaymentReturnedMessageConsumer;
import com.example.paymentservice.event.model.published.PaymentReturnedEvent;
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
@PactTestFor(providerName = "pactflow-return-payment-kafka", providerType = ProviderType.ASYNCH)
public class ReturnPaymentPactTest {

    @Autowired
    private PaymentReturnedMessageConsumer paymentReturnedMessageConsumer;

    @Pact(consumer = "pactflow-return-payment-kafka")
    MessagePact returnPaymentValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderId", "1234352");
        body.numberType("amount", 50);


        return builder
                .expectsToReceive("return payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-return-payment-kafka")
    MessagePact returnPaymentInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("orderIdx", "1234352");
        body.numberType("amount", 50);


        return builder
                .expectsToReceive("return payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-return-payment-kafka")
    MessagePact returnPaymentWithoutOrderId(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.numberType("amount", 50);

        return builder
                .expectsToReceive("return payment message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "returnPaymentValidMessage")
    void returnPaymentValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            PaymentReturnedEvent paymentReturnedEvent = paymentReturnedMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);
            assertEquals(paymentReturnedEvent.getOrderId(), jsonMessage.getString("orderId"), "orderId parameter is not equal between message and event object");
            assertEquals(paymentReturnedEvent.getAmount(), jsonMessage.getLong("amount"), "amount parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for return payment event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "returnPaymentInvalidMessage")
    void returnPaymentInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {

        assertThrows(ConstraintViolationException.class,
                ()->{
                    paymentReturnedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
                });

    }

    @Test
    @PactTestFor(pactMethod = "returnPaymentWithoutOrderId")
    void returnPaymentNullStockIdShouldNotConsume(List<Message> messages) throws Exception {
        assertThrows(ConstraintViolationException.class,
                ()->{
                    paymentReturnedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
                });
    }


}
