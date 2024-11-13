package com.sbukak.domain.team.service;

import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.dto.ScheduleDto;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.dto.GetTeamResponseDto;
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
public class TeamService {

    private final TeamRepository teamRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public GetTeamResponseDto getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("team not found"));
        List<Schedule> schedules = scheduleRepository.findAllByTeam(team);

        // 스케줄 데이터를 년도별로 그룹화하고, 내부에서 날짜 기준으로 정렬
        Map<Integer, List<Schedule>> schedulesByYear = schedules.stream()
            .collect(Collectors.groupingBy(schedule -> schedule.getStartAt().getYear()));

        // 년도별로 그룹화된 데이터를 처리
        List<GetTeamResponseDto.TeamScheduleByYearDto> schedulesYear = schedulesByYear.entrySet().stream()
            .map(entry -> {
                int year = entry.getKey();
                List<ScheduleDto> sortedSchedules = entry.getValue().stream()
                    .sorted(Comparator.comparing(Schedule::getStartAt))  // 날짜 순서로 정렬
                    .map(Schedule::toScheduleDto)
                    .toList();

                return new GetTeamResponseDto.TeamScheduleByYearDto(year, sortedSchedules);
            })
            .sorted(Comparator.comparing(GetTeamResponseDto.TeamScheduleByYearDto::year).reversed())  // 년도별로 내림차순 정렬 (최신 년도 우선)
            .toList();

        return new GetTeamResponseDto(schedulesYear);
    }

}
