package com.sbukak.domain.team.dto;

public record TeamDto(
    Long id,
    int ranking,
    String leagueName,
    String collegeName,
    String teamName,
    int points,
    int wins,
    int draws,
    int losses,
    int matches,
    int goals,
    int goalsDifference,
    String recentMatches,
    String iconImageUrl,
    String formationImageUrl
) {
}