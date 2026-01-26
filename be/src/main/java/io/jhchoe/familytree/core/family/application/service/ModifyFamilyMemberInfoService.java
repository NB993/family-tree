package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberInfoCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberInfoUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 구성원의 기본 정보(이름, 생일, 생일타입) 변경을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyFamilyMemberInfoService implements ModifyFamilyMemberInfoUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final ModifyFamilyMemberPort modifyFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long modifyInfo(ModifyFamilyMemberInfoCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. 현재 사용자가 해당 Family의 구성원인지 확인하고 역할 검증
        FamilyMember currentMember = findFamilyMemberPort.findByFamilyIdAndUserId(
                command.familyId(), command.currentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 2. OWNER 또는 ADMIN만 정보 변경 가능
        if (!currentMember.hasRoleAtLeast(FamilyMemberRole.ADMIN)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }

        // 3. 대상 구성원 조회
        FamilyMember targetMember = findFamilyMemberPort.findById(command.memberId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));

        // 4. Family ID 일치 확인
        if (!targetMember.getFamilyId().equals(command.familyId())) {
            throw new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND);
        }

        // 5. 본인 정보 수정 불가 (프로필 설정에서 처리)
        if (targetMember.getUserId() != null && targetMember.getUserId().equals(command.currentUserId())) {
            throw new FTException(FamilyExceptionCode.SELF_MODIFICATION_NOT_ALLOWED);
        }

        // 6. 초대된 멤버(userId 있음)의 생일/생일타입은 본인만 수정 가능
        if (targetMember.getUserId() != null && (command.birthday() != null || command.birthdayType() != null)) {
            throw new FTException(FamilyExceptionCode.INVITED_MEMBER_BIRTHDAY_MODIFICATION_NOT_ALLOWED);
        }

        // 7. 정보 변경
        FamilyMember updatedMember = targetMember.modifyInfo(
            command.name(),
            command.birthday(),
            command.birthdayType()
        );

        // 8. 저장
        return modifyFamilyMemberPort.modify(updatedMember);
    }
}
