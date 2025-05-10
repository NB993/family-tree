package io.jhchoe.familytree.core.relationship.application.service;

import io.jhchoe.familytree.core.relationship.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.relationship.application.port.in.SaveFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.relationship.application.port.out.FindFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.application.port.out.SaveFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.domain.FamilyMemberRelationship;
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
public class FindFamilyMemberRelationshipService implements SaveFamilyMemberRelationshipUseCase,
    FindFamilyMemberRelationshipUseCase {

    private final SaveFamilyRelationshipPort saveFamilyRelationshipPort;
    private final FindFamilyRelationshipPort findFamilyRelationshipPort;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long saveRelationship(SaveFamilyMemberRelationshipCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        // 기존 관계가 있는지 확인
        Optional<FamilyMemberRelationship> existingRelationship = findFamilyRelationshipPort.findRelationship(
            command.getFamilyId(),
            command.getFromMemberId(),
            command.getToMemberId()
        );
        
        FamilyMemberRelationship relationship;
        if (existingRelationship.isPresent()) {
            // 기존 관계 업데이트
            relationship = existingRelationship.get().updateRelationship(
                command.getRelationshipType(),
                command.getCustomRelationship(),
                command.getDescription()
            );
        } else {
            // 새 관계 생성
            relationship = FamilyMemberRelationship.createRelationship(
                command.getFamilyId(),
                command.getFromMemberId(),
                command.getToMemberId(),
                command.getRelationshipType(),
                command.getCustomRelationship(),
                command.getDescription()
            );
        }
        
        return saveFamilyRelationshipPort.saveRelationship(relationship);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<FamilyMemberRelationship> findRelationship(FindFamilyMemberRelationshipQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        return findFamilyRelationshipPort.findRelationship(
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

        return findFamilyRelationshipPort.findAllRelationshipsByMember(
            query.getFamilyId(),
            query.getFromMemberId()
        );
    }
}
