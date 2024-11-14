package com.sbukak.domain.schedule.service;

import com.sbukak.domain.bet.domain.Bet;
import com.sbukak.domain.bet.repository.BetRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.enums.SportType;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.dto.GetSchedulesResponseDto;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.global.jwt.JwtTokenProvider;
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
    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final JwtTokenProvider jwtTokenProvider;


    //경기를 년도와 월별로 그룹화하여 처리
    @Transactional(readOnly = true)
    public GetSchedulesResponseDto getSchedules(SportType sportType, String token) {
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
        List<Bet> bets = betRepository.findAllByUser(user);

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
                                boolean isParticipatedBet = bets.stream().anyMatch(it -> it.getSchedule() == schedule);
                                return schedule.toScheduleDto(isParticipatedBet);
                            })
                            .toList()
                    )
                ).toList();

            schedulesYear.add(new GetSchedulesResponseDto.ScheduleYearDto(year, schedulesMonth));
        }

        return new GetSchedulesResponseDto(schedulesYear);
    }
}
