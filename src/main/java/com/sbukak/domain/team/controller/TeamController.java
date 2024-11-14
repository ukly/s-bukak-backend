package com.sbukak.domain.team.controller;

import com.sbukak.domain.team.dto.GetTeamResponseDto;
import com.sbukak.domain.team.dto.UpdateTeamPlayersRequestDto;
import com.sbukak.domain.team.service.TeamService;
import com.sbukak.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/team/{teamId}/players")
    @Operation(summary = "선수 정보 업데이트")
    public ResponseEntity<Void> updateTeamPlayers(
        HttpServletRequest httpServletRequest,
        @PathVariable("teamId") Long teamId,
        @RequestBody UpdateTeamPlayersRequestDto requestDto
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        teamService.updateTeamPlayers(token, teamId, requestDto);
        return ResponseEntity.ok().build();
    }
}
