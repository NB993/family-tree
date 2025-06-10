package io.jhchoe.familytree.core.family.application.validation;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;

/**
 * 구성원의 권한을 검증하는 유틸리티 클래스입니다.
 */
public final class FamilyMemberAuthorizationValidator {

    private FamilyMemberAuthorizationValidator() {
        // 인스턴스화 방지
    }
    
    /**
     * 구성원이 필요한 최소 역할을 가지고 있는지 검증합니다.
     *
     * @param member       검증할 구성원
     * @param requiredRole 필요한 최소 역할
     * @throws FTException 권한이 충분하지 않은 경우
     */
    public static void validateRole(FamilyMember member, FamilyMemberRole requiredRole) {
        Objects.requireNonNull(member, "member must not be null");
        Objects.requireNonNull(requiredRole, "requiredRole must not be null");
        
        if (!member.hasRoleAtLeast(requiredRole)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
    }
    
    /**
     * 구성원이 활성 상태인지 검증합니다.
     *
     * @param member 검증할 구성원
     * @throws FTException 활성 상태가 아닌 경우
     */
    public static void validateActiveStatus(FamilyMember member) {
        Objects.requireNonNull(member, "member must not be null");
        
        if (!member.isActive()) {
            throw new FTException(FamilyExceptionCode.MEMBER_NOT_ACTIVE);
        }
    }
    
    /**
     * 구성원 권한 및 활성 상태를 모두 검증합니다.
     *
     * @param member       검증할 구성원
     * @param requiredRole 필요한 최소 역할
     * @throws FTException 권한이 충분하지 않거나 활성 상태가 아닌 경우
     */
    public static void validateRoleAndStatus(FamilyMember member, FamilyMemberRole requiredRole) {
        validateActiveStatus(member);
        validateRole(member, requiredRole);
    }
    
    /**
     * 관리자가 다른 관리자를 수정할 수 있는지 검증합니다.
     * ADMIN은 다른 ADMIN을 수정할 수 없고, OWNER는 모든 구성원을 수정할 수 있습니다.
     *
     * @param currentMember 현재 작업을 수행하는 구성원
     * @param targetMember  수정 대상 구성원
     * @throws FTException  권한이 충분하지 않은 경우
     */
    public static void validateAdminModification(FamilyMember currentMember, FamilyMember targetMember) {
        Objects.requireNonNull(currentMember, "currentMember must not be null");
        Objects.requireNonNull(targetMember, "targetMember must not be null");
        
        // OWNER는 모든 구성원 수정 가능
        if (currentMember.getRole() == FamilyMemberRole.OWNER) {
            return;
        }
        
        // ADMIN은 자신의 정보 수정 불가
        if (Objects.equals(currentMember.getId(), targetMember.getId())) {
            throw new FTException(FamilyExceptionCode.SELF_MODIFICATION_NOT_ALLOWED);
        }
        
        // ADMIN은 다른 ADMIN 또는 OWNER 수정 불가
        if (currentMember.getRole() == FamilyMemberRole.ADMIN 
                && targetMember.getRole().isAtLeast(FamilyMemberRole.ADMIN)) {
            throw new FTException(FamilyExceptionCode.ADMIN_MODIFICATION_NOT_ALLOWED);
        }
    }
    
    /**
     * 구성원이 OWNER 권한을 가지고 있는지 검증합니다.
     *
     * @param member 검증할 구성원
     * @throws FTException OWNER 권한이 없는 경우
     */
    public static void validateOwnerRole(FamilyMember member) {
        validateRole(member, FamilyMemberRole.OWNER);
    }
    
    /**
     * Family 수정/삭제 권한을 검증합니다.
     * OWNER 권한과 활성 상태를 모두 확인합니다.
     *
     * @param member 검증할 구성원
     * @throws FTException OWNER 권한이 없거나 활성 상태가 아닌 경우
     */
    public static void validateFamilyModificationPermission(FamilyMember member) {
        validateRoleAndStatus(member, FamilyMemberRole.OWNER);
    }
    
    /**
     * 비공개 Family 접근 권한을 검증합니다.
     * 공개 Family는 누구나 조회 가능하고, 비공개 Family는 구성원만 조회 가능합니다.
     *
     * @param family Family 정보
     * @param member 현재 사용자의 구성원 정보 (비구성원인 경우 null)
     * @throws FTException 비공개 Family에 접근할 권한이 없는 경우
     */
    public static void validateFamilyAccessPermission(Family family, FamilyMember member) {
        Objects.requireNonNull(family, "family must not be null");
        
        // 공개 Family는 누구나 접근 가능
        if (family.getIsPublic()) {
            return;
        }
        
        // 비공개 Family는 구성원만 접근 가능
        if (member == null) {
            throw new FTException(FamilyExceptionCode.ACCESS_DENIED);
        }
    }
}
