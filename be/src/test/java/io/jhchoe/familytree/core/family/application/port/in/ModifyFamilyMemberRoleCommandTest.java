package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.*;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ModifyFamilyMemberRoleCommand 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberRoleCommandTest")
class ModifyFamilyMemberRoleCommandTest {

    @Test
    @DisplayName("올바른 매개변수로 명령 객체를 생성할 수 있습니다")
    void create_command_with_valid_parameters() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        );

        // then
        assertThat(command.getFamilyId()).isEqualTo(familyId);
        assertThat(command.getMemberId()).isEqualTo(memberId);
        assertThat(command.getCurrentUserId()).isEqualTo(currentUserId);
        assertThat(command.getNewRole()).isEqualTo(newRole);
    }

    @Test
    @DisplayName("familyId가 null인 경우 예외가 발생합니다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("memberId가 null인 경우 예외가 발생합니다")
    void throw_exception_when_member_id_is_null() {
        // given
        Long familyId = 1L;
        Long memberId = null;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("memberId must not be null");
    }

    @Test
    @DisplayName("currentUserId가 null인 경우 예외가 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = null;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("currentUserId must not be null");
    }

    @Test
    @DisplayName("newRole이 null인 경우 예외가 발생합니다")
    void throw_exception_when_new_role_is_null() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = null;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("newRole must not be null");
    }

    @Test
    @DisplayName("familyId가 0 이하인 경우 예외가 발생합니다")
    void throw_exception_when_family_id_is_not_positive() {
        // given
        Long familyId = 0L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("familyId must be positive");
    }

    @Test
    @DisplayName("memberId가 0 이하인 경우 예외가 발생합니다")
    void throw_exception_when_member_id_is_not_positive() {
        // given
        Long familyId = 1L;
        Long memberId = -1L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("memberId must be positive");
    }

    @Test
    @DisplayName("currentUserId가 0 이하인 경우 예외가 발생합니다")
    void throw_exception_when_current_user_id_is_not_positive() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 0L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> new ModifyFamilyMemberRoleCommand(
            familyId, memberId, currentUserId, newRole
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("currentUserId must be positive");
    }
}
