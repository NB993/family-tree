package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.LogSecurityEventCommand;
import io.jhchoe.familytree.common.auth.application.port.out.SaveSecurityEventPort;
import io.jhchoe.familytree.common.auth.domain.SecurityEvent;
import io.jhchoe.familytree.common.auth.domain.SecurityEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] LogSecurityEventServiceTest")
class LogSecurityEventServiceTest {

    @InjectMocks
    private LogSecurityEventService logSecurityEventService;

    @Mock
    private SaveSecurityEventPort saveSecurityEventPort;

    @Test
    @DisplayName("유효한 Command로 보안 이벤트 로깅 시 성공합니다")
    void log_event_success_when_valid_command() {
        // given
        LogSecurityEventCommand command = new LogSecurityEventCommand(
            SecurityEventType.AUTHENTICATION_FAILURE,
            "user123",
            "192.168.1.1",
            "Mozilla/5.0",
            "로그인 실패 - 잘못된 비밀번호"
        );
        Long expectedEventId = 1L;

        // Mocking: 보안 이벤트 저장 성공
        when(saveSecurityEventPort.save(any(SecurityEvent.class))).thenReturn(expectedEventId);

        // when
        Long savedEventId = logSecurityEventService.logEvent(command);

        // then
        assertThat(savedEventId).isEqualTo(expectedEventId);
        verify(saveSecurityEventPort).save(any(SecurityEvent.class));
    }

    @Test
    @DisplayName("토큰 만료 이벤트 로깅 시 성공합니다")
    void log_token_expired_event_success() {
        // given
        LogSecurityEventCommand command = new LogSecurityEventCommand(
            SecurityEventType.TOKEN_EXPIRED,
            "user456",
            "10.0.0.1",
            "Chrome/91.0",
            "Access Token 만료"
        );
        Long expectedEventId = 2L;

        // Mocking: 보안 이벤트 저장 성공
        when(saveSecurityEventPort.save(any(SecurityEvent.class))).thenReturn(expectedEventId);

        // when
        Long savedEventId = logSecurityEventService.logEvent(command);

        // then
        assertThat(savedEventId).isEqualTo(expectedEventId);
        verify(saveSecurityEventPort).save(any(SecurityEvent.class));
    }

    @Test
    @DisplayName("Command가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        assertThatThrownBy(() -> logSecurityEventService.logEvent(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("userId가 null인 보안 이벤트도 정상적으로 로깅됩니다")
    void log_event_success_when_user_id_is_null() {
        // given
        LogSecurityEventCommand command = new LogSecurityEventCommand(
            SecurityEventType.SUSPICIOUS_ACCESS_PATTERN,
            null, // 익명 사용자
            "203.0.113.1",
            "Suspicious Bot/1.0",
            "의심스러운 접근 패턴 감지"
        );
        Long expectedEventId = 3L;

        // Mocking: 보안 이벤트 저장 성공
        when(saveSecurityEventPort.save(any(SecurityEvent.class))).thenReturn(expectedEventId);

        // when
        Long savedEventId = logSecurityEventService.logEvent(command);

        // then
        assertThat(savedEventId).isEqualTo(expectedEventId);
        verify(saveSecurityEventPort).save(any(SecurityEvent.class));
    }
}
