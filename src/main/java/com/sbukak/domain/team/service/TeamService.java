package com.sbukak.domain.team.service;

import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.dto.GetTeamResponseDto;
import com.sbukak.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public GetTeamResponseDto getTeam() {
        Long teamId = 1L;
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("team not found"));
        List<Schedule> schedules = scheduleRepository.findAllByTeam(team);
        return null;
    }

}
