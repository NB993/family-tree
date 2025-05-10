package io.jhchoe.familytree.core.relationship.application.port.in;

import lombok.Getter;

/**
 * 구성원별 가족 관계 목록 조회 쿼리 객체입니다.
 */
@Getter
public class FindMemberRelationshipsQuery {

    private final Long familyId;
    private final Long fromMemberId;

    /**
     * 구성원별 가족 관계 목록 조회 쿼리 객체를 생성합니다.
     *
     * @param familyId     가족 ID
     * @param fromMemberId 관계를 정의한 구성원 ID
     */
    public FindMemberRelationshipsQuery(Long familyId, Long fromMemberId) {
        validateParams(familyId, fromMemberId);
        
        this.familyId = familyId;
        this.fromMemberId = fromMemberId;
    }
    
    private void validateParams(Long familyId, Long fromMemberId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 가족 ID가 필요합니다.");
        }
        if (fromMemberId == null || fromMemberId <= 0) {
            throw new IllegalArgumentException("유효한 구성원 ID가 필요합니다.");
        }
    }
}
