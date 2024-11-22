package com.sbukak.domain.schedule.dto;

import com.sbukak.domain.schedule.enums.LeagueType;
import com.sbukak.global.enums.SportType;

public record ScheduleRequestDto(
    SportType sportType,
    LeagueType leagueType,
    CreateScheduleTeamRequestDto homeTeam,
    CreateScheduleTeamRequestDto awayTeam,
    String startDate,
    String startTime,
    String place
) {
    public record CreateScheduleTeamRequestDto(
        String collegeName,
        String teamName
    ) {
    }
}
