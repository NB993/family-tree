package io.jhchoe.familytree.core.invite.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] SaveFamilyInviteCommandTest")
class SaveFamilyInviteCommandTest {

    @Test
    @DisplayName("유효한 requesterId로 커맨드를 생성할 수 있습니다")
    void create_command_with_valid_requesterId() {
        // when
        SaveFamilyInviteCommand command = new SaveFamilyInviteCommand(1L);

        // then
        assertThat(command.requesterId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("requesterId가 null이면 예외가 발생합니다")
    void throw_exception_when_requesterId_is_null() {
        // when & then
        assertThatThrownBy(() -> new SaveFamilyInviteCommand(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("requesterId must not be null");
    }

    @Test
    @DisplayName("requesterId가 0 이하이면 예외가 발생합니다")
    void throw_exception_when_requesterId_is_zero_or_negative() {
        // when & then
        assertThatThrownBy(() -> new SaveFamilyInviteCommand(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("requesterId must be greater than 0");

        assertThatThrownBy(() -> new SaveFamilyInviteCommand(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("requesterId must be greater than 0");
    }
}