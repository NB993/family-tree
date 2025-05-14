package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 가족 관계 저장 및 조회를 위한 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyMemberRelationshipAdapter implements SaveFamilyMemberRelationshipPort, FindFamilyMemberRelationshipPort {

    private final FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(FamilyMemberRelationship familyMemberRelationship) {
        Objects.requireNonNull(familyMemberRelationship, "relationship must not be null");
        
        FamilyMemberRelationshipJpaEntity entity = FamilyMemberRelationshipJpaEntity.from(familyMemberRelationship);
        return familyMemberRelationshipJpaRepository.save(entity).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyMemberRelationship> findByFamilyIdAndFromMemberIdAndToMemberId(Long familyId, Long fromMemberId, Long toMemberId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        Objects.requireNonNull(toMemberId, "toMemberId must not be null");
        
        return familyMemberRelationshipJpaRepository
            .findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId)
            .map(FamilyMemberRelationshipJpaEntity::toFamilyRelationship);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberRelationship> findAllByFamilyIdAndFromMemberId(Long familyId, Long fromMemberId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        
        return familyMemberRelationshipJpaRepository
            .findAllByFamilyIdAndFromMemberId(familyId, fromMemberId)
            .stream()
            .map(FamilyMemberRelationshipJpaEntity::toFamilyRelationship)
            .collect(Collectors.toList());
    }
}
