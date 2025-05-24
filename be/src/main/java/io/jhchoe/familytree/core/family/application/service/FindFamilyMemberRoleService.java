package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 구성원 역할 조회를 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyMemberRoleService implements FindFamilyMembersRoleUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FindFamilyPort findFamilyPort;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FamilyMember> findAllByFamilyId(FindFamilyMembersRoleQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        // 1. Family가 존재하는지 먼저 확인
        if (!findFamilyPort.existsById(query.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND);
        }
        
        // 2. 현재 사용자가 해당 Family의 구성원인지 확인
        findFamilyMemberPort.findByFamilyIdAndUserId(
                query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 3. 구성원 목록 조회
        return findFamilyMemberPort.findAllByFamilyId(query.getFamilyId());
    }
}
