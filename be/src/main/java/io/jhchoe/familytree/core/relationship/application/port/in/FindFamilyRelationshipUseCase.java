package io.jhchoe.familytree.core.relationship.application.port.in;

import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
import java.util.List;
import java.util.Optional;

/**
 * 가족 관계 조회를 위한 유스케이스 인터페이스입니다.
 */
public interface FindFamilyRelationshipUseCase {

    /**
     * 특정 구성원이 정의한 다른 구성원과의 관계를 조회합니다.
     *
     * @param query 관계 조회에 필요한 정보가 담긴 쿼리 객체
     * @return 조회된 가족 관계 정보
     */
    Optional<FamilyRelationship> findRelationship(FindFamilyRelationshipQuery query);
    
    /**
     * 특정 구성원이 정의한 모든 관계를 조회합니다.
     *
     * @param query 관계 조회에 필요한 정보가 담긴 쿼리 객체
     * @return 조회된 가족 관계 목록
     */
    List<FamilyRelationship> findAllRelationshipsByMember(FindMemberRelationshipsQuery query);
}
