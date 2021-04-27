package com.example.stockservice.event.model.published;

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
public class StockReleasedEvent extends PublishedEvent {

    @NotNull
    private String orderId;

    @NotNull
    private long numberOfItemsReleased;

    private EventType eventType = EventType.EVENT;

    public String getOrderId() {
        return orderId;
    }

    public long getNumberOfItemsReleased() {
        return numberOfItemsReleased;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
}
