package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyJoinRequestQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyJoinRequestUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyMemberAuthorizationValidator;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 가입 신청 목록 조회를 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyJoinRequestService implements FindFamilyJoinRequestUseCase {

    private final FindFamilyJoinRequestPort findFamilyJoinRequestPort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FindFamilyPort findFamilyPort;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FamilyJoinRequest> findAllByFamilyId(FindFamilyJoinRequestQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        // 1. Family가 존재하는지 먼저 확인
        if (!findFamilyPort.existsById(query.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND);
        }
        
        // 2. 현재 사용자가 해당 Family의 구성원인지 확인
        FamilyMember currentMember = findFamilyMemberPort.findByFamilyIdAndUserId(
                query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 3. 권한 검증 - ADMIN 이상 권한 필요
        FamilyMemberAuthorizationValidator.validateRoleAndStatus(currentMember, FamilyMemberRole.ADMIN);
        
        // 4. 가입 신청 목록 조회
        return findFamilyJoinRequestPort.findAllByFamilyId(query.getFamilyId());
    }
}
