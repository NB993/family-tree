package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagUseCase;
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
 * 태그 수정 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyFamilyMemberTagService implements ModifyFamilyMemberTagUseCase {

    private final SaveFamilyMemberTagPort saveFamilyMemberTagPort;
    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void modify(ModifyFamilyMemberTagCommand command, Long currentUserId) {
        Objects.requireNonNull(command, "command는 null일 수 없습니다");
        Objects.requireNonNull(currentUserId, "currentUserId는 null일 수 없습니다");

        Long familyId = command.familyId();
        Long tagId = command.tagId();

        // 1. Family 존재 여부 확인
        validateFamilyExists(familyId);

        // 2. OWNER 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(familyId, currentUserId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        validateOwnerRole(currentMember);

        // 3. 태그 존재 여부 확인
        FamilyMemberTag tag = findFamilyMemberTagPort.findById(tagId)
            .orElseThrow(() -> new FTException(FamilyExceptionCode.TAG_NOT_FOUND));

        // 4. 태그가 해당 Family의 것인지 확인
        if (!tag.getFamilyId().equals(familyId)) {
            throw new FTException(FamilyExceptionCode.TAG_NOT_IN_FAMILY);
        }

        // 5. 이름 변경 시 중복 검증 (본인 제외)
        String newName = command.name();
        findFamilyMemberTagPort.findByFamilyIdAndName(familyId, newName)
            .filter(existing -> !existing.getId().equals(tagId))
            .ifPresent(existing -> {
                throw new FTException(FamilyExceptionCode.TAG_NAME_DUPLICATED);
            });

        // 6. 태그 수정
        FamilyMemberTag modifiedTag = tag.rename(newName, currentUserId);

        // 색상도 변경하는 경우
        if (command.color() != null) {
            modifiedTag = modifiedTag.changeColor(command.color(), currentUserId);
        }

        saveFamilyMemberTagPort.save(modifiedTag);
    }

    private void validateFamilyExists(Long familyId) {
        if (!findFamilyPort.existsById(familyId)) {
            throw new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND);
        }
    }

    private void validateOwnerRole(FamilyMember member) {
        if (!member.hasRoleAtLeast(FamilyMemberRole.OWNER)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
    }
}
