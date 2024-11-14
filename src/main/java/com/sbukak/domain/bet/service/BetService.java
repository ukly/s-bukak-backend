package com.sbukak.domain.bet.service;

import com.sbukak.domain.bet.domain.Bet;
import com.sbukak.domain.bet.dto.SubmitBetRequestDto;
import com.sbukak.domain.bet.enums.BetTimeType;
import com.sbukak.domain.bet.repository.BetRepository;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.schedule.repository.ScheduleRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BetService {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void submitBet(SubmitBetRequestDto requestDto, String token) {
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
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
    }
}
