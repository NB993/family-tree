package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteUseCase;
import io.jhchoe.familytree.core.invite.application.port.out.SaveFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String save(final SaveFamilyInviteCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 새로운 초대 생성 (기본 5회)
        final FamilyInvite familyInvite = FamilyInvite.newInvite(command.familyId(), command.requesterId(), null);

        // 초대 저장
        final FamilyInvite savedInvite = saveFamilyInvitePort.save(familyInvite);

        return savedInvite.getInviteCode();
    }
}
