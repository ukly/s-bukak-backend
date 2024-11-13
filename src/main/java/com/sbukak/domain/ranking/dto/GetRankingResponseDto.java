package com.sbukak.domain.ranking.dto;

import com.sbukak.domain.team.dto.TeamDto;

import java.util.List;

public record GetRankingResponseDto(
    List<GetRankingResponseLeague> teams
) {
    public record GetRankingResponseLeague(
        String leagueName,
        List<TeamDto> teams
    ) {
    }

}
