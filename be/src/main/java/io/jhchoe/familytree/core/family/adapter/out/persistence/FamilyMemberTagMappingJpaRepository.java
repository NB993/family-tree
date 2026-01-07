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
     *
     * @param memberId 멤버 ID
     */
    @Modifying
    @Query("DELETE FROM FamilyMemberTagMappingJpaEntity m WHERE m.memberId = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

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
