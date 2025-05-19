package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Family 가입 신청 JPA Repository 인터페이스입니다.
 */
public interface FamilyJoinRequestJpaRepository extends JpaRepository<FamilyJoinRequestJpaEntity, Long> {

    /**
     * 특정 Family와 신청자에 대한 가장 최근의 가입 신청을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param requesterId 조회할 신청자 ID
     * @return 가장 최근의 가입 신청 정보를 Optional로 반환
     */
    @Query("SELECT fjr FROM family_join_request fjr " +
           "WHERE fjr.familyId = :familyId AND fjr.requesterId = :requesterId " +
           "ORDER BY fjr.id DESC")
    Optional<FamilyJoinRequestJpaEntity> findTopByFamilyIdAndRequesterIdOrderByIdDesc(
        @Param("familyId") Long familyId,
        @Param("requesterId") Long requesterId
    );
}
