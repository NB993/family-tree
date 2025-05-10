package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
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
public class SaveFamilyMemberRelationshipService implements SaveFamilyMemberRelationshipUseCase {

    private final SaveFamilyMemberRelationshipPort saveFamilyMemberRelationshipPort;
    private final FindFamilyMemberRelationshipPort findFamilyMemberRelationshipPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long saveRelationship(SaveFamilyMemberRelationshipCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 현재 사용자가 가족의 구성원인지 확인
//        boolean isFamilyMember = familyMemberRepository.existsByFamilyIdAndUserId(command.getFamilyId(), command.getFromMemberId());
//        if (!isFamilyMember) {
//            throw new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER);
//        }
        
        // 기존 관계가 있는지 확인
        Optional<FamilyMemberRelationship> existingRelationship = findFamilyMemberRelationshipPort.findRelationship(
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
        
        return saveFamilyMemberRelationshipPort.saveRelationship(relationship);
    }
}
