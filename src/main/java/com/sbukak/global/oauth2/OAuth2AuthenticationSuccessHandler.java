package com.sbukak.global.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.jwt.JwtTokenProvider;
import com.sbukak.global.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    @Value("${app.client-url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getName();
        String profileImageUrl = oAuth2User.getAttribute("picture");

        //유저 정보가 없거나 세션이 만료된것이 아닌지 확인
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // 해당 서비스에 처음 로그인 하는 유저의 경우
            // Zero Width Joiner와 같은 유니코드 제거
            userService.createNewUser(email, name, profileImageUrl);

            String redirectUrl = Utils.createRedirectUrl(clientUrl, "/signup", Map.of("email", email, "name", name));
            response.sendRedirect(redirectUrl);
        } else if (!user.isRegistered()) {
            //구글 로그인은 했지만 회원가입을 완료하지 않은 경우
            String redirectUrl = Utils.createRedirectUrl(clientUrl, "/signup", Map.of("email", email, "name", name));
            response.sendRedirect(redirectUrl);
        } else {
            // 해당 서비스에 이미 로그인 해본 적이 있는 유저의 경우
            String accessToken = jwtTokenProvider.createToken(email, name);
            String redirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrl + "/")
                    .queryParam("token", accessToken)
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        }
    }
}
