package io.jhchoe.familytree.common.auth.util;

import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.exception.ExpiredTokenException;
import io.jhchoe.familytree.common.auth.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성, 검증, 파싱을 담당하는 유틸리티 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    /**
     * FTUser 정보를 기반으로 Access Token을 생성합니다.
     *
     * @param ftUser 토큰에 포함할 사용자 정보
     * @return 생성된 Access Token
     */
    public String generateAccessToken(final FTUser ftUser) {
        Objects.requireNonNull(ftUser, "ftUser must not be null");
        Objects.requireNonNull(ftUser.getId(), "ftUser.id must not be null");

        final LocalDateTime expirationTime = LocalDateTime.now()
            .plusSeconds(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
            .setSubject(ftUser.getId().toString())
            .claim("email", ftUser.getEmail())
            .claim("name", ftUser.getName())
            .claim("role", "USER") // 현재는 고정값, 향후 확장 가능
            .setIssuer(jwtProperties.getIssuer())
            .setIssuedAt(new Date())
            .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * 사용자 ID를 기반으로 Refresh Token을 생성합니다.
     *
     * @param userId 사용자 ID
     * @return 생성된 Refresh Token
     */
    public String generateRefreshToken(final Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        final LocalDateTime expirationTime = LocalDateTime.now()
            .plusSeconds(jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuer(jwtProperties.getIssuer())
            .setIssuedAt(new Date())
            .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * JWT 토큰을 검증하고 유효한지 확인합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (final ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (final SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 사용자 ID
     */
    public Long extractUserId(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        final Claims claims = extractClaims(token);
        final String userIdStr = claims.getSubject();
        
        try {
            return Long.parseLong(userIdStr);
        } catch (final NumberFormatException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다");
        }
    }

    /**
     * JWT 토큰에서 이메일을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 이메일
     */
    public String extractEmail(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        final Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * JWT 토큰에서 사용자 이름을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 사용자 이름
     */
    public String extractName(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        final Claims claims = extractClaims(token);
        return claims.get("name", String.class);
    }

    /**
     * JWT 토큰에서 역할을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 역할
     */
    public String extractRole(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        final Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * JWT 토큰에서 만료 시간을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 만료 시간
     */
    public Date extractExpiration(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        final Claims claims = extractClaims(token);
        return claims.getExpiration();
    }

    /**
     * JWT 토큰이 만료되었는지 확인합니다.
     *
     * @param token JWT 토큰
     * @return 만료되었으면 true, 그렇지 않으면 false
     */
    public boolean isTokenExpired(final String token) {
        Objects.requireNonNull(token, "token must not be null");

        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (final ExpiredTokenException e) {
            return true;
        }
    }

    /**
     * JWT 토큰에서 Claims를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 Claims
     */
    private Claims extractClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (final ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (final SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    /**
     * JWT 서명에 사용할 SecretKey를 생성합니다.
     *
     * @return JWT 서명용 SecretKey
     */
    private SecretKey getSigningKey() {
        final byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
