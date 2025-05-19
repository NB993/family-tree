package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;

/**
 * Family 가입 신청 저장을 위한 포트 인터페이스입니다.
 */
public interface SaveFamilyJoinRequestPort {

    /**
     * Family 가입 신청을 저장합니다.
     *
     * @param familyJoinRequest 저장할 가입 신청 정보
     * @return 저장된 가입 신청의 ID
     */
    Long save(FamilyJoinRequest familyJoinRequest);
}
