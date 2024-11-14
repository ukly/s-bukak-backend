package com.sbukak.domain.team.dto;

import java.util.List;
import java.util.Map;

public record GetTeamResponseDto(
    Map<String, Map<String, Team>> sports
) {
    public record Team(
        String name,
        String logoUrl,
        String collageName,
        TeamRank teamRank,
        List<Match> recentMatches,
        List<UpcomingMatch> upcomingMatches
    ) {
        public record TeamRank(
            String year,
            String category,
            int rank
        ) {}

        public record Match(
            String opponent,
            String opponentName,
            String score,
            String league,
            String round,
            String date
        ) {}

        public record UpcomingMatch(
            String date,
            String opponent,
            String opponentName,
            String result
        ) {}
    }
}
