package com.example.orderservice.event.model.published;

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
public class OrderCreatedEvent extends PublishedEvent {

    @NotNull
    private String stockId;

    @NotNull
    private int orderAmount;

    private String description;

    @NotNull
    private EventType eventType = EventType.OP_SINGLE;

    public String getStockId() {
        return stockId;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public String getDescription() {
        return description;
    }

    public EventType getEventType() {
        return eventType;
    }
}
