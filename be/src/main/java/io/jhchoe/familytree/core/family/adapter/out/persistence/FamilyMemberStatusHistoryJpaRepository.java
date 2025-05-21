package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 구성원 상태 변경 이력에 대한 JPA 리포지토리 인터페이스입니다.
 */
public interface FamilyMemberStatusHistoryJpaRepository extends JpaRepository<FamilyMemberStatusHistoryJpaEntity, Long> {

    /**
     * 특정 구성원의 모든 상태 변경 이력을 조회합니다.
     *
     * @param memberId 구성원 ID
     * @return 상태 변경 이력 목록
     */
    List<FamilyMemberStatusHistoryJpaEntity> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    /**
     * 특정 가족의 모든 상태 변경 이력을 조회합니다.
     *
     * @param familyId 가족 ID
     * @return 상태 변경 이력 목록
     */
    List<FamilyMemberStatusHistoryJpaEntity> findAllByFamilyIdOrderByCreatedAtDesc(Long familyId);
}
