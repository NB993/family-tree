package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.domain.SecurityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] LogSecurityEventCommandTest")
class LogSecurityEventCommandTest {

    @Test
    @DisplayName("유효한 파라미터로 Command 생성 시 성공합니다")
    void create_command_success_when_valid_parameters() {
        // given
        SecurityEventType eventType = SecurityEventType.AUTHENTICATION_FAILURE;
        String userId = "user123";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        String description = "로그인 실패";

        // when
        LogSecurityEventCommand command = new LogSecurityEventCommand(
            eventType, userId, ipAddress, userAgent, description
        );

        // then
        assertThat(command.eventType()).isEqualTo(eventType);
        assertThat(command.userId()).isEqualTo(userId);
        assertThat(command.ipAddress()).isEqualTo(ipAddress);
        assertThat(command.userAgent()).isEqualTo(userAgent);
        assertThat(command.description()).isEqualTo(description);
    }

    @Test
    @DisplayName("이벤트 타입이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_event_type_is_null() {
        assertThatThrownBy(() -> new LogSecurityEventCommand(
            null, "user123", "192.168.1.1", "Mozilla/5.0", "로그인 실패"
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("이벤트 타입이 필요합니다.");
    }

    @Test
    @DisplayName("IP 주소가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_ip_address_is_null() {
        assertThatThrownBy(() -> new LogSecurityEventCommand(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", null, "Mozilla/5.0", "로그인 실패"
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("IP 주소가 필요합니다.");
    }

    @Test
    @DisplayName("IP 주소가 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_ip_address_is_blank() {
        assertThatThrownBy(() -> new LogSecurityEventCommand(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", "", "Mozilla/5.0", "로그인 실패"
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("IP 주소는 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("이벤트 설명이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_description_is_null() {
        assertThatThrownBy(() -> new LogSecurityEventCommand(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", "192.168.1.1", "Mozilla/5.0", null
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("이벤트 설명이 필요합니다.");
    }

    @Test
    @DisplayName("이벤트 설명이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_description_is_blank() {
        assertThatThrownBy(() -> new LogSecurityEventCommand(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", "192.168.1.1", "Mozilla/5.0", ""
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이벤트 설명은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("userId와 userAgent는 null이어도 Command 생성에 성공합니다")
    void create_command_success_when_optional_parameters_are_null() {
        // given
        SecurityEventType eventType = SecurityEventType.TOKEN_EXPIRED;
        String ipAddress = "192.168.1.1";
        String description = "토큰 만료";

        // when
        LogSecurityEventCommand command = new LogSecurityEventCommand(
            eventType, null, ipAddress, null, description
        );

        // then
        assertThat(command.eventType()).isEqualTo(eventType);
        assertThat(command.userId()).isNull();
        assertThat(command.ipAddress()).isEqualTo(ipAddress);
        assertThat(command.userAgent()).isNull();
        assertThat(command.description()).isEqualTo(description);
    }
}
