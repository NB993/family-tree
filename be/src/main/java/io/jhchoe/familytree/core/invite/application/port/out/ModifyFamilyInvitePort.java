package io.jhchoe.familytree.core.invite.application.port.out;

import io.jhchoe.familytree.core.invite.domain.FamilyInvite;

/**
 * FamilyInvite 수정 포트입니다.
 */
public interface ModifyFamilyInvitePort {
    
    /**
     * FamilyInvite를 수정합니다.
     *
     * @param familyInvite 수정할 FamilyInvite
     * @return 수정된 FamilyInvite
     */
    FamilyInvite modify(FamilyInvite familyInvite);
}