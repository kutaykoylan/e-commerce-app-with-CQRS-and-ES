package com.example.stockservice.event.model.received;

import com.kloia.eventapis.common.ReceivedEvent;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingStockReleaseEvent extends ReceivedEvent {

    @NotNull
    private String stockId;

    @NotNull
    private int reservedStockVersion;

    public String getStockId() {
        return stockId;
    }

    public int getReservedStockVersion() {
        return reservedStockVersion;
    }
}
