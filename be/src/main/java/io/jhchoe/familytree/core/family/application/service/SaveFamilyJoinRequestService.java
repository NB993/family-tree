package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyJoinRequestCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyJoinRequestUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 가입 신청을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class SaveFamilyJoinRequestService implements SaveFamilyJoinRequestUseCase {

    private static final int MAX_FAMILY_JOIN_LIMIT = 5;

    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FindFamilyJoinRequestPort findFamilyJoinRequestPort;
    private final SaveFamilyJoinRequestPort saveFamilyJoinRequestPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveFamilyJoinRequestCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. Family 존재 여부 확인
        if (!findFamilyPort.existsById(command.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND);
        }

        // 2. 이미 가입된 Family인지 확인
        if (findFamilyMemberPort.existsByFamilyIdAndUserId(command.getFamilyId(), command.getRequesterId())) {
            throw new FTException(FamilyExceptionCode.ALREADY_JOINED_FAMILY, "가입된 패밀리입니다.");
        }

        // 3. 최근 가입 신청 상태 확인
        Optional<FamilyJoinRequest> latestRequest = findFamilyJoinRequestPort
            .findLatestByFamilyIdAndRequesterId(command.getFamilyId(), command.getRequesterId());

        if (latestRequest.isPresent()) {
            FamilyJoinRequest request = latestRequest.get();
            if (request.isPending()) {
                throw new FTException(FamilyExceptionCode.JOIN_REQUEST_ALREADY_PENDING, "가입 신청을 처리중이에요.");
            }
            if (request.isApproved()) {
                throw new FTException(FamilyExceptionCode.ALREADY_JOINED_FAMILY, "가입된 패밀리입니다.");
            }
            // 거절된 경우 다시 신청 가능
        }

        // 4. 가입 가능 수 제한 확인
        //todo 수정 1
        int approvedCount = findFamilyJoinRequestPort.countByRequesterIdAndStatusApproved(command.getRequesterId());
        if (approvedCount >= MAX_FAMILY_JOIN_LIMIT) {
            throw new FTException(FamilyExceptionCode.EXCEEDED_FAMILY_JOIN_LIMIT);
        }

        // 5. 신규 가입 신청 생성 및 저장
        FamilyJoinRequest newRequest = FamilyJoinRequest.newRequest(
            command.getFamilyId(),
            command.getRequesterId()
        );

        return saveFamilyJoinRequestPort.save(newRequest);
    }
}
