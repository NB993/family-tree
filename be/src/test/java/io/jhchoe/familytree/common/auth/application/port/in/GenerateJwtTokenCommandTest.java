package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] GenerateJwtTokenCommand")
class GenerateJwtTokenCommandTest {

    @Test
    @DisplayName("유효한 사용자 정보로 Command 객체를 생성할 수 있다")
    void create_command_with_valid_user() {
        // given
        final FTUser user = FTUser.ofOAuth2User(
                1L,
                "테스트 사용자",
                "test@example.com",
                OAuth2Provider.GOOGLE,
                Map.of(
                    "sub", "google_123",
                    "name", "테스트 사용자",
                    "email", "test@example.com",
                    "picture", "https://example.com/profile.jpg"
                )
        );

        // when
        final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(user);

        // then
        assertThat(command.user()).isEqualTo(user);
        assertThat(command.getUserId()).isEqualTo(1L);
        assertThat(command.getEmail()).isEqualTo("test@example.com");
        assertThat(command.getName()).isEqualTo("테스트 사용자");
        assertThat(command.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("사용자 정보가 null이면 예외가 발생한다")
    void create_command_with_null_user_throws_exception() {
        // when & then
        assertThatThrownBy(() -> new GenerateJwtTokenCommand(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("user must not be null");
    }

    @Test
    @DisplayName("JWT용 사용자로 Command 객체를 생성할 수 있다")
    void create_command_with_jwt_user() {
        // given
        final FTUser user = FTUser.ofJwtUser(
                2L,
                "JWT 사용자",
                "jwt@example.com",
                "ADMIN"
        );

        // when
        final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(user);

        // then
        assertThat(command.getUserId()).isEqualTo(2L);
        assertThat(command.getEmail()).isEqualTo("jwt@example.com");
        assertThat(command.getName()).isEqualTo("JWT 사용자");
        assertThat(command.getRole()).isEqualTo("ADMIN");
    }
}
