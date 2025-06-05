package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * Family ID와 현재 사용자 기준으로 ACTIVE 상태 구성원 목록 조회를 위한 쿼리 객체입니다.
 */
@Getter
public class FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery {

    private final Long familyId;
    private final Long currentUserId;

    /**
     * FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery 생성자입니다.
     *
     * @param familyId 조회할 Family ID
     * @param currentUserId 현재 사용자 ID
     * @throws IllegalArgumentException 필수 파라미터가 null이거나 유효하지 않은 경우
     */
    public FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(Long familyId, Long currentUserId) {
        validateParams(familyId, currentUserId);
        
        this.familyId = familyId;
        this.currentUserId = currentUserId;
    }

    private void validateParams(Long familyId, Long currentUserId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 가족 ID가 필요합니다.");
        }
        if (currentUserId == null || currentUserId <= 0) {
            throw new IllegalArgumentException("유효한 현재 사용자 ID가 필요합니다.");
        }
    }
}
