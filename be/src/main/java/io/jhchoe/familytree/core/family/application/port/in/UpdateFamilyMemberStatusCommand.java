package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import lombok.Getter;

/**
 * 구성원 상태 변경을 위한 커맨드 객체입니다.
 */
@Getter
public class UpdateFamilyMemberStatusCommand {
    
    private final Long familyId;
    private final Long memberId;
    private final Long currentUserId;
    private final FamilyMemberStatus newStatus;
    private final String reason;
    
    /**
     * 구성원 상태 변경 커맨드 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param memberId      변경 대상 구성원 ID
     * @param currentUserId 현재 로그인한 사용자 ID
     * @param newStatus     변경할 새 상태
     * @param reason        상태 변경 사유
     */
    public UpdateFamilyMemberStatusCommand(
        Long familyId,
        Long memberId,
        Long currentUserId,
        FamilyMemberStatus newStatus,
        String reason
    ) {
        validateFamilyId(familyId);
        validateMemberId(memberId);
        validateCurrentUserId(currentUserId);
        validateNewStatus(newStatus);
        validateNotSelf(memberId, currentUserId);
        
        this.familyId = familyId;
        this.memberId = memberId;
        this.currentUserId = currentUserId;
        this.newStatus = newStatus;
        this.reason = reason;
    }
    
    private void validateFamilyId(Long familyId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 Family ID가 필요합니다.");
        }
    }
    
    private void validateMemberId(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("유효한 Member ID가 필요합니다.");
        }
    }
    
    private void validateCurrentUserId(Long currentUserId) {
        if (currentUserId == null || currentUserId <= 0) {
            throw new IllegalArgumentException("유효한 현재 사용자 ID가 필요합니다.");
        }
    }
    
    private void validateNewStatus(FamilyMemberStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("변경할 상태를 지정해야 합니다.");
        }
    }
    
    private void validateNotSelf(Long memberId, Long currentUserId) {
        if (memberId.equals(currentUserId)) {
            throw new IllegalArgumentException("자신의 상태는 변경할 수 없습니다.");
        }
    }
}
