package io.jhchoe.familytree.common.auth.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("[Unit Test] SaveRefreshTokenCommandTest")
class SaveRefreshTokenCommandTest {

    @Test
    @DisplayName("유효한 파라미터로 커맨드 객체를 생성합니다")
    void newRefreshToken_success_when_parameters_are_valid() {
        // given
        Long userId = 1L;
        String tokenHash = "hashed-token-value";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        
        // when
        SaveRefreshTokenCommand command = new SaveRefreshTokenCommand(userId, tokenHash, expiresAt);
        
        // then
        assertThat(command.getUserId()).isEqualTo(userId);
        assertThat(command.getTokenHash()).isEqualTo(tokenHash);
        assertThat(command.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("사용자 ID가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_user_id_is_null() {
        // given
        String tokenHash = "hashed-token-value";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // when & then
        assertThatThrownBy(() -> new SaveRefreshTokenCommand(null, tokenHash, expiresAt))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }

    @Test
    @DisplayName("토큰 해시가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_token_hash_is_null() {
        // given
        Long userId = 1L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // when & then
        assertThatThrownBy(() -> new SaveRefreshTokenCommand(userId, null, expiresAt))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tokenHash must not be null");
    }

    @Test
    @DisplayName("토큰 해시가 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_token_hash_is_blank() {
        // given
        Long userId = 1L;
        String tokenHash = "";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // when & then
        assertThatThrownBy(() -> new SaveRefreshTokenCommand(userId, tokenHash, expiresAt))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tokenHash must not be blank");
    }

    @Test
    @DisplayName("만료 시간이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_expires_at_is_null() {
        // given
        Long userId = 1L;
        String tokenHash = "hashed-token-value";

        // when & then
        assertThatThrownBy(() -> new SaveRefreshTokenCommand(userId, tokenHash, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("expiresAt must not be null");
    }
}
