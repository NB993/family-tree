package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 태그 생성 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class SaveFamilyMemberTagService implements SaveFamilyMemberTagUseCase {

    private static final int MAX_TAGS_PER_FAMILY = 10;

    private final SaveFamilyMemberTagPort saveFamilyMemberTagPort;
    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(final SaveFamilyMemberTagCommand command, final Long currentUserId) {
        Objects.requireNonNull(command, "command는 null일 수 없습니다");
        Objects.requireNonNull(currentUserId, "currentUserId는 null일 수 없습니다");

        Long familyId = command.familyId();

        // 1. Family 존재 여부 확인
        validateFamilyExists(familyId);

        // 2. 현재 사용자가 Family 구성원인지 확인 및 OWNER 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(familyId, currentUserId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        validateOwnerRole(currentMember);

        // 3. 태그 수 제한 검증 (최대 10개)
        int tagCount = findFamilyMemberTagPort.countByFamilyId(familyId);
        if (tagCount >= MAX_TAGS_PER_FAMILY) {
            throw new FTException(FamilyExceptionCode.TAG_LIMIT_EXCEEDED);
        }

        // 4. 이름 중복 검증
        findFamilyMemberTagPort.findByFamilyIdAndName(familyId, command.name())
            .ifPresent(existing -> {
                throw new FTException(FamilyExceptionCode.TAG_NAME_DUPLICATED);
            });

        // 5. 태그 생성 및 저장
        FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, command.name(), currentUserId);
        return saveFamilyMemberTagPort.save(tag);
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
}
