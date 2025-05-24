package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMember;

/**
 * Family 구성원 저장을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface SaveFamilyMemberPort {

    /**
     * 새로운 Family 구성원을 저장합니다.
     *
     * @param familyMember 저장할 구성원 정보
     * @return 저장된 구성원의 ID
     */
    Long save(FamilyMember familyMember);
}
