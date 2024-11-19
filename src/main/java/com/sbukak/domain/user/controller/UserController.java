package com.sbukak.domain.user.controller;

import com.sbukak.domain.user.dto.RegistserRequestDto;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "첫 로그인 시 추가 정보를 입력하여 회원가입하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<Void> registerWithAdditionalInfo(@RequestBody RegistserRequestDto requestDto) {

        User newUser;
        if (requestDto.isTeamLeader()) {
            newUser = userService.registerNewUser(
                    requestDto.email(),
                    requestDto.name(),
                    ROLE.TEAM,
                    requestDto.sport(),
                    requestDto.college(),
                    requestDto.team());
        } else {
            newUser = userService.registerNewUser(
                    requestDto.email(),
                    requestDto.name(),
                    ROLE.USER,
                    null,
                    null,
                    null);
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(newUser.getEmail(), newUser.getName());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}