package com.sbukak.domain.schedule.controller;

import com.sbukak.global.enums.SportType;
import com.sbukak.domain.schedule.dto.GetSchedulesResponseDto;
import com.sbukak.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedule")
    @Operation(summary = "일정 조회")
    public ResponseEntity<GetSchedulesResponseDto> getSchedules(
        @RequestParam(value = "sportType", defaultValue = "SOCCER") SportType sportType
    ) {
        return ResponseEntity.ok(
            scheduleService.getSchedules(sportType)
        );
    }
}
