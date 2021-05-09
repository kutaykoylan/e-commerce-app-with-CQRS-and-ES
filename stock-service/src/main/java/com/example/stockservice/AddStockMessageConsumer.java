package com.example.stockservice;

import com.example.stockservice.event.model.published.StockAddedEvent;
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
public class AddStockMessageConsumer {

    private ObjectMapper objectMapper;

    public AddStockMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public StockAddedEvent consumeStringMessage(String messageString) throws IOException {
        StockAddedEvent message =
                objectMapper.readValue(messageString, StockAddedEvent.class);


        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<StockAddedEvent>> violations = validator.validate(message);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
