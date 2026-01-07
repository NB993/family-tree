package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.MemberTagsInfo;
import io.jhchoe.familytree.core.family.application.port.in.MemberTagsInfo.TagSimpleInfo;
import io.jhchoe.familytree.core.family.application.port.in.ModifyMemberTagsCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyMemberTagsUseCase;
import io.jhchoe.familytree.core.family.application.port.out.DeleteFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 멤버에 태그를 할당/해제하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyMemberTagsService implements ModifyMemberTagsUseCase {

    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final SaveFamilyMemberTagMappingPort saveFamilyMemberTagMappingPort;
    private final DeleteFamilyMemberTagMappingPort deleteFamilyMemberTagMappingPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MemberTagsInfo modify(final ModifyMemberTagsCommand command, final Long currentUserId) {
        Objects.requireNonNull(command, "command는 null일 수 없습니다");
        Objects.requireNonNull(currentUserId, "currentUserId는 null일 수 없습니다");

        Long familyId = command.familyId();
        Long memberId = command.memberId();
        List<Long> tagIds = command.tagIds();

        // 1. Family 존재 여부 확인
        validateFamilyExists(familyId);

        // 2. 현재 사용자가 Family 구성원이면서 OWNER인지 확인
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(familyId, currentUserId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        validateOwnerRole(currentMember);

        // 3. 대상 멤버 조회 및 검증
        FamilyMember targetMember = findFamilyMemberPort.findById(memberId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));

        // 4. 대상 멤버가 해당 Family 소속인지 확인
        if (!targetMember.getFamilyId().equals(familyId)) {
            throw new FTException(FamilyExceptionCode.MEMBER_NOT_IN_FAMILY);
        }

        // 5. 태그 검증 및 매핑 생성
        List<FamilyMemberTag> tags = validateAndGetTags(tagIds, familyId);

        // 6. 기존 매핑 삭제 후 새 매핑 저장 (전체 교체 방식)
        deleteFamilyMemberTagMappingPort.deleteAllByMemberId(memberId);

        if (!tags.isEmpty()) {
            List<FamilyMemberTagMapping> mappings = tags.stream()
                .map(tag -> FamilyMemberTagMapping.newMapping(tag.getId(), memberId))
                .toList();
            saveFamilyMemberTagMappingPort.saveAll(mappings);
        }

        // 7. 결과 반환
        List<TagSimpleInfo> tagInfos = tags.stream()
            .map(tag -> new TagSimpleInfo(tag.getId(), tag.getName(), tag.getColor()))
            .toList();

        return new MemberTagsInfo(memberId, targetMember.getName(), tagInfos);
    }

    private void validateFamilyExists(final Long familyId) {
        if (!findFamilyPort.existsById(familyId)) {
            throw new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND);
        }
    }

    private void validateOwnerRole(final FamilyMember member) {
        if (!member.hasRoleAtLeast(FamilyMemberRole.OWNER)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
    }

    private List<FamilyMemberTag> validateAndGetTags(final List<Long> tagIds, final Long familyId) {
        if (tagIds.isEmpty()) {
            return List.of();
        }

        List<FamilyMemberTag> tags = findFamilyMemberTagPort.findAllByIds(tagIds);

        // 모든 태그가 존재하는지 확인
        if (tags.size() != tagIds.size()) {
            throw new FTException(FamilyExceptionCode.TAG_NOT_FOUND);
        }

        // 모든 태그가 해당 Family의 것인지 확인
        boolean hasTagFromOtherFamily = tags.stream()
            .anyMatch(tag -> !tag.getFamilyId().equals(familyId));

        if (hasTagFromOtherFamily) {
            throw new FTException(FamilyExceptionCode.TAG_NOT_IN_FAMILY);
        }

        return tags;
    }
}
