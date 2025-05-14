package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindAllFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import java.util.List;
import java.util.Objects;
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FamilyMemberRelationship find(FindFamilyMemberRelationshipQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        return findFamilyMemberRelationshipPort.findByFamilyIdAndFromMemberIdAndToMemberId(
            query.getFamilyId(),
            query.getFromMemberId(),
            query.getToMemberId()
        ).orElseThrow(() -> FTException.NOT_FOUND);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FamilyMemberRelationship> findAll(FindAllFamilyMemberRelationshipsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyMemberRelationshipPort.findAllByFamilyIdAndFromMemberId(
            query.getFamilyId(),
            query.getFromMemberId()
        );
    }
}
