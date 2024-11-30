package com.sbukak.domain.user.controller;

import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.user.dto.AdminLoginRequestDTO;
import com.sbukak.domain.user.dto.EditProfileRequestDTO;
import com.sbukak.domain.user.dto.RegistserRequestDto;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.AdminAuthenticationService;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.jwt.JwtTokenProvider;
import com.sbukak.global.oauth2.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminAuthenticationService adminAuthenticationService;

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
        Team team = newUser.getTeam();
        Boolean isTeamLeader = newUser.getRole() == ROLE.TEAM;

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(newUser.getId(),newUser.getEmail(), newUser.getName(), newUser.getProfileImageUrl(),isTeamLeader,
                team.getSportType().getName(), team.getCollege().getName(), team.getName());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @PatchMapping("/editProfile")
    @Operation(summary = "프로필 수정", description = "사용자의 프로필(예: 닉네임)을 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<Void> editProfile(
            @RequestBody EditProfileRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();

        Team team = user.getTeam();
        Boolean isTeamLeader = user.getRole() == ROLE.TEAM;


        user = userService.editProfile(user, requestDTO.name());

        String accessToken;
        if(isTeamLeader) {
            accessToken = jwtTokenProvider.createToken(user.getId(),user.getEmail(), user.getName(), user.getProfileImageUrl(), isTeamLeader,
                    team.getSportType().getName(), team.getCollege().getName(), team.getName());
        } else {
            accessToken = jwtTokenProvider.createToken(user.getId(),user.getEmail(), user.getName(), user.getProfileImageUrl(), isTeamLeader,
                    null, null, null);
        }


        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequestDTO loginRequest) {
        try {
            String token = adminAuthenticationService.adminLogin(loginRequest);
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body("로그인 성공");
        } catch (AuthenticationServiceException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}