package com.example.stockservice.event.model.published;

import com.kloia.eventapis.common.EventType;
import com.kloia.eventapis.common.PublishedEvent;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockAddedEvent extends PublishedEvent {
    @NotNull
    private long addedStock;
    @NotNull
    private EventType eventType = EventType.OP_SINGLE;

    public long getAddedStock() {
        return addedStock;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
}
