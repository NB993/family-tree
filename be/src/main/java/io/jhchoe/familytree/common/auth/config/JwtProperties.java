package io.jhchoe.familytree.common.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 관련 설정 프로퍼티를 관리하는 클래스입니다.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private String issuer;

    /**
     * JWT 비밀 키를 반환합니다.
     *
     * @return JWT 비밀 키
     */
    public String getSecret() {
        return secret;
    }

    /**
     * JWT 비밀 키를 설정합니다.
     *
     * @param secret JWT 비밀 키
     */
    public void setSecret(final String secret) {
        this.secret = secret;
    }

    /**
     * Access Token 만료 시간(초)을 반환합니다.
     *
     * @return Access Token 만료 시간(초)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Access Token 만료 시간(초)을 설정합니다.
     *
     * @param accessTokenExpiration Access Token 만료 시간(초)
     */
    public void setAccessTokenExpiration(final long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * Refresh Token 만료 시간(초)을 반환합니다.
     *
     * @return Refresh Token 만료 시간(초)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    /**
     * Refresh Token 만료 시간(초)을 설정합니다.
     *
     * @param refreshTokenExpiration Refresh Token 만료 시간(초)
     */
    public void setRefreshTokenExpiration(final long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * JWT 발급자를 반환합니다.
     *
     * @return JWT 발급자
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * JWT 발급자를 설정합니다.
     *
     * @param issuer JWT 발급자
     */
    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }
}
