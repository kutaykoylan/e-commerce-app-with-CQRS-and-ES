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
import com.example.paymentservice.consumers.PaymentProcessedMessageConsumer;
import com.example.paymentservice.entity.PaymentInformation;
import com.example.paymentservice.event.model.received.PaymentProcessEvent;
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
@PactTestFor(providerName = "pactflow-process-payment-kafka", providerType = ProviderType.ASYNCH)
public class ProcessPaymentPactTest {

    @Autowired
    private PaymentProcessedMessageConsumer paymentProcessedMessageConsumer;

    @Pact(consumer = "pactflow-process-payment-kafka")
    MessagePact processPaymentValidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("paymentInformation", "test information");
        body.numberType("amount", 50);
        body.stringType("cardInformation", "1213-1232-1231-6353");
        body.stringType("paymentAddress", "paymentAddress");


        return builder
                .expectsToReceive("process payment message")
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "pactflow-process-payment-kafka")
    MessagePact processPaymentInvalidMessage(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();

        body.stringType("paymentInformation1", "test description");

        return builder
                .expectsToReceive("process payment message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "processPaymentValidMessage")
    void processPaymentValidAttributesShouldConsume(List<Message> messages) throws Exception {
        try{
            String jsonStringMessage =  messages.get(0).contentsAsString();
            PaymentProcessEvent paymentProcessEvent = paymentProcessedMessageConsumer.consumeStringMessage(jsonStringMessage);
            JSONObject jsonMessage = new JSONObject(jsonStringMessage);

            PaymentInformation pI = new PaymentInformation(jsonMessage.getString("paymentAddress"), jsonMessage.getLong("amount"), jsonMessage.getString("cardInformation"));

            assertEquals(paymentProcessEvent.getPaymentInformation(), pI, "stockId parameter is not equal between message and event object");
        }catch (Exception exp){
            fail("invalid kafka message for process payment event");
        }
    }

    @Test
    @PactTestFor(pactMethod = "processPaymentInvalidMessage")
    void processPaymentInvalidAttributesShouldNotConsume(List<Message> messages) throws Exception {

        assertThrows(ConstraintViolationException.class,
                ()->{
                    paymentProcessedMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
                });

    }


}