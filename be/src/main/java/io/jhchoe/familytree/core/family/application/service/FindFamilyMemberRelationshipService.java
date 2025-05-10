package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가족 관계 관리 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyMemberRelationshipService implements FindFamilyMemberRelationshipUseCase {

    private final FindFamilyMemberRelationshipPort findFamilyMemberRelationshipPort;
    private final FamilyMemberJpaRepository familyMemberRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<FamilyMemberRelationship> findRelationship(FindFamilyMemberRelationshipQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        return findFamilyMemberRelationshipPort.findRelationship(
            query.getFamilyId(),
            query.getFromMemberId(),
            query.getToMemberId()
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FamilyMemberRelationship> findAllRelationshipsByMember(FindFamilyMemberRelationshipsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyMemberRelationshipPort.findAllRelationshipsByMember(
            query.getFamilyId(),
            query.getFromMemberId()
        );
    }
}
