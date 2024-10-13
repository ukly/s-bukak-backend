package com.sbukak.domain.user.controller;

import com.sbukak.domain.user.dto.RegistserRequestDto;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.oauth2.GoogleOAuth2UserInfo;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private final HttpSession httpSession;
    private final UserService userService;

    @PostMapping("/register")
    public String registerWithAdditionalInfo(@RequestBody RegistserRequestDto requestDto) {
        GoogleOAuth2UserInfo userInfo = (GoogleOAuth2UserInfo) httpSession.getAttribute("oauthUser");

        if (userInfo != null) {
            userService.registerNewUser(userInfo.getEmail(), userInfo.getName(), )
        }

    }
}
