package com.sbukak.domain.ranking.service;

import com.sbukak.domain.team.dto.GetTeamResponseDto;
import com.sbukak.domain.team.dto.TeamDto;
import com.sbukak.global.enums.SportType;
import com.sbukak.domain.ranking.dto.GetRankingResponseDto;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public GetRankingResponseDto getRanking(SportType sportType) {
        Map<String, List<TeamDto>> teamsByLeague = teamRepository.findAllBySportType(sportType).stream()
            .map(Team::toTeamDto)
            .sorted(Comparator.comparing(TeamDto::ranking))
            .collect(Collectors.groupingBy(TeamDto::leagueName));

        List<GetRankingResponseDto.GetRankingResponseLeague> leagues = teamsByLeague.entrySet().stream()
            .map(entry -> new GetRankingResponseDto.GetRankingResponseLeague(entry.getKey(), entry.getValue()))
            .toList();

        return new GetRankingResponseDto(leagues);
    }
}
