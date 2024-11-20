package com.sbukak.domain.team.dto;

import com.sbukak.domain.team.domain.Player;

import java.util.List;

public record GetTeamResponseDto(
    Long teamId,
    String name,
    String logoUrl,
    String collageName,
    TeamRank teamRank,
    List<Match> recentMatches,
    List<UpcomingMatch> upcomingMatches,
    List<Player> players,
    boolean canUpdatePlayers
) {
    public record TeamRank(
        String year,
        String category,
        int rank
    ) {
    }

    public record Match(
        String opponent,
        String opponentLogoUrl,
        String score,
        String league,
        String round,
        String date
    ) {
    }

    public record UpcomingMatch(
        String date,
        String opponent,
        String opponentName,
        String result
    ) {
    }
}
