package io.jhchoe.familytree.common.auth.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] CheckRateLimitCommandTest")
class CheckRateLimitCommandTest {

    @Test
    @DisplayName("유효한 파라미터로 Command 생성 시 성공합니다")
    void create_command_success_when_valid_parameters() {
        // given
        String key = "token_refresh";
        String ipAddress = "192.168.1.1";
        int limitCount = 5;
        long windowSizeInSeconds = 300L;

        // when
        CheckRateLimitCommand command = new CheckRateLimitCommand(
            key, ipAddress, limitCount, windowSizeInSeconds
        );

        // then
        assertThat(command.key()).isEqualTo(key);
        assertThat(command.ipAddress()).isEqualTo(ipAddress);
        assertThat(command.limitCount()).isEqualTo(limitCount);
        assertThat(command.windowSizeInSeconds()).isEqualTo(windowSizeInSeconds);
    }

    @Test
    @DisplayName("키가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_key_is_null() {
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            null, "192.168.1.1", 5, 300L
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Rate Limit 키가 필요합니다.");
    }

    @Test
    @DisplayName("키가 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_key_is_blank() {
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "", "192.168.1.1", 5, 300L
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rate Limit 키는 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("IP 주소가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_ip_address_is_null() {
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "token_refresh", null, 5, 300L
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("IP 주소가 필요합니다.");
    }

    @Test
    @DisplayName("IP 주소가 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_ip_address_is_blank() {
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "token_refresh", "", 5, 300L
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("IP 주소는 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("제한 횟수가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_limit_count_is_zero_or_negative() {
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "token_refresh", "192.168.1.1", 0, 300L
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("제한 횟수는 0보다 커야 합니다.");
        
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "token_refresh", "192.168.1.1", -1, 300L
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("제한 횟수는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("시간 윈도우 크기가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_window_size_is_zero_or_negative() {
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "token_refresh", "192.168.1.1", 5, 0L
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("시간 윈도우 크기는 0보다 커야 합니다.");
        
        assertThatThrownBy(() -> new CheckRateLimitCommand(
            "token_refresh", "192.168.1.1", 5, -1L
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("시간 윈도우 크기는 0보다 커야 합니다.");
    }
}
