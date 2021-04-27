package com.example.paymentservice.consumers;

import com.example.paymentservice.event.model.published.PaymentFailedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class PaymentFailedMessageConsumer {

    private ObjectMapper objectMapper;

    public PaymentFailedMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PaymentFailedEvent consumeStringMessage(String messageString) throws IOException {
        PaymentFailedEvent message =
                objectMapper.readValue(messageString, PaymentFailedEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<PaymentFailedEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
