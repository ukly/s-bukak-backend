package com.sbukak.domain.user.service;

import com.sbukak.domain.user.dto.AdminLoginRequestDTO;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.global.jwt.JwtTokenProvider;
import com.sbukak.global.oauth2.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public String adminLogin(AdminLoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

            // 관리자 권한 체크
            if (!user.isAdmin()) {
                throw new AuthenticationServiceException("관리자만 접근 가능합니다.");
            }

            // 토큰 생성
            String token = jwtTokenProvider.createToken(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getProfileImageUrl(),
                    user.getRole() == ROLE.TEAM,
                    null,
                    null,
                    null
            );

            return token;
        } catch (AuthenticationException e) {
            throw new AuthenticationServiceException("인증 실패");
        }
    }
}