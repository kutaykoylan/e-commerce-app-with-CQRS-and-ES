package com.example.stockservice;

import com.example.stockservice.event.model.received.WaitingStockReleaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;


@Component
public class WaitingStockReleaseMessageConsumer {
    private ObjectMapper objectMapper;

    public WaitingStockReleaseMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WaitingStockReleaseEvent consumeStringMessage(String messageString) throws IOException {
        WaitingStockReleaseEvent message =
                objectMapper.readValue(messageString, WaitingStockReleaseEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<WaitingStockReleaseEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}