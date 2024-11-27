package com.sbukak.global.oauth2;

import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);
        String email = userInfo.getEmail();

        User user = userService.findByEmail(email);

        if (user == null) {
            httpSession.setAttribute("oauthUser", userInfo);
        }

        return new CustomOAuth2User(oAuth2User);
    }

}
