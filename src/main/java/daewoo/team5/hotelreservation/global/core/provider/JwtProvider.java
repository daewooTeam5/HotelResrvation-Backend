package daewoo.team5.hotelreservation.global.core.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final long expirationTimeAccessToken = 1000 * 60 * 60; // 1 hour
    private final long expirationTimeRefreshToken = 1000L * 60 * 60 * 24 * 30; // 1 month
    @Value("${JWT_SECRET}")
    private String jwtSecretKey;

    private SecretKey getSigningKey() {
        // 문자열 키를 SecretKey 객체로 변환
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) throws JsonProcessingException {
        Claims claims = parseClaims(token);
        String subJson = claims.getSubject(); // JSON 문자열
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> subMap = mapper.readValue(subJson, new TypeReference<>(){});
        String role = (String) subMap.get("role");
        log.info("Claims: {}", claims);
        String userId = claims.getSubject();

        return new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(role)));
    }
    // JWT 토큰 발급
    public <T> String generateToken(T data,long expirationTime) {
        log.info("secret key: {}", jwtSecretKey);

        String payloadJson;
        try {
            // 객체를 JSON 문자열로 변환
            payloadJson = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JWT payload 직렬화 실패", e);
        }

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(payloadJson)        // payload를 문자열로 넣음
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // JWT 토큰 발급
    public <T> String generateToken(T data, TokenType tokenType) {
        log.info("secret key: {}", jwtSecretKey);
        String payloadJson;
        try {
            // 객체를 JSON 문자열로 변환
            payloadJson = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JWT payload 직렬화 실패", e);
        }
        Date now = new Date();

        Date expiryDate = new Date(now.getTime() +
                (tokenType == TokenType.ACCESS
                        ? expirationTimeAccessToken
                        : expirationTimeRefreshToken)
        );

        return Jwts.builder()
                .setSubject(payloadJson)        // payload를 문자열로 넣음
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // token 디코딩
    public String decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // token 인증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "JWT 서명 검증 실패", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "JWT 토큰 만료", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new ApiException(HttpStatus.BAD_REQUEST, "JWT 형식 오류", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new ApiException(HttpStatus.BAD_REQUEST, "지원하지 않는 JWT", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty or null: {}", e.getMessage());
            throw new ApiException(HttpStatus.BAD_REQUEST, "JWT 토큰이 비어있음", e.getMessage());
        }

    }

    public enum TokenType {
        ACCESS, REFRESH
    }
}
