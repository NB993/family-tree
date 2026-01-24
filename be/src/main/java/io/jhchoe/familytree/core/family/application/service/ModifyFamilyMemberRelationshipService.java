package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 구성원의 관계 정보 변경을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyFamilyMemberRelationshipService implements ModifyFamilyMemberRelationshipUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final ModifyFamilyMemberPort modifyFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long modify(ModifyFamilyMemberRelationshipCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. 현재 사용자가 해당 Family의 구성원인지 확인
        findFamilyMemberPort.findByFamilyIdAndUserId(command.familyId(), command.currentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 2. 대상 구성원 조회
        FamilyMember targetMember = findFamilyMemberPort.findById(command.memberId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));

        // 3. Family ID 일치 확인
        if (!targetMember.getFamilyId().equals(command.familyId())) {
            throw new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND);
        }

        // 4. 관계 변경
        FamilyMember updatedMember = targetMember.updateRelationship(
            command.relationshipType(),
            command.customRelationship()
        );

        // 5. 저장
        return modifyFamilyMemberPort.modify(updatedMember);
    }
}
