package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteUseCase;
import io.jhchoe.familytree.core.invite.application.port.out.SaveFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SaveFamilyInviteService는 초대 생성 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class SaveFamilyInviteService implements SaveFamilyInviteUseCase {

    private final SaveFamilyInvitePort saveFamilyInvitePort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String save(final SaveFamilyInviteCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 사용자가 OWNER인 Family 조회
        final FamilyMember ownerMember = findFamilyMemberPort
            .findByUserIdAndRole(command.requesterId(), FamilyMemberRole.OWNER)
            .orElseThrow(() -> new FTException(InviteExceptionCode.NOT_FAMILY_OWNER));

        final Long familyId = ownerMember.getFamilyId();

        // 새로운 초대 생성 (기본값 사용)
        final FamilyInvite familyInvite = FamilyInvite.newInvite(familyId, command.requesterId());

        // 초대 저장
        final FamilyInvite savedInvite = saveFamilyInvitePort.save(familyInvite);

        return savedInvite.getInviteCode();
    }
}
