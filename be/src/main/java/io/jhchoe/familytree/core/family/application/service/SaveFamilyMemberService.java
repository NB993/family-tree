package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가족 구성원 수동 등록 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class SaveFamilyMemberService implements SaveFamilyMemberUseCase {

    private final SaveFamilyMemberPort saveFamilyMemberPort;
    private final FamilyValidationService familyValidationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveFamilyMemberCommand command, Long currentUserId) {
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(currentUserId, "currentUserId must not be null");

        familyValidationService.validateFamilyExists(command.getFamilyId());
        familyValidationService.validateFamilyAccess(command.getFamilyId(), currentUserId);

        FamilyMember familyMember = FamilyMember.newManualMember(
            command.getFamilyId(),
            command.getName(),
            command.getRelationshipType(),
            command.getCustomRelationship(),
            command.getBirthday()
        );

        return saveFamilyMemberPort.save(familyMember);
    }
}
