package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ProcessFamilyJoinRequestCommand;
import io.jhchoe.familytree.core.family.application.port.in.ProcessFamilyJoinRequestUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyMemberAuthorizationValidator;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 가입 신청 처리를 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ProcessFamilyJoinRequestService implements ProcessFamilyJoinRequestUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FindFamilyJoinRequestPort findFamilyJoinRequestPort;
    private final ModifyFamilyJoinRequestPort modifyFamilyJoinRequestPort;
    private final SaveFamilyMemberPort saveFamilyMemberPort;
    private final FindUserPort findUserPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public FamilyJoinRequest process(final ProcessFamilyJoinRequestCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. 현재 사용자가 해당 Family의 구성원인지 확인하고 권한 검증
        FamilyMember currentMember = findFamilyMemberPort.findByFamilyIdAndUserId(
                command.getFamilyId(), command.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 2. OWNER 또는 ADMIN 권한 검증
        FamilyMemberAuthorizationValidator.validateRoleAndStatus(currentMember, FamilyMemberRole.ADMIN);

        // 3. 가입 신청 조회
        FamilyJoinRequest joinRequest = findFamilyJoinRequestPort.findById(command.getRequestId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.JOIN_REQUEST_NOT_FOUND));

        // 4. Family ID 일치 확인
        if (!joinRequest.getFamilyId().equals(command.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.JOIN_REQUEST_NOT_FOUND);
        }

        // 5. 처리 가능한 상태인지 확인 (PENDING 상태만 처리 가능)
        if (!joinRequest.canBeProcessed()) {
            throw new FTException(FamilyExceptionCode.JOIN_REQUEST_ALREADY_PROCESSED);
        }

        // 6. 상태에 따른 처리
        FamilyJoinRequest processedRequest = processJoinRequest(command, joinRequest);

        // 7. 가입 신청 상태 업데이트
        return modifyFamilyJoinRequestPort.updateFamilyJoinRequest(processedRequest);
    }

    private FamilyJoinRequest processJoinRequest(ProcessFamilyJoinRequestCommand command,
        FamilyJoinRequest joinRequest) {
        if (command.getStatus() == FamilyJoinRequestStatus.APPROVED) {
            FamilyJoinRequest approvedRequest = joinRequest.approve();
            createAndSaveNewFamilyMember(command.getFamilyId(), joinRequest.getRequesterId());
            return approvedRequest;
        }
        return joinRequest.reject();
    }

    private void createAndSaveNewFamilyMember(Long familyId, Long requesterId) {
        // User 정보 조회
        User user = findUserPort.findById(requesterId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.USER_NOT_FOUND));

        // FamilyMember 생성 (User 정보를 복사)
        FamilyMember newMember = FamilyMember.newMember(
            familyId,
            requesterId,
            user.getName(),          // User에서 복사
            user.getProfileUrl(),    // User에서 복사
            user.getBirthday(),      // User에서 복사
            user.getBirthdayType()   // User에서 복사
        );
        saveFamilyMemberPort.save(newMember);
    }
}
