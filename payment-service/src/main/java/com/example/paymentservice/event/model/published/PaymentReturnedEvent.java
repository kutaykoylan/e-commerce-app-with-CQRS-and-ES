package com.example.paymentservice.event.model.published;

import com.kloia.eventapis.common.EventType;
import com.kloia.eventapis.common.PublishedEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Data
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentReturnedEvent extends PublishedEvent {

    @NotNull
    private String orderId;

    @NotNull
    private float amount;

    private EventType eventType = EventType.OP_SINGLE;

    public String getOrderId() {
        return orderId;
    }

    public float getAmount() {
        return amount;
    }

    public EventType getEventType() {
        return eventType;
    }
}
