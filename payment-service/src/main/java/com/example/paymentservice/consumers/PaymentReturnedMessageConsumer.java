package com.example.paymentservice.consumers;

import com.example.paymentservice.event.model.published.PaymentReturnedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class PaymentReturnedMessageConsumer {

    private ObjectMapper objectMapper;

    public PaymentReturnedMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PaymentReturnedEvent consumeStringMessage(String messageString) throws IOException {
        PaymentReturnedEvent message =
                objectMapper.readValue(messageString, PaymentReturnedEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<PaymentReturnedEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
