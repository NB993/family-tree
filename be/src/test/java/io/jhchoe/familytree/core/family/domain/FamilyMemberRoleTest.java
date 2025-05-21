package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberRoleTest")
class FamilyMemberRoleTest {

    @Test
    @DisplayName("OWNER는 OWNER, ADMIN, MEMBER 이상의 권한을 가진다")
    void owner_has_at_least_all_roles() {
        // given
        FamilyMemberRole role = FamilyMemberRole.OWNER;
        
        // when & then
        assertThat(role.isAtLeast(FamilyMemberRole.OWNER)).isTrue();
        assertThat(role.isAtLeast(FamilyMemberRole.ADMIN)).isTrue();
        assertThat(role.isAtLeast(FamilyMemberRole.MEMBER)).isTrue();
    }
    
    @Test
    @DisplayName("ADMIN은 ADMIN, MEMBER 이상의 권한을 가진다")
    void admin_has_at_least_admin_and_member_roles() {
        // given
        FamilyMemberRole role = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThat(role.isAtLeast(FamilyMemberRole.OWNER)).isFalse();
        assertThat(role.isAtLeast(FamilyMemberRole.ADMIN)).isTrue();
        assertThat(role.isAtLeast(FamilyMemberRole.MEMBER)).isTrue();
    }
    
    @Test
    @DisplayName("MEMBER는 MEMBER 이상의 권한을 가진다")
    void member_has_at_least_member_role() {
        // given
        FamilyMemberRole role = FamilyMemberRole.MEMBER;
        
        // when & then
        assertThat(role.isAtLeast(FamilyMemberRole.OWNER)).isFalse();
        assertThat(role.isAtLeast(FamilyMemberRole.ADMIN)).isFalse();
        assertThat(role.isAtLeast(FamilyMemberRole.MEMBER)).isTrue();
    }
}
