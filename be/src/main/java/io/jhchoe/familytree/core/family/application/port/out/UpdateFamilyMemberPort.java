package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMember;

/**
 * Family 구성원 정보를 수정하기 위한 포트입니다.
 */
public interface UpdateFamilyMemberPort {

    /**
     * 구성원 정보를 업데이트합니다.
     *
     * @param familyMember 업데이트할 FamilyMember 객체
     * @return 업데이트된 FamilyMember의 ID
     */
    Long update(FamilyMember familyMember);
}
