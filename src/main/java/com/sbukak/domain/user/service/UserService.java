package com.sbukak.domain.user.service;

import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.Team;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public String processUser(String email, String name, String role) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            registerNewUser(userInfo)
        }
    }

    @Transactional
    public User registerNewUser(String email, String name, String sport, String college, String teamName) {
        Team team =

        User user = User.builder()
                .email(email)
                .name(name)
                .role(ROLE.USER)
                .build();

        return userRepository.save(user);
    }
}
