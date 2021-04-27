package com.example.orderservice.consumers;

import com.example.orderservice.event.model.published.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class OrderCreatedMessageConsumer {

    private ObjectMapper objectMapper;

    public OrderCreatedMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderCreatedEvent consumeStringMessage(String messageString) throws IOException {
        OrderCreatedEvent message =
                objectMapper.readValue(messageString, OrderCreatedEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<OrderCreatedEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
