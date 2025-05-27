package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 가족 관계 JPA 리포지토리 인터페이스입니다.
 */
public interface FamilyMemberRelationshipJpaRepository extends JpaRepository<FamilyMemberRelationshipJpaEntity, Long> {
    
    /**
     * 특정 구성원 간의 관계를 조회합니다.
     *
     * @param familyId     가족 ID
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @param toMemberId   관계가 정의된 대상 구성원 ID
     * @return 조회된 가족 관계 JPA 엔티티
     */
    Optional<FamilyMemberRelationshipJpaEntity> findByFamilyIdAndFromMemberIdAndToMemberId(
        Long familyId,
        Long fromMemberId,
        Long toMemberId
    );
    
    /**
     * 특정 구성원이 정의한 모든 관계를 조회합니다.
     *
     * @param familyId     가족 ID
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @return 조회된 가족 관계 JPA 엔티티 목록
     */
    List<FamilyMemberRelationshipJpaEntity> findAllByFamilyIdAndFromMemberId(Long familyId, Long fromMemberId);

    /**
     * 특정 가족의 모든 관계를 조회합니다.
     *
     * @param familyId 가족 ID
     * @return 조회된 가족 관계 JPA 엔티티 목록
     */
    List<FamilyMemberRelationshipJpaEntity> findAllByFamilyId(Long familyId);
}
