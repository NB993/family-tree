package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Family 가입 신청 JPA Repository 인터페이스입니다.
 */
public interface FamilyJoinRequestJpaRepository extends JpaRepository<FamilyJoinRequestJpaEntity, Long> {

    /**
     * 특정 Family와 신청자에 대한 가장 최근의 가입 신청을 조회합니다.
     * 메서드 이름 기반 쿼리 대신 JPQL @Query를 사용했었던 이유:
     * - 결과를 ID 내림차순으로 정렬한 후 첫 번째 결과만 가져오는 로직이 필요했음
     * - 메서드 이름만으로는 정렬 조건과 최상위 결과만 가져오는 의도를 명확히 표현하기 어려움
     * - 하지만 Spring Data JPA의 메서드 명명 규칙으로도 동일한 기능 구현 가능
     *
     * @param familyId 조회할 Family ID
     * @param requesterId 조회할 신청자 ID
     * @return 가장 최근의 가입 신청 정보를 Optional로 반환
     */
    Optional<FamilyJoinRequestJpaEntity> findTopByFamilyIdAndRequesterIdOrderByIdDesc(
        Long familyId, Long requesterId
    );
}
