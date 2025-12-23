package io.jhchoe.familytree.core.invite.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] SaveFamilyInviteCommandTest")
class SaveFamilyInviteCommandTest {

    @Test
    @DisplayName("유효한 familyId와 requesterId로 커맨드를 생성할 수 있습니다")
    void create_command_with_valid_familyId_and_requesterId() {
        // when
        SaveFamilyInviteCommand command = new SaveFamilyInviteCommand(10L, 1L);

        // then
        assertThat(command.familyId()).isEqualTo(10L);
        assertThat(command.requesterId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("familyId가 null이면 예외가 발생합니다")
    void throw_exception_when_familyId_is_null() {
        // when & then
        assertThatThrownBy(() -> new SaveFamilyInviteCommand(null, 1L))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("requesterId가 null이면 예외가 발생합니다")
    void throw_exception_when_requesterId_is_null() {
        // when & then
        assertThatThrownBy(() -> new SaveFamilyInviteCommand(10L, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("requesterId must not be null");
    }

    @Test
    @DisplayName("familyId가 0 이하이면 예외가 발생합니다")
    void throw_exception_when_familyId_is_zero_or_negative() {
        // when & then
        assertThatThrownBy(() -> new SaveFamilyInviteCommand(0L, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("familyId must be greater than 0");

        assertThatThrownBy(() -> new SaveFamilyInviteCommand(-1L, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("familyId must be greater than 0");
    }

    @Test
    @DisplayName("requesterId가 0 이하이면 예외가 발생합니다")
    void throw_exception_when_requesterId_is_zero_or_negative() {
        // when & then
        assertThatThrownBy(() -> new SaveFamilyInviteCommand(10L, 0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("requesterId must be greater than 0");

        assertThatThrownBy(() -> new SaveFamilyInviteCommand(10L, -1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("requesterId must be greater than 0");
    }
}