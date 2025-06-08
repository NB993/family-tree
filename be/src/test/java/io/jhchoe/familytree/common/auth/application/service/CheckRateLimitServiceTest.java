package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.CheckRateLimitCommand;
import io.jhchoe.familytree.common.auth.application.port.out.RateLimitPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] CheckRateLimitServiceTest")
class CheckRateLimitServiceTest {

    @InjectMocks
    private CheckRateLimitService checkRateLimitService;

    @Mock
    private RateLimitPort rateLimitPort;

    @Test
    @DisplayName("Rate Limit 이내 요청일 때 true를 반환합니다")
    void return_true_when_within_rate_limit() {
        // given
        CheckRateLimitCommand command = new CheckRateLimitCommand(
            "token_refresh", "192.168.1.1", 5, 300L
        );
        String expectedKey = "token_refresh:192.168.1.1";

        // Mocking: Rate Limit 체크 성공 (허용)
        when(rateLimitPort.checkAndIncrement(expectedKey, 5, 300L)).thenReturn(true);

        // when
        boolean result = checkRateLimitService.checkRateLimit(command);

        // then
        assertThat(result).isTrue();
        verify(rateLimitPort).checkAndIncrement(expectedKey, 5, 300L);
    }

    @Test
    @DisplayName("Rate Limit 초과 요청일 때 false를 반환합니다")
    void return_false_when_rate_limit_exceeded() {
        // given
        CheckRateLimitCommand command = new CheckRateLimitCommand(
            "login_attempt", "10.0.0.1", 3, 600L
        );
        String expectedKey = "login_attempt:10.0.0.1";

        // Mocking: Rate Limit 체크 실패 (제한 초과)
        when(rateLimitPort.checkAndIncrement(expectedKey, 3, 600L)).thenReturn(false);
        // Mocking: 현재 요청 횟수 조회 (로깅용)
        when(rateLimitPort.getCurrentCount(expectedKey)).thenReturn(4);

        // when
        boolean result = checkRateLimitService.checkRateLimit(command);

        // then
        assertThat(result).isFalse();
        verify(rateLimitPort).checkAndIncrement(expectedKey, 3, 600L);
        verify(rateLimitPort).getCurrentCount(expectedKey);
    }

    @Test
    @DisplayName("Command가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        assertThatThrownBy(() -> checkRateLimitService.checkRateLimit(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("Rate Limit 키가 올바르게 생성됩니다")
    void generate_rate_limit_key_correctly() {
        // given
        CheckRateLimitCommand command = new CheckRateLimitCommand(
            "api_call", "203.0.113.1", 10, 60L
        );
        String expectedKey = "api_call:203.0.113.1";

        // Mocking: Rate Limit 체크 성공
        when(rateLimitPort.checkAndIncrement(expectedKey, 10, 60L)).thenReturn(true);

        // when
        boolean result = checkRateLimitService.checkRateLimit(command);

        // then
        assertThat(result).isTrue();
        verify(rateLimitPort).checkAndIncrement(expectedKey, 10, 60L);
    }

    @Test
    @DisplayName("특수문자가 포함된 IP 주소도 올바르게 키를 생성합니다")
    void generate_key_correctly_with_special_characters_in_ip() {
        // given
        CheckRateLimitCommand command = new CheckRateLimitCommand(
            "password_reset", "::1", 2, 3600L
        );
        String expectedKey = "password_reset:::1";

        // Mocking: Rate Limit 체크 성공
        when(rateLimitPort.checkAndIncrement(expectedKey, 2, 3600L)).thenReturn(true);

        // when
        boolean result = checkRateLimitService.checkRateLimit(command);

        // then
        assertThat(result).isTrue();
        verify(rateLimitPort).checkAndIncrement(expectedKey, 2, 3600L);
    }
}
