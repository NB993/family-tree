package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.out.DeleteFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 태그 삭제 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class DeleteFamilyMemberTagService implements DeleteFamilyMemberTagUseCase {

    private final DeleteFamilyMemberTagPort deleteFamilyMemberTagPort;
    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(final DeleteFamilyMemberTagCommand command, final Long currentUserId) {
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

        // 5. 태그 삭제 (CASCADE로 매핑도 함께 삭제됨)
        deleteFamilyMemberTagPort.deleteById(tagId);
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
