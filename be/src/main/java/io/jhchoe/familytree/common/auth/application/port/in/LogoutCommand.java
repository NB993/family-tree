package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * 로그아웃을 위한 Command 객체입니다.
 */
public record LogoutCommand(
    Long userId
) {
    
    /**
     * LogoutCommand 생성자입니다.
     *
     * @param userId 로그아웃할 사용자 ID
     * @throws IllegalArgumentException userId가 null이거나 0 이하인 경우 발생
     */
    public LogoutCommand {
        Objects.requireNonNull(userId, "userId must not be null");
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
    }
}
