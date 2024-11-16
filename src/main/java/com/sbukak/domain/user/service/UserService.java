package com.sbukak.domain.user.service;

import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final HttpSession httpSession;

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public User registerNewUser(String email, String name, ROLE role ,String sport, String college, String teamName) {
        Team team = teamRepository.findByName(teamName).orElse(null);

        if(team != null) {
            if(!sport.equals(team.getSportType().getName()) || !college.equals(team.getCollege().getName())){
                throw new IllegalArgumentException("team attributes are not valid");
            }
        }

        String profileImageUrl = (String) httpSession.getAttribute("profile");

        User user = User.builder()
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .role(role)
                .build();

        if(role == ROLE.TEAM){
            user.setTeam(team);
        }

        return userRepository.save(user);
    }

    public User getUserByToken(String token) {
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }
}
