package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import java.util.List;
import java.util.Optional;

/**
 * 가족 관계 조회 포트 인터페이스입니다.
 */
public interface FindFamilyMemberRelationshipPort {

    /**
     * 특정 구성원 간의 관계를 조회합니다.
     *
     * @param familyId     가족 ID
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @param toMemberId   관계가 정의된 대상 구성원 ID
     * @return 조회된 가족 관계 정보
     */
    Optional<FamilyMemberRelationship> findByFamilyIdAndFromMemberIdAndToMemberId(Long familyId, Long fromMemberId, Long toMemberId);
    
    /**
     * 특정 구성원이 정의한 모든 관계를 조회합니다.
     *
     * @param familyId     가족 ID
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @return 조회된 가족 관계 목록
     */
    List<FamilyMemberRelationship> findAllByFamilyIdAndFromMemberId(Long familyId, Long fromMemberId);
}
