package com.sbukak.domain.schedule.controller;

import com.sbukak.domain.schedule.dto.GetSchedulesResponseDto;
import com.sbukak.domain.schedule.dto.ScheduleRequestDto;
import com.sbukak.domain.schedule.service.ScheduleService;
import com.sbukak.global.enums.SportType;
import com.sbukak.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "일정 조회")
    public ResponseEntity<GetSchedulesResponseDto> getSchedules(
        HttpServletRequest httpServletRequest,
        @RequestParam(value = "sportType", defaultValue = "SOCCER") SportType sportType
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        return ResponseEntity.ok(
            scheduleService.getSchedules(sportType, token)
        );
    }

    @PostMapping
    @Operation(summary = "일정 등록")
    public ResponseEntity<Void> createSchedule(
        HttpServletRequest httpServletRequest,
        @RequestBody ScheduleRequestDto requestDto
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        scheduleService.createSchedule(token, requestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{scheduleId}")
    @Operation(summary = "일정 수정")
    public ResponseEntity<Void> updateSchedule(
        HttpServletRequest httpServletRequest,
        @PathVariable Long scheduleId,
        @RequestBody ScheduleRequestDto requestDto
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        scheduleService.updateSchedule(token, scheduleId, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제")
    public ResponseEntity<Void> deleteSchedule(
        HttpServletRequest httpServletRequest,
        @PathVariable Long scheduleId
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        scheduleService.deleteSchedule(token, scheduleId);
        return ResponseEntity.ok().build();
    }
}
