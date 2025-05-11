package io.jhchoe.familytree.core.family.application.port.out;

public interface FindFamilyMemberPort {

    boolean existsByFamilyIdAndUserId(Long familyId, Long userId);
}
