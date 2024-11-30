package com.sbukak.global.oauth2;

import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일로 사용자 정보 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid login credentials"));

        // 권한 이름 설정
        String roleName = "ROLE_" + user.getRole().name(); // "ROLE_USER" 또는 "ROLE_ADMIN"

        // User 엔티티를 UserDetails로 변환
        return new CustomUserDetails(user);
    }
}
