package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * [Unit Test] ProcessFamilyJoinRequestCommandTest
 */
@DisplayName("[Unit Test] ProcessFamilyJoinRequestCommandTest")
class ProcessFamilyJoinRequestCommandTest {

    @Test
    @DisplayName("모든 필수 값이 유효할 때 Command 객체 생성에 성공합니다")
    void create_success_when_all_required_values_are_valid() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.APPROVED;
        String message = "가족으로 환영합니다";
        Long currentUserId = 1L;

        // when
        ProcessFamilyJoinRequestCommand command = new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        );

        // then
        assertThat(command.getFamilyId()).isEqualTo(familyId);
        assertThat(command.getRequestId()).isEqualTo(requestId);
        assertThat(command.getStatus()).isEqualTo(status);
        assertThat(command.getMessage()).isEqualTo(message);
        assertThat(command.getCurrentUserId()).isEqualTo(currentUserId);
    }

    @Test
    @DisplayName("message가 null일 때도 Command 객체 생성에 성공합니다")
    void create_success_when_message_is_null() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.REJECTED;
        String message = null;
        Long currentUserId = 1L;

        // when
        ProcessFamilyJoinRequestCommand command = new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        );

        // then
        assertThat(command.getMessage()).isNull();
    }

    @Test
    @DisplayName("familyId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long requestId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.APPROVED;
        String message = "test";
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("requestId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_request_id_is_null() {
        // given
        Long familyId = 1L;
        Long requestId = null;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.APPROVED;
        String message = "test";
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("requestId must not be null");
    }

    @Test
    @DisplayName("status가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_status_is_null() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        FamilyJoinRequestStatus status = null;
        String message = "test";
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("status must not be null");
    }

    @Test
    @DisplayName("currentUserId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.APPROVED;
        String message = "test";
        Long currentUserId = null;

        // when & then
        assertThatThrownBy(() -> new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("currentUserId must not be null");
    }

    @Test
    @DisplayName("status가 PENDING일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_status_is_pending() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.PENDING;
        String message = "test";
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new ProcessFamilyJoinRequestCommand(
            familyId, requestId, status, message, currentUserId
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("status must be APPROVED or REJECTED");
    }
}
