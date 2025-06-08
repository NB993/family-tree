package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * JWT 토큰 삭제를 위한 Command 객체입니다.
 */
public record DeleteJwtTokenCommand(
    Long userId
) {
    
    /**
     * DeleteJwtTokenCommand 생성자입니다.
     *
     * @param userId JWT 토큰을 삭제할 사용자 ID
     * @throws IllegalArgumentException userId가 null이거나 0 이하인 경우 발생
     */
    public DeleteJwtTokenCommand {
        Objects.requireNonNull(userId, "userId must not be null");
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
    }
}
