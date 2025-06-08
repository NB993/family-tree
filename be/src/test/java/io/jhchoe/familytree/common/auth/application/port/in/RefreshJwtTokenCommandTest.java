package io.jhchoe.familytree.common.auth.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JWT 토큰 수정 Command 객체 단위 테스트
 */
@DisplayName("[Unit Test] ModifyJwtTokenCommandTest")
class ModifyJwtTokenCommandTest {

    @Test
    @DisplayName("유효한 Refresh Token으로 Command 생성 시 성공합니다")
    void create_success_when_valid_refresh_token() {
        // given
        String validRefreshToken = "valid.refresh.token";

        // when
        ModifyJwtTokenCommand command = new ModifyJwtTokenCommand(validRefreshToken);

        // then
        assertThat(command.refreshToken()).isEqualTo(validRefreshToken);
    }

    @Test
    @DisplayName("null Refresh Token으로 Command 생성 시 NullPointerException이 발생합니다")
    void create_throws_exception_when_refresh_token_is_null() {
        // when & then
        assertThatThrownBy(() -> new ModifyJwtTokenCommand(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("refreshToken must not be null");
    }

    @Test
    @DisplayName("빈 Refresh Token으로 Command 생성 시 IllegalArgumentException이 발생합니다")
    void create_throws_exception_when_refresh_token_is_empty() {
        // when & then
        assertThatThrownBy(() -> new ModifyJwtTokenCommand(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("refreshToken must not be blank");

        assertThatThrownBy(() -> new ModifyJwtTokenCommand("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("refreshToken must not be blank");
    }
}
