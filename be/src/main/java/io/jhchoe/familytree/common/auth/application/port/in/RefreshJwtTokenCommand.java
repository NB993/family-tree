package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * JWT 토큰 갱신을 위한 Command 객체입니다.
 */
public record RefreshJwtTokenCommand(
    String refreshToken
) {
    
    /**
     * RefreshJwtTokenCommand 생성자입니다.
     *
     * @param refreshToken 갱신할 Refresh Token
     * @throws IllegalArgumentException refreshToken이 null이거나 비어있는 경우 발생
     */
    public RefreshJwtTokenCommand {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
        if (refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken must not be blank");
        }
    }
}
