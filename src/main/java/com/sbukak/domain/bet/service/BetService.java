package com.sbukak.domain.bet.service;

import com.sbukak.domain.bet.domain.Bet;
import com.sbukak.domain.bet.dto.SubmitBetRequestDto;
import com.sbukak.domain.bet.enums.BetTimeType;
import com.sbukak.domain.bet.repository.BetRepository;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BetService {
    private final BetRepository betRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;

    @Transactional
    public void submitBet(SubmitBetRequestDto requestDto, String token) {
        User user = userService.getUserByToken(token);
        Schedule schedule = scheduleRepository.findById(requestDto.scheduleId())
            .orElseThrow(() -> new IllegalArgumentException("schedule not found"));
        if (betRepository.existsByUserAndSchedule(user, schedule)) {
            throw new IllegalArgumentException("유저가 해당 경기에 대해 이미 승부예측을 진행했습니다.");
        }
        if (BetTimeType.getBetTimeType(schedule.getStartAt()) != BetTimeType.예측진행중) {
            throw new IllegalArgumentException("해당 경기는 예측 진행 중이 아닙니다.");
        }
        Bet bet = new Bet(user, schedule, requestDto.isBetHomeTeam());
        betRepository.save(bet);
        schedule.bet(requestDto.isBetHomeTeam());
    }
}
