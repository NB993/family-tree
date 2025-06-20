package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyMemberAuthorizationValidator;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 수정 유스케이스를 구현하는 서비스 클래스입니다.
 * <p>
 * 이 클래스는 ModifyFamilyUseCase 인터페이스를 구현하여
 * Family 엔티티의 수정 비즈니스 로직을 처리합니다.
 * </p>
 */
@RequiredArgsConstructor
@Transactional
@Service
public class ModifyFamilyService implements ModifyFamilyUseCase {

    private final FindFamilyPort findFamilyPort;
    private final ModifyFamilyPort modifyFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FamilyValidationService familyValidationService;

    
    @Override
    public Long modify(ModifyFamilyCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. Family 존재 여부 확인
        familyValidationService.validateFamilyExists(command.getId());
        
        // 2. 구성원 여부 확인  
        familyValidationService.validateFamilyAccess(command.getId(), command.getUserId());
        
        // 3. 구성원 정보 조회
        FamilyMember member = findFamilyMemberPort.findByFamilyIdAndUserId(command.getId(), command.getUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 4. OWNER 권한 검증 (새로 추가)
        FamilyMemberAuthorizationValidator.validateFamilyModificationPermission(member);

        Family family = findFamilyPort.findById(command.getId())
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "family"));
        // 기존 isPublic 값 유지하면서 다른 필드만 업데이트
        family.update(command.getName(), command.getDescription(), command.getProfileUrl(), family.getIsPublic());

        return modifyFamilyPort.modifyFamily(family);
    }
}
