package com.upiq.research.calculation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodRange {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static PeriodRange of(LocalDateTime startDate, LocalDateTime endDate) {
        return PeriodRange.builder()
                .startDate(startDate)
                .endDate(normalizeEnd(endDate))
                .build();
    }

    public static PeriodRange of(LocalDate startDate, LocalDate endDate) {
        return PeriodRange.builder()
                .startDate(startDate != null ? startDate.atStartOfDay() : null)
                .endDate(endDate != null ? endDate.atTime(LocalTime.MAX) : null)
                .build();
    }

    public static PeriodRange fromIsoStrings(String startIso, String endIso) {
        LocalDate start = startIso != null && !startIso.isBlank() ? LocalDate.parse(startIso.substring(0, 10)) : null;
        LocalDate end = endIso != null && !endIso.isBlank() ? LocalDate.parse(endIso.substring(0, 10)) : null;
        return of(start, end);
    }

    private static LocalDateTime normalizeEnd(LocalDateTime end) {
        if (end != null && end.toLocalTime().equals(LocalTime.MIN)) {
            return end.with(LocalTime.MAX);
        }
        return end;
    }
}
