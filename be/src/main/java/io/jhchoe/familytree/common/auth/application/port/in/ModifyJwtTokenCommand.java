package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * JWT 토큰 수정을 위한 Command 객체입니다.
 */
public record ModifyJwtTokenCommand(
    String refreshToken
) {
    
    /**
     * ModifyJwtTokenCommand 생성자입니다.
     *
     * @param refreshToken 수정할 Refresh Token
     * @throws IllegalArgumentException refreshToken이 null이거나 비어있는 경우 발생
     */
    public ModifyJwtTokenCommand {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
        if (refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken must not be blank");
        }
    }
}
