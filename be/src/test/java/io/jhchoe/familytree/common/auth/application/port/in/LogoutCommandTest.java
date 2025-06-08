package io.jhchoe.familytree.common.auth.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] LogoutCommandTest")
class LogoutCommandTest {

    @Test
    @DisplayName("유효한 userId로 Command 객체 생성에 성공합니다")
    void create_success_when_valid_user_id() {
        // given
        Long validUserId = 1L;

        // when
        LogoutCommand command = new LogoutCommand(validUserId);

        // then
        assertThat(command.userId()).isEqualTo(validUserId);
    }

    @Test
    @DisplayName("userId가 null인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_user_id_is_null() {
        assertThatThrownBy(() -> new LogoutCommand(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("userId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_user_id_is_not_positive() {
        assertThatThrownBy(() -> new LogoutCommand(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("userId must be positive");

        assertThatThrownBy(() -> new LogoutCommand(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("userId must be positive");
    }
}
