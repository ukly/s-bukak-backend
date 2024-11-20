package com.sbukak.domain.bet.controller;

import com.sbukak.domain.bet.dto.SubmitBetRequestDto;
import com.sbukak.domain.bet.service.BetService;
import com.sbukak.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/bet")
    @Operation(summary = "승부 예측 등록")
    public ResponseEntity<Void> submitBet(
        HttpServletRequest httpServletRequest,
        @RequestBody SubmitBetRequestDto requestDto
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        betService.submitBet(requestDto, token);
        return ResponseEntity.ok().build();
    }
}
