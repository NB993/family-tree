package io.jhchoe.familytree.core.family.application.validation;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberAuthorizationValidatorTest")
class FamilyMemberAuthorizationValidatorTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 6, 10, 12, 0);

    @Test
    @DisplayName("OWNER 권한 검증 - 성공")
    void validate_owner_role_success() {
        // given
        FamilyMember ownerMember = FamilyMember.withId(
            1L, 1L, 1L, null, "소유자", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatNoException()
            .isThrownBy(() -> FamilyMemberAuthorizationValidator.validateOwnerRole(ownerMember));
    }
    
    @Test
    @DisplayName("OWNER 권한 검증 - 실패 (ADMIN 권한)")
    void validate_owner_role_fail_admin_role() {
        // given
        FamilyMember adminMember = FamilyMember.withId(
            1L, 1L, 1L, null, "관리자", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateOwnerRole(adminMember))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("해당 작업을 수행할 권한이 없습니다");
    }
    
    @Test
    @DisplayName("OWNER 권한 검증 - 실패 (MEMBER 권한)")
    void validate_owner_role_fail_member_role() {
        // given
        FamilyMember member = FamilyMember.withId(
            1L, 1L, 1L, null, "일반구성원", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateOwnerRole(member))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("해당 작업을 수행할 권한이 없습니다");
    }
    
    @Test
    @DisplayName("Family 수정 권한 검증 - 성공")
    void validate_family_modification_permission_success() {
        // given
        FamilyMember ownerMember = FamilyMember.withId(
            1L, 1L, 1L, null, "소유자", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatNoException()
            .isThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyModificationPermission(ownerMember));
    }
    
    @Test
    @DisplayName("Family 수정 권한 검증 - 실패 (ADMIN 권한)")
    void validate_family_modification_permission_fail_admin_role() {
        // given
        FamilyMember adminMember = FamilyMember.withId(
            1L, 1L, 1L, null, "관리자", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyModificationPermission(adminMember))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("해당 작업을 수행할 권한이 없습니다");
    }
    
    @Test
    @DisplayName("Family 수정 권한 검증 - 실패 (비활성 OWNER)")
    void validate_family_modification_permission_fail_inactive_status() {
        // given
        FamilyMember suspendedOwner = FamilyMember.withId(
            1L, 1L, 1L, null, "소유자", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.SUSPENDED, FamilyMemberRole.OWNER,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyModificationPermission(suspendedOwner))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("활성 상태가 아닌 구성원입니다");
    }
    
    @Test
    @DisplayName("비공개 Family 접근 제어 - 성공 (공개 Family)")
    void validate_family_access_permission_success_public_family() {
        // given
        Family publicFamily = Family.withId(
            1L, "공개가족", "공개된 가족입니다", "profile.jpg", true,
            1L, NOW, 1L, NOW
        );
        
        // when & then (비구성원도 접근 가능)
        assertThatNoException()
            .isThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyAccessPermission(publicFamily, null));
    }
    
    @Test
    @DisplayName("비공개 Family 접근 제어 - 성공 (비공개 Family + 구성원)")
    void validate_family_access_permission_success_private_family_member() {
        // given
        Family privateFamily = Family.withId(
            1L, "비공개가족", "비공개된 가족입니다", "profile.jpg", false,
            1L, NOW, 1L, NOW
        );
        FamilyMember member = FamilyMember.withId(
            1L, 1L, 1L, null, "구성원", null, "profile.jpg", NOW, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatNoException()
            .isThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyAccessPermission(privateFamily, member));
    }
    
    @Test
    @DisplayName("비공개 Family 접근 제어 - 실패 (비공개 Family + 비구성원)")
    void validate_family_access_permission_fail_private_family_non_member() {
        // given
        Family privateFamily = Family.withId(
            1L, "비공개가족", "비공개된 가족입니다", "profile.jpg", false,
            1L, NOW, 1L, NOW
        );
        
        // when & then
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyAccessPermission(privateFamily, null))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("접근이 거부되었습니다");
    }
    
    @Test
    @DisplayName("null 파라미터 검증 - validateOwnerRole")
    void validate_owner_role_fail_null_parameter() {
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateOwnerRole(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("member must not be null");
    }
    
    @Test
    @DisplayName("null 파라미터 검증 - validateFamilyModificationPermission")
    void validate_family_modification_permission_fail_null_parameter() {
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyModificationPermission(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("member must not be null");
    }
    
    @Test
    @DisplayName("null 파라미터 검증 - validateFamilyAccessPermission family null")
    void validate_family_access_permission_fail_family_null() {
        assertThatThrownBy(() -> FamilyMemberAuthorizationValidator.validateFamilyAccessPermission(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("family must not be null");
    }
}
