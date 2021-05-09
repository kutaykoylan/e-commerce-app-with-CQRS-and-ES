package com.example.stockservice.event.model.published;

import com.kloia.eventapis.common.EventType;
import com.kloia.eventapis.common.PublishedEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockAddedEvent extends PublishedEvent {

    @NotNull
    @Min(1)
    private long addedStock;

    @NotNull
    private EventType eventType = EventType.OP_SINGLE;

    public long getAddedStock() {
        return addedStock;
    }

    @NotNull
    @Override
    public EventType getEventType() {
        return eventType;
    }
}
