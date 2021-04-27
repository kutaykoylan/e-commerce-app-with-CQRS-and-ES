package com.example.stockservice;

import com.example.stockservice.event.model.published.StockNotEnoughEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class StockNotEnoughMessageConsumer {
    private ObjectMapper objectMapper;

    public StockNotEnoughMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public StockNotEnoughEvent consumeStringMessage(String messageString) throws IOException {
        StockNotEnoughEvent message =
                objectMapper.readValue(messageString, StockNotEnoughEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<StockNotEnoughEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
