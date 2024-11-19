package com.sbukak.global.oauth2;

import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    @Value("${app.client-url}")
    private String clientUrl;

    private final HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getName();
        String profileImageUrl = oAuth2User.getAttribute("picture");

        httpSession.setAttribute("profile", profileImageUrl);

        GoogleOAuth2UserInfo userInfo = (GoogleOAuth2UserInfo) httpSession.getAttribute("oauthUser");


        //유저 정보가 없거나 세션이 만료된것이 아닌지 확인
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // 해당 서비스에 처음 로그인 하는 유저의 경우
            user = User.builder()
                    .email(email)
                    .name(name)
                    .profileImageUrl(profileImageUrl)
                    .isRegistered(false)
                    .build();
            userRepository.save(user);

            String redirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrl + "/signup")
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        } else if (!user.isRegistered()) {
            //회원가입을 완료 안한경우
            String redirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrl + "/signup")
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        } else {
            // 해당 서비스에 이미 로그인 해본 적이 있는 유저의 경우
            String accessToken = jwtTokenProvider.createToken(email);
            String redirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrl + "/")
                    .queryParam("token", accessToken)
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        }
    }
}
