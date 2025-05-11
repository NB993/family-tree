package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FamilyMemberAdapter implements FindFamilyMemberPort {

    private final FamilyMemberJpaRepository familyMemberJpaRepository;

    @Override
    public boolean existsByFamilyIdAndUserId(Long familyId, Long userId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        return familyMemberJpaRepository.existsByFamilyIdAndUserId(familyId, userId);
    }
}
