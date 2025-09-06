package io.jhchoe.familytree.core.invite.application.port.out;

import io.jhchoe.familytree.core.invite.domain.FamilyInvite;

/**
 * SaveFamilyInvitePort는 가족 초대를 저장하기 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface SaveFamilyInvitePort {
    
    /**
     * 가족 초대를 저장합니다.
     *
     * @param familyInvite 저장할 가족 초대 객체
     * @return 저장된 가족 초대 객체
     */
    FamilyInvite save(FamilyInvite familyInvite);
}