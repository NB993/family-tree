package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import java.util.List;
import java.util.Optional;

/**
 * Family 가입 신청 조회를 위한 포트 인터페이스입니다.
 */
public interface FindFamilyJoinRequestPort {

    /**
     * 가장 최근의 Family 가입 신청을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param requesterId 조회할 신청자 ID
     * @return 가장 최근의 가입 신청 정보를 Optional로 반환
     */
    Optional<FamilyJoinRequest> findLatestByFamilyIdAndRequesterId(Long familyId, Long requesterId);
    
    /**
     * 특정 상태의 Family 가입 신청을 조회합니다.
     *
     * @param requesterId 조회할 신청자 ID
     * @param statuses 조회할 상태 목록
     * @return 특정 상태의 가입 신청 목록
     */
    // todo 수정 2
    List<FamilyJoinRequest> findByRequesterIdAndStatusIn(Long requesterId, List<FamilyJoinRequestStatus> statuses);
    
    /**
     * 특정 사용자의 모든 승인된 Family 가입 신청 수를 조회합니다.
     *
     * @param requesterId 조회할 신청자 ID
     * @return 승인된 가입 신청 수
     */
    int countByRequesterIdAndStatusApproved(Long requesterId);
}
