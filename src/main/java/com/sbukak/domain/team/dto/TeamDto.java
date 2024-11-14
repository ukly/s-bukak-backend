package com.sbukak.domain.team.dto;

import java.util.List;

public record TeamDto(
    Long id,
    int ranking,
    String leagueName,
    String collegeName,
    String teamName,
    int points,
    int wins,
    Integer draws,
    int losses,
    int matches,
    Integer goals,
    int goalsDifference,
    List<Integer> recentMatches,
    String iconImageUrl,
    String formationImageUrl,
    String nameEng
) {
}