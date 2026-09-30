package com.upiq.research.context;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.upiq.research.calculation.dto.PeriodRange;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@JsonPropertyOrder({"start", "end"})
public class PeriodWindow {

    LocalDate start;
    LocalDate end;

    public static PeriodWindow from(PeriodRange period) {
        if (period == null || period.getStartDate() == null || period.getEndDate() == null) {
            return null;
        }
        return PeriodWindow.builder()
                .start(period.getStartDate().toLocalDate())
                .end(period.getEndDate().toLocalDate())
                .build();
    }
}
