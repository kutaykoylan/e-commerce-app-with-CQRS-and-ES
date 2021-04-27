package com.example.orderservice.consumers;

import com.example.orderservice.event.model.published.OrderPaidEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class OrderPaidMessageConsumer {

    private ObjectMapper objectMapper;

    public OrderPaidMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderPaidEvent consumeStringMessage(String messageString) throws IOException {
        OrderPaidEvent message =
                objectMapper.readValue(messageString, OrderPaidEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<OrderPaidEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
