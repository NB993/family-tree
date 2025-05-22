package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRoleCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRoleUseCase;
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
 * Family 구성원 역할 변경을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyFamilyMemberRoleService implements ModifyFamilyMemberRoleUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final ModifyFamilyMemberPort modifyFamilyMemberPort;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long modifyRole(ModifyFamilyMemberRoleCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        // 1. 현재 사용자가 해당 Family의 구성원인지 확인하고 역할 검증
        FamilyMember currentMember = findFamilyMemberPort.findByFamilyIdAndUserId(
                command.getFamilyId(), command.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 2. OWNER만 역할 변경 가능
        if (currentMember.getRole() != FamilyMemberRole.OWNER) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
        
        // 3. 대상 구성원 조회
        FamilyMember targetMember = findFamilyMemberPort.findById(command.getMemberId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));
        
        // 4. Family ID 일치 확인
        if (!targetMember.getFamilyId().equals(command.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND);
        }
        
        try {
            // 5. 역할 변경
            FamilyMember updatedMember = targetMember.updateRole(command.getNewRole());
            
            // 6. 저장
            return modifyFamilyMemberPort.modify(updatedMember);
        } catch (IllegalStateException e) {
            throw new FTException(FamilyExceptionCode.CANNOT_CHANGE_OWNER_ROLE);
        }
    }
}
