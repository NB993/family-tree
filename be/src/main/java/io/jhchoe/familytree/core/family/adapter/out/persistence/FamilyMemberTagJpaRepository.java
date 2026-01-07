package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * FamilyMemberTag에 대한 JPA 리포지토리 인터페이스입니다.
 */
public interface FamilyMemberTagJpaRepository extends JpaRepository<FamilyMemberTagJpaEntity, Long> {

    /**
     * 특정 Family의 모든 태그를 조회합니다.
     *
     * @param familyId Family ID
     * @return 태그 엔티티 목록
     */
    List<FamilyMemberTagJpaEntity> findAllByFamilyId(Long familyId);

    /**
     * 특정 Family에서 태그 이름으로 태그를 조회합니다.
     *
     * @param familyId Family ID
     * @param name     태그 이름
     * @return 태그 엔티티 (없으면 빈 Optional)
     */
    Optional<FamilyMemberTagJpaEntity> findByFamilyIdAndName(Long familyId, String name);

    /**
     * 특정 Family의 태그 수를 반환합니다.
     *
     * @param familyId Family ID
     * @return 태그 수
     */
    int countByFamilyId(Long familyId);
}
