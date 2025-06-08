package io.jhchoe.familytree.common.auth.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JWT 토큰 삭제 Command 객체 단위 테스트
 */
@DisplayName("[Unit Test] DeleteJwtTokenCommandTest")
class DeleteJwtTokenCommandTest {

    @Test
    @DisplayName("유효한 사용자 ID로 Command 생성 시 성공합니다")
    void create_success_when_valid_user_id() {
        // given
        Long validUserId = 1L;

        // when
        DeleteJwtTokenCommand command = new DeleteJwtTokenCommand(validUserId);

        // then
        assertThat(command.userId()).isEqualTo(validUserId);
    }

    @Test
    @DisplayName("null 사용자 ID로 Command 생성 시 NullPointerException이 발생합니다")
    void create_throws_exception_when_user_id_is_null() {
        // when & then
        assertThatThrownBy(() -> new DeleteJwtTokenCommand(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }

    @Test
    @DisplayName("0 이하의 사용자 ID로 Command 생성 시 IllegalArgumentException이 발생합니다")
    void create_throws_exception_when_user_id_is_zero_or_negative() {
        // when & then
        assertThatThrownBy(() -> new DeleteJwtTokenCommand(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("userId must be positive");

        assertThatThrownBy(() -> new DeleteJwtTokenCommand(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("userId must be positive");
    }
}
