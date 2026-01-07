package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagInfo;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagsQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 태그 조회 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyMemberTagService implements FindFamilyMemberTagUseCase {

    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final FindFamilyMemberTagMappingPort findFamilyMemberTagMappingPort;
    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FamilyMemberTagInfo> findAll(FindFamilyMemberTagsQuery query, Long currentUserId) {
        Objects.requireNonNull(query, "query는 null일 수 없습니다");
        Objects.requireNonNull(currentUserId, "currentUserId는 null일 수 없습니다");

        Long familyId = query.familyId();

        // 1. Family 존재 여부 확인
        validateFamilyExists(familyId);

        // 2. 현재 사용자가 Family 구성원인지 확인
        validateFamilyMember(familyId, currentUserId);

        // 3. 태그 목록 조회
        List<FamilyMemberTag> tags = findFamilyMemberTagPort.findAllByFamilyId(familyId);

        // 4. 각 태그별 멤버 수 조회 및 가나다순 정렬
        return tags.stream()
            .map(tag -> FamilyMemberTagInfo.fromDomain(
                tag,
                findFamilyMemberTagMappingPort.countByTagId(tag.getId())
            ))
            .sorted(Comparator.comparing(FamilyMemberTagInfo::name))
            .toList();
    }

    private void validateFamilyExists(Long familyId) {
        if (!findFamilyPort.existsById(familyId)) {
            throw new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND);
        }
    }

    private void validateFamilyMember(Long familyId, Long userId) {
        findFamilyMemberPort.findByFamilyIdAndUserId(familyId, userId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
    }
}
