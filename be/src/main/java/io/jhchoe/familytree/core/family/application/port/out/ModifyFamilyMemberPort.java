package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMember;

/**
 * Family 구성원 정보를 수정하기 위한 포트입니다.
 */
public interface ModifyFamilyMemberPort {

    /**
     * 구성원 정보를 수정합니다.
     *
     * @param familyMember 수정할 FamilyMember 객체
     * @return 수정된 FamilyMember의 ID
     */
    Long modify(FamilyMember familyMember);
}
