package com.sbukak.global.jwt;

import com.sbukak.global.oauth2.CustomUserDetailService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {
    private Key key;
    private final CustomUserDetailService customUserDetailService;

    public JwtTokenProvider(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long tokenValidityInSeconds;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email, String name, Boolean isTeamLeader, String sport, String college, String team){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("name", name);
        claims.put("email", email);
        claims.put("isTeamLeader", isTeamLeader);
        claims.put("sport", sport);
        claims.put("college", college);
        claims.put("team", team);

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInSeconds * 1000); // 초 단위로 설정되었으므로 1000을 곱해 밀리초로 변환

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey) // HMAC-SHA256을 사용하여 시크릿 키로 서명
                .compact();
    }

    public String getEmail(String token){
        return parseClaims(token).get("email", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
            throw new ExpiredJwtException(null, null, "만료된 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못되었습니다.");
            throw new IllegalArgumentException("JWT 토큰 입력이 잘못되었습니다");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("JWT 토큰의 형식이 올바르지 않습니다.");
        } catch (Exception e) {
            return false;
        }
    }


    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // Request Header 에서 토큰 정보를 추출하는 메서드
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Bearer 타입의 토큰인지 확인
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 다음부터가 토큰 값
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getEmail(HttpServletRequest request) {
        String token = resolveToken(request);
        return getEmailFromToken(token);
    }
}
