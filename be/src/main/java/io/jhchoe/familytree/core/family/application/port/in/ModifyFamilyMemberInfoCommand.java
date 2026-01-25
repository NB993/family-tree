package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Family 구성원 기본 정보 수정을 위한 명령 객체입니다.
 *
 * @param familyId      Family ID
 * @param memberId      변경할 구성원 ID
 * @param currentUserId 현재 요청하는 사용자 ID
 * @param name          새 이름
 * @param birthday      새 생일 (nullable)
 * @param birthdayType  새 생일 유형 (nullable)
 */
public record ModifyFamilyMemberInfoCommand(
    Long familyId,
    Long memberId,
    Long currentUserId,
    String name,
    LocalDateTime birthday,
    BirthdayType birthdayType
) {
    public ModifyFamilyMemberInfoCommand {
        Objects.requireNonNull(familyId, "familyId는 null일 수 없습니다");
        Objects.requireNonNull(memberId, "memberId는 null일 수 없습니다");
        Objects.requireNonNull(currentUserId, "currentUserId는 null일 수 없습니다");
        Objects.requireNonNull(name, "name은 null일 수 없습니다");

        if (familyId <= 0) {
            throw new IllegalArgumentException("familyId는 0보다 커야 합니다");
        }
        if (memberId <= 0) {
            throw new IllegalArgumentException("memberId는 0보다 커야 합니다");
        }
        if (currentUserId <= 0) {
            throw new IllegalArgumentException("currentUserId는 0보다 커야 합니다");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다");
        }
    }
}
