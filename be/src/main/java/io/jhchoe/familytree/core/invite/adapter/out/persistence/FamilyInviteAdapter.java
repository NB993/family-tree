package io.jhchoe.familytree.core.invite.adapter.out.persistence;

import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.application.port.out.SaveFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FamilyInvite 관련 아웃바운드 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyInviteAdapter implements SaveFamilyInvitePort, FindFamilyInvitePort {

    private final FamilyInviteJpaRepository familyInviteJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public FamilyInvite save(final FamilyInvite familyInvite) {
        Objects.requireNonNull(familyInvite, "familyInvite must not be null");

        final FamilyInviteJpaEntity jpaEntity = FamilyInviteJpaEntity.from(familyInvite);
        final FamilyInviteJpaEntity savedEntity = familyInviteJpaRepository.save(jpaEntity);
        
        return savedEntity.toFamilyInvite();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyInvite> findById(final Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return familyInviteJpaRepository.findById(id)
            .map(FamilyInviteJpaEntity::toFamilyInvite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyInvite> findByInviteCode(final String inviteCode) {
        Objects.requireNonNull(inviteCode, "inviteCode must not be null");

        return familyInviteJpaRepository.findByInviteCode(inviteCode)
            .map(FamilyInviteJpaEntity::toFamilyInvite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyInvite> findByRequesterId(final Long requesterId) {
        Objects.requireNonNull(requesterId, "requesterId must not be null");

        return familyInviteJpaRepository.findByRequesterId(requesterId)
            .stream()
            .map(FamilyInviteJpaEntity::toFamilyInvite)
            .collect(Collectors.toList());
    }
}