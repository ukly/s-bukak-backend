package com.sbukak.domain.ranking.controller;

import com.sbukak.global.enums.SportType;
import com.sbukak.domain.ranking.dto.GetRankingResponseDto;
import com.sbukak.domain.ranking.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/ranking")
    @Operation(summary = "팀 순위")
    public ResponseEntity<GetRankingResponseDto> getRanking(
        @RequestParam(value = "sportType", defaultValue = "SOCCER") SportType sportType
    ) {
        return ResponseEntity.ok(
            rankingService.getRanking(sportType)
        );
    }
}
