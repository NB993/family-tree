package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import java.util.List;

/**
 * Family 가입 신청 목록 조회 유스케이스 인터페이스입니다.
 */
public interface FindFamilyJoinRequestUseCase {

    /**
     * 특정 Family의 가입 신청 목록을 조회합니다.
     * 관리자(OWNER/ADMIN) 권한을 가진 사용자만 조회할 수 있습니다.
     *
     * @param query 가입 신청 목록 조회를 위한 쿼리 객체
     * @return Family의 가입 신청 목록
     */
    List<FamilyJoinRequest> findAllByFamilyId(FindFamilyJoinRequestQuery query);
}
