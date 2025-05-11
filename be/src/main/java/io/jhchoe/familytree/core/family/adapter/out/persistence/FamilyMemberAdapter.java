package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FamilyMemberAdapter implements FindFamilyMemberPort {

    @Override
    public boolean existsByFamilyIdAndUserId(Long familyId, Long userId) {
        return false;
    }
}
