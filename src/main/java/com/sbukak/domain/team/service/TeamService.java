package com.sbukak.domain.team.service;

import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.dto.GetTeamResponseDto;
import com.sbukak.domain.team.dto.UpdateTeamPlayersRequestDto;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.jwt.JwtTokenProvider;
import com.sbukak.global.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Transactional(readOnly = true)
    public GetTeamResponseDto getTeam(Long teamId, String token) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("team not found"));
        List<Schedule> schedules = scheduleRepository.findAllByTeam(team);

        return createGetTeamResponse(team, schedules, token);
    }

    private GetTeamResponseDto createGetTeamResponse(Team team, List<Schedule> schedules, String token) {
        User user = userService.getUserOrNull(token);

        List<GetTeamResponseDto.Match> recentMatches = schedules.stream()
            .filter(Schedule::isScheduleFinished)
            .sorted((s1, s2) -> s2.getStartAt().compareTo(s1.getStartAt()))
            .limit(3)
            .map(this::convertToMatch)
            .collect(Collectors.toList());

        List<GetTeamResponseDto.UpcomingMatch> upcomingMatches = schedules.stream()
            .sorted((s1, s2) -> s2.getStartAt().compareTo(s1.getStartAt()))
            .limit(6)
            .map(this::convertToUpcomingMatch)
            .collect(Collectors.toList());

        GetTeamResponseDto.TeamRank teamRank = new GetTeamResponseDto.TeamRank("2024",
            team.getCollege().getLeague().getName(), team.getRanking());

        return new GetTeamResponseDto(
            team.getId(),
            team.getName(),
            team.getIconImageUrl(),
            team.getCollege().getName(),
            teamRank,
            recentMatches,
            upcomingMatches,
            team.getPlayers(),
            user != null && team.canUpdatePlayers(user)
        );
    }

    private GetTeamResponseDto.Match convertToMatch(Schedule schedule) {
        int matchYear = schedule.getStartAt().getYear();
        long matchRound = scheduleRepository.findAllByTeam(schedule.getHomeTeam()).stream()
            .filter(s -> s.getStartAt().getYear() == matchYear && s.getStartAt().isBefore(schedule.getStartAt()))
            .count() + 1;

        return new GetTeamResponseDto.Match(
            schedule.getAwayTeam().getName(),
            schedule.getAwayTeam().getIconImageUrl(),
            schedule.getHomeTeamGoals() + " - " + schedule.getAwayTeamGoals(),
            schedule.getLeagueType().name(),
            String.valueOf(matchRound),
            Utils.dateTimeToFormat(schedule.getStartAt())
        );
    }

    private GetTeamResponseDto.UpcomingMatch convertToUpcomingMatch(Schedule schedule) {
        String result = schedule.isScheduleFinished() ?
            schedule.getHomeTeamGoals() + " - " + schedule.getAwayTeamGoals() : null;
        return new GetTeamResponseDto.UpcomingMatch(
            Utils.dateTimeToFormat(schedule.getStartAt()),
            schedule.getAwayTeam().getName(),
            schedule.getAwayTeam().getIconImageUrl(),
            result
        );
    }

    @Transactional
    public void updateTeamPlayers(String token, Long teamId, UpdateTeamPlayersRequestDto requestDto) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("team not found"));
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
        team.setPlayers(requestDto.players(), user);
    }
}
