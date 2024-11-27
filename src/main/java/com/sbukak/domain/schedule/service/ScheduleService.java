package com.sbukak.domain.schedule.service;

import com.sbukak.domain.bet.domain.Bet;
import com.sbukak.domain.bet.repository.BetRepository;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.dto.GetSchedulesResponseDto;
import com.sbukak.domain.schedule.dto.ScheduleRequestDto;
import com.sbukak.domain.schedule.dto.ScheduleResultRequestDto;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.team.domain.College;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.CollegeRepository;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.enums.SportType;
import com.sbukak.global.jwt.JwtTokenProvider;
import com.sbukak.global.util.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TeamRepository teamRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;


    //경기를 년도와 월별로 그룹화하여 처리
    @Transactional(readOnly = true)
    public GetSchedulesResponseDto getSchedules(SportType sportType, String token) {
        List<Bet> bets = getBets(token);

        List<Schedule> schedules = scheduleRepository.findAllBySportType(sportType);

        // Schedule 데이터를 년도별로 그룹화한 후, 그 안에서 월별로 그룹화
        Map<Integer, Map<Integer, List<Schedule>>> schedulesByYearAndMonth = schedules.stream()
            .collect(Collectors.groupingBy(
                schedule -> schedule.getStartAt().getYear(),  // 년도별 그룹화
                Collectors.groupingBy(schedule -> schedule.getStartAt().getMonthValue())  // 월별 그룹화
            ));

        List<GetSchedulesResponseDto.ScheduleYearDto> schedulesYear = new ArrayList<>();

        // 년도별 데이터를 처리
        for (Map.Entry<Integer, Map<Integer, List<Schedule>>> yearEntry : schedulesByYearAndMonth.entrySet()) {
            int year = yearEntry.getKey();

            // 월별 데이터를 처리
            List<GetSchedulesResponseDto.ScheduleYearDto.ScheduleMonthDto> schedulesMonth = yearEntry.getValue().entrySet().stream()
                .map(
                    monthEntry -> new GetSchedulesResponseDto.ScheduleYearDto.ScheduleMonthDto(
                        monthEntry.getKey(),
                        monthEntry.getValue().stream()
                            .map(schedule -> {
                                Bet bet =
                                    bets.stream().filter(it -> it.getSchedule() == schedule).findFirst().orElse(null);
                                boolean isParticipatedBet = bet != null;
                                Boolean isBetHomeTeam = bet != null ? bet.getIsBetHomeTeam() : null;
                                return schedule.toScheduleDto(isParticipatedBet, isBetHomeTeam);
                            })
                            .toList()
                    )
                ).toList();

            schedulesYear.add(new GetSchedulesResponseDto.ScheduleYearDto(year, schedulesMonth));
        }

        return new GetSchedulesResponseDto(schedulesYear);
    }

    private List<Bet> getBets(String token) {
        User user = userService.getUserOrNull(token);
        if (user == null) {
            return new ArrayList<>();
        }
        return betRepository.findAllByUser(user);
    }

    @Transactional
    public void createSchedule(String token, ScheduleRequestDto requestDto) {
        userService.checkAdminByToken(token);
        Team homeTeam = getTeam(requestDto.homeTeam());
        Team awayTeam = getTeam(requestDto.awayTeam());
        Schedule schedule = new Schedule(
            homeTeam,
            awayTeam,
            Utils.StringToDateTime(requestDto.startDate(), requestDto.startTime()),
            requestDto.sportType(),
            requestDto.leagueType(),
            requestDto.place()
        );
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void updateSchedule(String token, Long scheduleId, ScheduleRequestDto requestDto) {
        userService.checkAdminByToken(token);
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("스케줄 정보를 찾을 수 없습니다."));
        Team homeTeam = getTeam(requestDto.homeTeam());
        Team awayTeam = getTeam(requestDto.awayTeam());
        schedule.update(
            requestDto.sportType(),
            requestDto.leagueType(),
            homeTeam,
            awayTeam,
            Utils.StringToDateTime(requestDto.startDate(), requestDto.startTime()),
            requestDto.place()
        );
    }

    @Transactional
    public void deleteSchedule(String token, Long scheduleId) {
        userService.checkAdminByToken(token);
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("스케줄 정보를 찾을 수 없습니다."));
        scheduleRepository.delete(schedule);
    }

    @Transactional
    public void createScheduleResult(String token, Long scheduleId, ScheduleResultRequestDto requestDto) {
        userService.checkAdminByToken(token);
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("스케줄 정보를 찾을 수 없습니다."));
        schedule.updateScheduleResult(requestDto.homeTeamGoals(), requestDto.awayTeamGoals());
    }

    private Team getTeam(ScheduleRequestDto.CreateScheduleTeamRequestDto teamRequestDto) {
        College teamCollege = collegeRepository.findByName(teamRequestDto.collegeName())
            .orElseThrow(() -> new IllegalArgumentException("소속 정보를 찾을 수 없습니다."));
        return teamRepository.findByNameAndCollege(teamRequestDto.teamName(), teamCollege)
            .orElseThrow(() -> new IllegalArgumentException("팀 정보를 찾을 수 없습니다."));
    }

}
