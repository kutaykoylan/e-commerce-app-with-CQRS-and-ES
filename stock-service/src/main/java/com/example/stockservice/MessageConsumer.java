package com.example.stockservice;

import com.example.stockservice.event.model.published.StockReservedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class MessageConsumer {

    private ObjectMapper objectMapper;

    public MessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void consumeStringMessage(String messageString) throws IOException {
        StockReservedEvent message =
                objectMapper.readValue(messageString, StockReservedEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<StockReservedEvent>> violations =
                validator.validate(message);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
        // pass message into business use case
    }

}
