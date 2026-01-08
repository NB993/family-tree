package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * FamilyMemberTagMapping JPA 레포지토리입니다.
 */
public interface FamilyMemberTagMappingJpaRepository extends JpaRepository<FamilyMemberTagMappingJpaEntity, Long> {

    /**
     * 특정 멤버의 모든 태그 매핑을 삭제합니다.
     * <p>
     * JPQL 벌크 삭제를 사용하는 이유:
     * - Spring Data JPA의 deleteBy 메서드는 먼저 SELECT 후 개별 DELETE를 수행 (N+1 문제)
     * - JPQL 벌크 삭제는 단일 DELETE 쿼리로 처리하여 성능 최적화
     *
     * @param memberId 멤버 ID
     */
    @Modifying
    @Query("DELETE FROM FamilyMemberTagMappingJpaEntity m WHERE m.memberId = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    /**
     * 여러 멤버의 모든 태그 매핑을 조회합니다.
     * <p>
     * N+1 문제 해결을 위한 배치 조회 메서드입니다.
     * IN 쿼리를 사용하여 단일 쿼리로 여러 멤버의 매핑을 조회합니다.
     *
     * @param memberIds 멤버 ID 목록
     * @return 태그 매핑 엔티티 목록
     */
    @Query("SELECT m FROM FamilyMemberTagMappingJpaEntity m WHERE m.memberId IN :memberIds")
    List<FamilyMemberTagMappingJpaEntity> findAllByMemberIds(@Param("memberIds") List<Long> memberIds);

    /**
     * 특정 멤버의 모든 태그 매핑을 조회합니다.
     *
     * @param memberId 멤버 ID
     * @return 태그 매핑 엔티티 목록
     */
    List<FamilyMemberTagMappingJpaEntity> findAllByMemberId(Long memberId);

    /**
     * 특정 태그에 매핑된 멤버 수를 조회합니다.
     *
     * @param tagId 태그 ID
     * @return 멤버 수
     */
    int countByTagId(Long tagId);
}
