package com.sbukak.domain.schedule.dto;

import java.util.List;

public record GetSchedulesResponseDto(
    List<ScheduleYearDto> schedulesYear
) {
    public record ScheduleYearDto(
        int year,
        List<ScheduleMonthDto> schedulesMonth
    ) {
        public record ScheduleMonthDto(
            int month,
            List<ScheduleDto> schedules
        ) {
        }
    }
}
