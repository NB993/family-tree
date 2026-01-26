package io.jhchoe.familytree.core.user.application.port.in;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User 프로필 수정을 위한 명령 객체입니다.
 *
 * @param userId       수정할 사용자 ID
 * @param name         새 이름
 * @param birthday     새 생일 (nullable)
 * @param birthdayType 새 생일 유형 (nullable)
 */
public record ModifyUserCommand(
    Long userId,
    String name,
    LocalDateTime birthday,
    BirthdayType birthdayType
) {
    public ModifyUserCommand {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다");
        Objects.requireNonNull(name, "name은 null일 수 없습니다");

        if (userId <= 0) {
            throw new IllegalArgumentException("userId는 0보다 커야 합니다");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다");
        }
        if (birthday != null && birthdayType == null) {
            throw new IllegalArgumentException("생일이 있으면 생일 유형도 필수입니다");
        }
        if (birthday == null && birthdayType != null) {
            throw new IllegalArgumentException("생일 유형이 있으면 생일도 필수입니다");
        }
    }
}
