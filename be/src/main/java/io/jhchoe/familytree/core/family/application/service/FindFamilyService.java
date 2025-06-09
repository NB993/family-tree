package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameContainingQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindMyFamiliesQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindPublicFamiliesQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.CursorPage;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Family 조회 기능을 구현하는 서비스 클래스.
 * 
 * <p>헥사고날 아키텍처의 애플리케이션 계층에서 Family 조회 비즈니스 로직을 담당합니다.
 * 코어 계층에서는 {@code Objects.requireNonNull}을 사용하여 null 체크를 수행합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class FindFamilyService implements FindFamilyUseCase {

    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Family find(FindFamilyByIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.findById(query.id())
            .orElseThrow(() -> FTException.NOT_FOUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Family> findAll(FindFamilyByNameContainingQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.findByNameContaining(query.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Family> findAll(FindMyFamiliesQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. 사용자가 소속된 FamilyMember 목록 조회
        List<FamilyMember> familyMembers = findFamilyMemberPort.findAllByUserId(query.getUserId());

        // 2. FamilyMember에서 Family ID 추출하여 Family 정보 조회
        return familyMembers.stream()
            .map(FamilyMember::getFamilyId)
            .map(familyId -> findFamilyPort.findById(familyId)
                .orElseThrow(() -> FTException.NOT_FOUND))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPage<Family> findAll(FindPublicFamiliesQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 공개 Family 검색 수행 (인프라 계층에서 isPublic=true 필터링 + 키워드 검색 + 커서 페이징 처리)
        return findFamilyPort.findPublicFamiliesByKeyword(
            query.getKeyword(), 
            query.getCursor(), 
            query.getSize()
        );
    }
}
