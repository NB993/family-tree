package io.jhchoe.familytree.core.relationship.application.port.in;

import lombok.Getter;

/**
 * 특정 가족 관계 조회 쿼리 객체입니다.
 */
@Getter
public class FindFamilyRelationshipQuery {

    private final Long familyId;
    private final Long fromMemberId;
    private final Long toMemberId;

    /**
     * 특정 가족 관계 조회 쿼리 객체를 생성합니다.
     *
     * @param familyId     가족 ID
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @param toMemberId   관계가 정의된 대상 구성원 ID
     */
    public FindFamilyRelationshipQuery(Long familyId, Long fromMemberId, Long toMemberId) {
        validateParams(familyId, fromMemberId, toMemberId);
        
        this.familyId = familyId;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
    }
    
    private void validateParams(Long familyId, Long fromMemberId, Long toMemberId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 가족 ID가 필요합니다.");
        }
        if (fromMemberId == null || fromMemberId <= 0) {
            throw new IllegalArgumentException("유효한 구성원 ID가 필요합니다.");
        }
        if (toMemberId == null || toMemberId <= 0) {
            throw new IllegalArgumentException("유효한 대상 구성원 ID가 필요합니다.");
        }
        if (fromMemberId.equals(toMemberId)) {
            throw new IllegalArgumentException("관계를 정의하는 사람과 대상은 서로 달라야 합니다.");
        }
    }
}
