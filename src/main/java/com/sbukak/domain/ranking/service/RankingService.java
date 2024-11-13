package com.sbukak.domain.ranking.service;

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

@Service
@RequiredArgsConstructor
public class RankingService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public GetRankingResponseDto getRanking(SportType sportType) {
        List<TeamDto> teams = teamRepository.findAllBySportType(sportType).stream()
            .map(Team::toTeamDto)
            .sorted(Comparator.comparing(TeamDto::ranking))
            .toList();
        return new GetRankingResponseDto(teams);
    }
}
