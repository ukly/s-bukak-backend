package com.sbukak.domain.team.controller;

import com.sbukak.domain.team.dto.GetTeamResponseDto;
import com.sbukak.domain.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/team")
    @Operation(summary = "팀 페이지 조회")
    public ResponseEntity<GetTeamResponseDto> getTeam() {
        return ResponseEntity.ok(
            teamService.getTeam()
        );
    }
}
