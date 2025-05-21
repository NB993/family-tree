package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * 구성원 역할 조회를 위한 쿼리 객체입니다.
 */
@Getter
public class FindFamilyMembersRoleQuery {
    
    private final Long familyId;
    private final Long currentUserId;
    
    /**
     * 구성원 역할 조회 쿼리 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param currentUserId 현재 로그인한 사용자 ID
     */
    public FindFamilyMembersRoleQuery(
        Long familyId,
        Long currentUserId
    ) {
        validateFamilyId(familyId);
        validateCurrentUserId(currentUserId);
        
        this.familyId = familyId;
        this.currentUserId = currentUserId;
    }
    
    private void validateFamilyId(Long familyId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 Family ID가 필요합니다.");
        }
    }
    
    private void validateCurrentUserId(Long currentUserId) {
        if (currentUserId == null || currentUserId <= 0) {
            throw new IllegalArgumentException("유효한 현재 사용자 ID가 필요합니다.");
        }
    }
}
