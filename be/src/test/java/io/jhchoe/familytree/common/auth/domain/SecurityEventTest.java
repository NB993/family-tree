package io.jhchoe.familytree.common.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] SecurityEventTest")
class SecurityEventTest {

    @Test
    @DisplayName("유효한 파라미터로 새 보안 이벤트 생성 시 성공합니다")
    void create_new_event_success_when_valid_parameters() {
        // given
        SecurityEventType eventType = SecurityEventType.AUTHENTICATION_FAILURE;
        String userId = "user123";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        String description = "로그인 실패";

        // when
        SecurityEvent event = SecurityEvent.newEvent(eventType, userId, ipAddress, userAgent, description);

        // then
        assertThat(event.getId()).isNull();
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getIpAddress()).isEqualTo(ipAddress);
        assertThat(event.getUserAgent()).isEqualTo(userAgent);
        assertThat(event.getDescription()).isEqualTo(description);
        assertThat(event.getOccurredAt()).isNotNull();
    }

    @Test
    @DisplayName("ID와 함께 기존 보안 이벤트 복원 시 성공합니다")
    void create_with_id_success_when_valid_parameters() {
        // given
        Long id = 1L;
        SecurityEventType eventType = SecurityEventType.TOKEN_EXPIRED;
        String userId = "user456";
        String ipAddress = "10.0.0.1";
        String userAgent = "Chrome/91.0";
        String description = "토큰 만료 이벤트";
        LocalDateTime occurredAt = LocalDateTime.of(2025, 6, 8, 10, 30);

        // when
        SecurityEvent event = SecurityEvent.withId(
            id, eventType, userId, ipAddress, userAgent, description, occurredAt
        );

        // then
        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getIpAddress()).isEqualTo(ipAddress);
        assertThat(event.getUserAgent()).isEqualTo(userAgent);
        assertThat(event.getDescription()).isEqualTo(description);
        assertThat(event.getOccurredAt()).isEqualTo(occurredAt);
    }

    @Test
    @DisplayName("이벤트 타입이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_event_type_is_null() {
        assertThatThrownBy(() -> SecurityEvent.newEvent(
            null, "user123", "192.168.1.1", "Mozilla/5.0", "설명"
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("eventType must not be null");
    }

    @Test
    @DisplayName("IP 주소가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_ip_address_is_null() {
        assertThatThrownBy(() -> SecurityEvent.newEvent(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", null, "Mozilla/5.0", "설명"
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("ipAddress must not be null");
    }

    @Test
    @DisplayName("IP 주소가 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_ip_address_is_blank() {
        assertThatThrownBy(() -> SecurityEvent.newEvent(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", "", "Mozilla/5.0", "설명"
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ipAddress must not be blank");
    }

    @Test
    @DisplayName("설명이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_description_is_null() {
        assertThatThrownBy(() -> SecurityEvent.newEvent(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", "192.168.1.1", "Mozilla/5.0", null
        ))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("description must not be null");
    }

    @Test
    @DisplayName("설명이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_description_is_blank() {
        assertThatThrownBy(() -> SecurityEvent.newEvent(
            SecurityEventType.AUTHENTICATION_FAILURE, "user123", "192.168.1.1", "Mozilla/5.0", ""
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("description must not be blank");
    }

    @Test
    @DisplayName("userId와 userAgent는 null이어도 보안 이벤트 생성에 성공합니다")
    void create_event_success_when_optional_parameters_are_null() {
        // given
        SecurityEventType eventType = SecurityEventType.RATE_LIMIT_EXCEEDED;
        String ipAddress = "203.0.113.1";
        String description = "API 요청 제한 초과";

        // when
        SecurityEvent event = SecurityEvent.newEvent(eventType, null, ipAddress, null, description);

        // then
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getUserId()).isNull();
        assertThat(event.getIpAddress()).isEqualTo(ipAddress);
        assertThat(event.getUserAgent()).isNull();
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("동일한 값을 가진 보안 이벤트는 equals에서 true를 반환합니다")
    void return_true_when_same_values_in_equals() {
        // given
        LocalDateTime occurredAt = LocalDateTime.of(2025, 6, 8, 15, 30);
        SecurityEvent event1 = SecurityEvent.withId(
            1L, SecurityEventType.LOGOUT_SUCCESS, "user123", "192.168.1.1", 
            "Mozilla/5.0", "로그아웃 성공", occurredAt
        );
        SecurityEvent event2 = SecurityEvent.withId(
            1L, SecurityEventType.LOGOUT_SUCCESS, "user123", "192.168.1.1", 
            "Mozilla/5.0", "로그아웃 성공", occurredAt
        );

        // when & then
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }
}
