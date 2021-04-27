package com.example.paymentservice.event.model.published;

import com.fasterxml.jackson.annotation.JsonView;
import com.kloia.eventapis.api.Views;
import com.kloia.eventapis.common.EventType;
import com.kloia.eventapis.common.PublishedEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Data
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSuccessEvent extends PublishedEvent {

    @NotNull
    private String orderId;

    @NotNull
    private String paymentAddress;

    @NotNull
    private float amount;

    @NotNull
    @JsonView(Views.RecordedOnly.class)
    private String cardInformation;

    private EventType eventType = EventType.EVENT;


    public String getOrderId() {
        return orderId;
    }

    public String getPaymentAddress() {
        return paymentAddress;
    }

    public float getAmount() {
        return amount;
    }

    public String getCardInformation() {
        return cardInformation;
    }

    public EventType getEventType() {
        return eventType;
    }
}
