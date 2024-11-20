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

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public User createNewUser(String email, String name, String profileImageUrl) {
        User user = User.builder()
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .role(ROLE.USER)
                .isRegistered(false)
                .isAdmin(false)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User registerNewUser(String email, String name, ROLE role ,String sport, String college, String teamName) {
        // 이미 존재하는 사용자 검색
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        Team team = teamRepository.findByName(teamName).orElse(null);

        if(team != null) {
            if(!sport.equals(team.getSportType().getName()) || !college.equals(team.getCollege().getName())){
                throw new IllegalArgumentException("team attributes are not valid");
            }
        }

        //팀 아이디인 경우 팀 설정
        if(role == ROLE.TEAM){
            user.setRole(role);
            user.setTeam(team);
        }

        //이름을 사용자가 수정한 값으로 변경
        user.setName(name);

        //회원가입 완료 처리
        user.setRegistered(true);

        return userRepository.save(user);
    }

    public User getUserByToken(String token) {
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }
}
