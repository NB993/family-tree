package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteByCodeQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteByIdQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInvitesByRequesterIdQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteUseCase;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FindFamilyInviteService는 초대 조회 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyInviteService implements FindFamilyInviteUseCase {

    private final FindFamilyInvitePort findFamilyInvitePort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FamilyInvite find(final FindFamilyInviteByCodeQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        FamilyInvite familyInvite = findFamilyInvitePort.findByInviteCode(query.inviteCode())
            .orElseThrow(() -> new FTException(InviteExceptionCode.INVITE_NOT_FOUND));

        if (familyInvite.isExpired()) {
            throw new FTException(InviteExceptionCode.INVITE_EXPIRED);
        }

        return familyInvite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FamilyInvite find(final FindFamilyInviteByIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyInvitePort.findById(query.id())
            .orElseThrow(() -> new FTException(InviteExceptionCode.INVITE_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FamilyInvite> findAll(final FindFamilyInvitesByRequesterIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyInvitePort.findByRequesterId(query.requesterId());
    }
}
