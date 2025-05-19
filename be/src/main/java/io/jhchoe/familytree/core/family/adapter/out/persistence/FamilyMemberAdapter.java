package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Family 멤버 관련 아웃바운드 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyMemberAdapter implements FindFamilyMemberPort {

    private final FamilyMemberJpaRepository familyMemberJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByFamilyIdAndUserId(Long familyId, Long userId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        return familyMemberJpaRepository.existsByFamilyIdAndUserId(familyId, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countActiveByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        
        return familyMemberJpaRepository.countByUserIdAndStatus(userId, FamilyMemberStatus.ACTIVE);
    }
}
