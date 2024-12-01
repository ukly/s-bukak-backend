package com.sbukak.domain.user.service;

import com.sbukak.domain.banner.repository.BannerRepository;
import com.sbukak.domain.bet.repository.BetRepository;
import com.sbukak.domain.board.repository.BoardRepository;
import com.sbukak.domain.board.repository.CommentRepository;
import com.sbukak.domain.message.repository.MessageRepository;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.domain.team.repository.TeamRepository;
import com.sbukak.domain.user.entity.ROLE;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {


    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final MessageRepository messageRepository;
    private final BannerRepository bannerRepository;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
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

    @Transactional
    public User editProfile(User user, String newName){
        user.setName(newName);

        return userRepository.save(user);
    }

    public User getUserOrNull(String token) {
        if (token == null) {
            return null;
        }
        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        if (userEmail == null) {
            return null;
        }
        return userRepository.findByEmail(userEmail).orElseGet(null);
    }

    public User checkAdminByToken(String token) {
        User user = getUserByToken(token);
        user.checkAdmin();
        return user;
    }

    @Transactional
    public void deleteUserById(Long userId){
        // Bet 삭제
        betRepository.deleteByUserId(userId);

        // Message 삭제
        messageRepository.deleteByUserId(userId);

        // Banner 삭제
        bannerRepository.deleteByUserId(userId);

        // 사용자가 작성한 댓글 삭제
        commentRepository.deleteByUserId(userId);

        // 사용자가 작성한 게시글에 속한 댓글 삭제
        List<Long> boardIds = boardRepository.findIdsByUserId(userId);
        if (!boardIds.isEmpty()) {
            commentRepository.deleteByBoardIdIn(boardIds);
        }

        // Board 삭제
        boardRepository.deleteByUserId(userId);

        // User 삭제
        userRepository.deleteById(userId);
    }
}
