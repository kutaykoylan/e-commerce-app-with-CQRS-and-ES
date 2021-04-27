package com.example.paymentservice.event.model.received;

import com.kloia.eventapis.common.ReceivedEvent;
import com.example.paymentservice.entity.PaymentInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessEvent extends ReceivedEvent {

    public PaymentInformation getPaymentInformation() {
        return paymentInformation;
    }
    @NotNull
    private PaymentInformation paymentInformation;
}
