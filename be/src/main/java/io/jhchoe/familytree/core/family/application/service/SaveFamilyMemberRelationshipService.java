package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
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
    private final FamilyValidationService familyValidationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveFamilyMemberRelationshipCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(command.getToMemberId(), "toMemberId must not be null");
        familyValidationService.validateFamilyExists(command.getFamilyId());
        familyValidationService.validateFamilyAccess(command.getFamilyId(), command.getFromMemberId());

        FamilyMemberRelationship familyMemberRelationship = getFamilyMemberRelationship(command);
        return saveFamilyMemberRelationshipPort.save(familyMemberRelationship);
    }

    private FamilyMemberRelationship getFamilyMemberRelationship(SaveFamilyMemberRelationshipCommand command) {
        Optional<FamilyMemberRelationship> existingRelationship = findFamilyMemberRelationshipPort.findByFamilyIdAndFromMemberIdAndToMemberId(
            command.getFamilyId(),
            command.getFromMemberId(),
            command.getToMemberId()
        );
        if (existingRelationship.isPresent()) {
            // 기존 관계 업데이트
            FamilyMemberRelationship updateRelationship = existingRelationship.get().update(
                command.getRelationshipType(),
                command.getCustomRelationship(),
                command.getDescription()
            );
            return updateRelationship;
        }

        // 새 관계 생성
        FamilyMemberRelationship newRelationship = FamilyMemberRelationship.newRelationship(
            command.getFamilyId(),
            command.getFromMemberId(),
            command.getToMemberId(),
            command.getRelationshipType(),
            command.getCustomRelationship(),
            command.getDescription()
        );
        return newRelationship;
    }
}
