package com.example.stockservice.event.model.published;

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
public class StockCreatedEvent extends PublishedEvent {

    @NotNull
    private String stockName;

    @NotNull
    private long remainingStock;

    private EventType eventType = EventType.OP_SINGLE;


    public String getStockName() {
        return stockName;
    }

    public long getRemainingStock() {
        return remainingStock;
    }

    public EventType getEventType() {
        return eventType;
    }
}
