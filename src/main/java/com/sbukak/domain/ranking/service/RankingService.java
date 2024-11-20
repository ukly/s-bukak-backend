package com.sbukak.domain.ranking.service;

import com.sbukak.domain.ranking.dto.GetRankingResponseDto;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.dto.TeamDto;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.global.enums.GameResultType;
import com.sbukak.global.enums.SportType;
import com.sbukak.global.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final TeamRepository teamRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public GetRankingResponseDto getRanking(SportType sportType) {
        List<Schedule> schedules = scheduleRepository.findAllBySportType(sportType);
        List<Team> teams = teamRepository.findAllBySportType(sportType);

        Map<Long, List<GameResultType>> recentResultsByTeam = teams.stream()
            .collect(Collectors.toMap(
                Team::getId,
                team -> getRecentGameResultsForTeam(team, schedules)
            ));

        Map<String, List<TeamDto>> teamsByLeague = teamRepository.findAllBySportType(sportType).stream()
            .map(team -> team.toTeamDto(recentResultsByTeam.getOrDefault(team.getId(), Collections.emptyList())))
            .sorted(Comparator.comparing(TeamDto::ranking))
            .collect(Collectors.groupingBy(TeamDto::leagueName));

        List<GetRankingResponseDto.GetRankingResponseLeague> leagues = teamsByLeague.entrySet().stream()
            .map(entry -> new GetRankingResponseDto.GetRankingResponseLeague(entry.getKey(), entry.getValue()))
            .toList();

        LocalDateTime lastUpdateDateTime =
            teams.stream().map(Team::getUpdateAt).max(Comparator.naturalOrder()).orElseGet(LocalDateTime::now);

        return new GetRankingResponseDto(leagues, Utils.dateTimeToFormat(lastUpdateDateTime));
    }

    private List<GameResultType> getRecentGameResultsForTeam(Team team, List<Schedule> schedules) {
        return schedules.stream()
            .filter(schedule -> schedule.getHomeTeam().equals(team) || schedule.getAwayTeam().equals(team))
            .sorted(Comparator.comparing(Schedule::getStartAt).reversed())
            .limit(7)
            .map(schedule -> getGameResultType(team, schedule))
            .collect(Collectors.toList());
    }

    private GameResultType getGameResultType(Team team, Schedule schedule) {
        boolean isHomeTeam = schedule.getHomeTeam().equals(team);
        int teamGoals = isHomeTeam ? schedule.getHomeTeamGoals() : schedule.getAwayTeamGoals();
        int opponentGoals = isHomeTeam ? schedule.getAwayTeamGoals() : schedule.getHomeTeamGoals();

        if (teamGoals > opponentGoals) {
            return GameResultType.W;
        } else if (teamGoals < opponentGoals) {
            return GameResultType.L;
        } else {
            return GameResultType.D;
        }
    }
}
