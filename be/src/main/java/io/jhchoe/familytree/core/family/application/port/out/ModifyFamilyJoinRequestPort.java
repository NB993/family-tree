package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;

/**
 * Family 가입 신청 수정을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface ModifyFamilyJoinRequestPort {

    /**
     * 가입 신청 정보를 업데이트합니다.
     *
     * @param familyJoinRequest 업데이트할 가입 신청 정보
     * @return 업데이트된 FamilyJoinRequest 객체
     */
    FamilyJoinRequest updateFamilyJoinRequest(FamilyJoinRequest familyJoinRequest);
}
