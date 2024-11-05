package com.sbukak.domain.user.controller;

import com.sbukak.domain.user.dto.RegistserRequestDto;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<Void> registerWithAdditionalInfo(@RequestBody RegistserRequestDto requestDto) {

        User newUser;
        if (requestDto.isTeamLeader()){
            newUser = userService.registerNewUser(
                    requestDto.email(),
                    requestDto.name(),
                    ROLE.USER,
                    requestDto.sport(),
                    requestDto.college(),
                    requestDto.team());
        } else {
            newUser = userService.registerNewUser(
                    requestDto.email(),
                    requestDto.name(),
                    ROLE.TEAM,
                    null,
                    null,
                    null);
        }


        //jwt 토큰 생성
        String accessToken = jwtTokenProvider.createToken(newUser.getEmail());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
