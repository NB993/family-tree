package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import lombok.Getter;

/**
 * 구성원 역할 변경을 위한 커맨드 객체입니다.
 */
@Getter
public class UpdateFamilyMemberRoleCommand {
    
    private final Long familyId;
    private final Long memberId;
    private final Long currentUserId;
    private final FamilyMemberRole newRole;
    
    /**
     * 구성원 역할 변경 커맨드 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param memberId      변경 대상 구성원 ID
     * @param currentUserId 현재 로그인한 사용자 ID
     * @param newRole       변경할 새 역할
     */
    public UpdateFamilyMemberRoleCommand(
        Long familyId,
        Long memberId,
        Long currentUserId,
        FamilyMemberRole newRole
    ) {
        validateFamilyId(familyId);
        validateMemberId(memberId);
        validateCurrentUserId(currentUserId);
        validateNewRole(newRole);
        validateNotSelf(memberId, currentUserId);
        
        this.familyId = familyId;
        this.memberId = memberId;
        this.currentUserId = currentUserId;
        this.newRole = newRole;
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
    
    private void validateNewRole(FamilyMemberRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("변경할 역할을 지정해야 합니다.");
        }
        
        if (newRole == FamilyMemberRole.OWNER) {
            throw new IllegalArgumentException("OWNER 역할로 변경할 수 없습니다.");
        }
    }
    
    private void validateNotSelf(Long memberId, Long currentUserId) {
        if (memberId.equals(currentUserId)) {
            throw new IllegalArgumentException("자신의 역할은 변경할 수 없습니다.");
        }
    }
}
