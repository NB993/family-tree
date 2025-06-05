package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * 특정 Family 구성원 단건 조회를 위한 쿼리 객체입니다.
 */
@Getter
public class FindFamilyMemberQuery {

    private final Long familyId;
    private final Long currentUserId;
    private final Long targetMemberId;

    /**
     * FindFamilyMemberQuery 생성자입니다.
     *
     * @param familyId 조회할 Family ID
     * @param currentUserId 현재 사용자 ID
     * @param targetMemberId 조회할 대상 구성원 ID
     * @throws IllegalArgumentException 필수 파라미터가 null이거나 유효하지 않은 경우
     */
    public FindFamilyMemberQuery(Long familyId, Long currentUserId, Long targetMemberId) {
        validateParams(familyId, currentUserId, targetMemberId);
        
        this.familyId = familyId;
        this.currentUserId = currentUserId;
        this.targetMemberId = targetMemberId;
    }

    private void validateParams(Long familyId, Long currentUserId, Long targetMemberId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 가족 ID가 필요합니다.");
        }
        if (currentUserId == null || currentUserId <= 0) {
            throw new IllegalArgumentException("유효한 현재 사용자 ID가 필요합니다.");
        }
        if (targetMemberId == null || targetMemberId <= 0) {
            throw new IllegalArgumentException("유효한 대상 구성원 ID가 필요합니다.");
        }
    }
}
