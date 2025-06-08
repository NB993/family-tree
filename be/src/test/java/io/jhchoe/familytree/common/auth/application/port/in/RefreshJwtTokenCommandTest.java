package io.jhchoe.familytree.common.auth.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] RefreshJwtTokenCommandTest")
class RefreshJwtTokenCommandTest {

    @Test
    @DisplayName("유효한 refreshToken으로 Command 객체 생성에 성공합니다")
    void create_success_when_valid_refresh_token() {
        // given
        String validRefreshToken = "valid.refresh.token";

        // when
        RefreshJwtTokenCommand command = new RefreshJwtTokenCommand(validRefreshToken);

        // then
        assertThat(command.refreshToken()).isEqualTo(validRefreshToken);
    }

    @Test
    @DisplayName("refreshToken이 null인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_refresh_token_is_null() {
        assertThatThrownBy(() -> new RefreshJwtTokenCommand(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("refreshToken이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_refresh_token_is_blank() {
        assertThatThrownBy(() -> new RefreshJwtTokenCommand(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("refreshToken must not be blank");

        assertThatThrownBy(() -> new RefreshJwtTokenCommand("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("refreshToken must not be blank");
    }
}
