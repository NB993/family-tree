package io.jhchoe.familytree.core.relationship.application.service;

import io.jhchoe.familytree.core.relationship.application.port.in.DefineFamilyRelationshipCommand;
import io.jhchoe.familytree.core.relationship.application.port.in.DefineFamilyRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyRelationshipQuery;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindMemberRelationshipsQuery;
import io.jhchoe.familytree.core.relationship.application.port.out.FindFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.application.port.out.SaveFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
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
public class FamilyRelationshipService implements DefineFamilyRelationshipUseCase, FindFamilyRelationshipUseCase {
    
    private final SaveFamilyRelationshipPort saveFamilyRelationshipPort;
    private final FindFamilyRelationshipPort findFamilyRelationshipPort;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long defineRelationship(DefineFamilyRelationshipCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        // 기존 관계가 있는지 확인
        Optional<FamilyRelationship> existingRelationship = findFamilyRelationshipPort.findRelationship(
            command.getFamilyId(),
            command.getFromMemberId(),
            command.getToMemberId()
        );
        
        FamilyRelationship relationship;
        if (existingRelationship.isPresent()) {
            // 기존 관계 업데이트
            relationship = existingRelationship.get().updateRelationship(
                command.getRelationshipType(),
                command.getCustomRelationship(),
                command.getDescription()
            );
        } else {
            // 새 관계 생성
            relationship = FamilyRelationship.createRelationship(
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
    public Optional<FamilyRelationship> findRelationship(FindFamilyRelationshipQuery query) {
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
    public List<FamilyRelationship> findAllRelationshipsByMember(FindMemberRelationshipsQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        return findFamilyRelationshipPort.findAllRelationshipsByMember(
            query.getFamilyId(),
            query.getFromMemberId()
        );
    }
}
