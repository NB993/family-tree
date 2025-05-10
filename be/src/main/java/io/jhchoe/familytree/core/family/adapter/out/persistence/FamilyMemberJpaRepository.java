package io.jhchoe.familytree.core.family.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMemberJpaEntity, Long> {

    /**
     * 특정 가족에 특정 사용자가 구성원으로 존재하는지 확인합니다.
     *
     * @param familyId 확인할 가족 ID
     * @param userId 확인할 사용자 ID
     * @return 가족 구성원으로 존재하면 true, 아니면 false
     */
    boolean existsByFamilyIdAndUserId(Long familyId, Long userId);
}
