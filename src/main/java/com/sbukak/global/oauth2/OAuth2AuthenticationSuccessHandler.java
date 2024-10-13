package com.sbukak.global.oauth2;

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
    @Value("${app.client-url}")
    private String clientUrl;

    private final HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        GoogleOAuth2UserInfo userInfo = (GoogleOAuth2UserInfo) httpSession.getAttribute("oauthUser");

        if (userInfo != null) {
            response.sendRedirect(clientUrl + "/additional-info");
        } else {
            String accessToken = jwtTokenProvider.createToken(email);
            String redirectUrl = UriComponentsBuilder.fromHttpUrl(clientUrl + "/login/callback")
                            .queryParam("token", accessToken).build().toUriString();

            response.sendRedirect(redirectUrl);
        }
    }
}
