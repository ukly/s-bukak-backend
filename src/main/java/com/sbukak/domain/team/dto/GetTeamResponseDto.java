package com.sbukak.domain.team.dto;

import com.sbukak.domain.schedule.dto.ScheduleDto;

import java.util.List;

public record GetTeamResponseDto(
    String teamName,
    String collegeName,
    String leagueName,
    String teamIconImageUrl,
    String formationImageUrl,
    List<ScheduleDto> schedules
) {
}
