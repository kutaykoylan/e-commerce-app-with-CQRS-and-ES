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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    MessagePact createPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();


        body.stringType("sender", "stock-service-group");
        body.integerType("opDate", 1232312312);
        body.stringType("userContext", null);

        body.object("event")
                .stringType("stockName", "utoy")
                .numberType("remainingStock", 100)
                .stringType("eventType", "OP_SINGLE")
                .closeObject();
        body.object("sender")
                .stringType("entityId", "ea83fffe-cebd-4858-83f8-f6285fbc1100")
                .numberType("version", 0)
                .closeObject();
        body.object("context")
                .stringType("opId", "e50e4146-45b6-4265-88b1-fb13fa0a6e4d")
                .stringType("parentOpId", null)
                .stringType("commandContext", "CreateStockCommandHandler")
                .numberType("commandTimeout", 10000)
                .numberType("startTime", 123123152)
                .closeObject();



        return builder
                .expectsToReceive("a user created message")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void test(List<Message> messages) throws Exception {
        createStockMessageConsumer.consumeStringMessage(messages.get(0).contentsAsString());
    }
}
