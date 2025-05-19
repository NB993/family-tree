package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMemberJpaEntity, Long> {

    /**
     * 특정 가족에 특정 사용자가 구성원으로 존재하는지 확인합니다.
     *
     * @param familyId 확인할 가족 ID
     * @param userId 확인할 사용자 ID
     * @return 가족 구성원으로 존재하면 true, 아니면 false
     */
    boolean existsByFamilyIdAndUserId(Long familyId, Long userId);
    
    /**
     * 특정 사용자가 활성 상태로 가입한 Family의 수를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @param status 조회할 상태
     * @return 활성 상태로 가입한 Family 수
     */
    int countByUserIdAndStatus(Long userId, FamilyMemberStatus status);
}
