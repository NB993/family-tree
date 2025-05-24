package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import java.util.List;
import java.util.Optional;

/**
 * Family 가입 신청 조회를 위한 포트 인터페이스입니다.
 */
public interface FindFamilyJoinRequestPort {

    /**
     * ID로 Family 가입 신청을 조회합니다.
     *
     * @param id 조회할 가입 신청 ID
     * @return 가입 신청 정보를 Optional로 반환
     */
    Optional<FamilyJoinRequest> findById(Long id);

    /**
     * 가장 최근의 Family 가입 신청을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param requesterId 조회할 신청자 ID
     * @return 가장 최근의 가입 신청 정보를 Optional로 반환
     */
    Optional<FamilyJoinRequest> findLatestByFamilyIdAndRequesterId(Long familyId, Long requesterId);

    /**
     * 특정 Family의 모든 가입 신청 목록을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @return Family의 가입 신청 목록
     */
    List<FamilyJoinRequest> findAllByFamilyId(Long familyId);
}
