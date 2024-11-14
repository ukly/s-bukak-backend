package com.sbukak.domain.team.controller;

import com.sbukak.domain.team.dto.GetTeamResponseDto;
import com.sbukak.domain.team.service.TeamService;
import com.sbukak.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/team/{teamId}")
    @Operation(summary = "팀 페이지 조회")
    public ResponseEntity<GetTeamResponseDto> getTeam(
        HttpServletRequest httpServletRequest,
        @PathVariable("teamId") Long teamId
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        return ResponseEntity.ok(
            teamService.getTeam(teamId, token)
        );
    }
}
