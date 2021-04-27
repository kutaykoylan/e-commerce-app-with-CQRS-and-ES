package com.example.stockservice;

import com.example.stockservice.event.model.published.StockNotEnoughEvent;
import com.example.stockservice.event.model.published.StockReleasedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class ReleasedStockMessageConsumer {

    private ObjectMapper objectMapper;

    public ReleasedStockMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public StockReleasedEvent consumeStringMessage(String messageString) throws IOException {
        StockReleasedEvent message =
                objectMapper.readValue(messageString, StockReleasedEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<StockReleasedEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
