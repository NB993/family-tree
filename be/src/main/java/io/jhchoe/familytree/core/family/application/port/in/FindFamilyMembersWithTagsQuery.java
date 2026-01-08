package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;

/**
 * 태그 정보를 포함한 Family 구성원 목록 조회 쿼리 객체입니다.
 *
 * @param familyId      조회할 Family ID
 * @param currentUserId 현재 사용자 ID
 */
public record FindFamilyMembersWithTagsQuery(
    Long familyId,
    Long currentUserId
) {
    public FindFamilyMembersWithTagsQuery {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        if (familyId <= 0) {
            throw new IllegalArgumentException("familyId must be positive");
        }
        if (currentUserId <= 0) {
            throw new IllegalArgumentException("currentUserId must be positive");
        }
    }
}
