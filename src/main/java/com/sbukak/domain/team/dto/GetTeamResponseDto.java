package com.sbukak.domain.team.dto;

import com.sbukak.domain.schedule.dto.ScheduleDto;

import java.util.List;

public record GetTeamResponseDto(
    TeamDto teams,
    List<TeamScheduleByYearDto> schedulesYear
) {
    public record TeamScheduleByYearDto(
        int year,
        List<ScheduleDto> schedules
    ) {
    }
}
