package io.jhchoe.familytree.core.relationship.adapter.out.persistence;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.relationship.application.port.out.FindFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.application.port.out.SaveFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
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
public class FamilyRelationshipAdapter implements SaveFamilyRelationshipPort, FindFamilyRelationshipPort {

    private final FamilyRelationshipJpaRepository familyRelationshipJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long saveRelationship(FamilyRelationship relationship) {
        Objects.requireNonNull(relationship, "relationship must not be null");
        
        FamilyRelationshipJpaEntity entity = FamilyRelationshipJpaEntity.from(relationship);
        return familyRelationshipJpaRepository.save(entity).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FamilyRelationship> findRelationship(Long familyId, Long fromMemberId, Long toMemberId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        Objects.requireNonNull(toMemberId, "toMemberId must not be null");
        
        return familyRelationshipJpaRepository
            .findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId)
            .map(FamilyRelationshipJpaEntity::toFamilyRelationship);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyRelationship> findAllRelationshipsByMember(Long familyId, Long fromMemberId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        
        return familyRelationshipJpaRepository
            .findAllByFamilyIdAndFromMemberId(familyId, fromMemberId)
            .stream()
            .map(FamilyRelationshipJpaEntity::toFamilyRelationship)
            .collect(Collectors.toList());
    }
}
