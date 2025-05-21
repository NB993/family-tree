package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] UpdateFamilyMemberRoleCommandTest")
class UpdateFamilyMemberRoleCommandTest {

    @Test
    @DisplayName("유효한 입력값으로 커맨드 객체를 생성할 수 있다")
    void when_valid_input_then_create_command() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when
        UpdateFamilyMemberRoleCommand command = new UpdateFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        );
        
        // then
        assertThat(command.getFamilyId()).isEqualTo(familyId);
        assertThat(command.getMemberId()).isEqualTo(memberId);
        assertThat(command.getCurrentUserId()).isEqualTo(currentUserId);
        assertThat(command.getNewRole()).isEqualTo(newRole);
    }
    
    @Test
    @DisplayName("Family ID가 null이면 예외가 발생한다")
    void when_family_id_is_null_then_throw_exception() {
        // given
        Long familyId = null;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Family ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Family ID가 0 이하이면 예외가 발생한다")
    void when_family_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 0L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Family ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Member ID가 null이면 예외가 발생한다")
    void when_member_id_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = null;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Member ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Member ID가 0 이하이면 예외가 발생한다")
    void when_member_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 0L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Member ID가 필요합니다");
    }
    
    @Test
    @DisplayName("현재 사용자 ID가 null이면 예외가 발생한다")
    void when_current_user_id_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = null;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 현재 사용자 ID가 필요합니다");
    }
    
    @Test
    @DisplayName("현재 사용자 ID가 0 이하이면 예외가 발생한다")
    void when_current_user_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 0L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 현재 사용자 ID가 필요합니다");
    }
    
    @Test
    @DisplayName("새 역할이 null이면 예외가 발생한다")
    void when_new_role_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = null;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("변경할 역할을 지정해야 합니다");
    }
    
    @Test
    @DisplayName("새 역할이 OWNER이면 예외가 발생한다")
    void when_new_role_is_owner_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.OWNER;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("OWNER 역할로 변경할 수 없습니다");
    }
    
    @Test
    @DisplayName("자신의 역할은 변경할 수 없다")
    void when_changing_self_role_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 2L; // 동일한 ID
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberRoleCommand(familyId, memberId, currentUserId, newRole);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("자신의 역할은 변경할 수 없습니다");
    }
}
