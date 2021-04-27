package com.example.paymentservice.consumers;

import com.example.paymentservice.event.model.published.PaymentSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Component
public class PaymentSucceededMessageConsumer {

    private ObjectMapper objectMapper;

    public PaymentSucceededMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PaymentSuccessEvent consumeStringMessage(String messageString) throws IOException {
        PaymentSuccessEvent message =
                objectMapper.readValue(messageString, PaymentSuccessEvent.class);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<PaymentSuccessEvent>> violations = validator.validate(message);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }




        return message;
    }
}
