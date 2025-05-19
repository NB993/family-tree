package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] SaveFamilyJoinRequestCommandTest")
class SaveFamilyJoinRequestCommandTest {

    @Test
    @DisplayName("유효한 파라미터로 커맨드 객체를 생성할 수 있다")
    void create_command_with_valid_parameters() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;

        // when
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        // then
        assertThat(command.getFamilyId()).isEqualTo(familyId);
        assertThat(command.getRequesterId()).isEqualTo(requesterId);
    }

    @Test
    @DisplayName("familyId가 null이면 예외가 발생한다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long requesterId = 2L;

        // when, then
        assertThatThrownBy(() -> new SaveFamilyJoinRequestCommand(familyId, requesterId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("가입할 Family ID는 필수입니다.");
    }

    @Test
    @DisplayName("familyId가 0 이하면 예외가 발생한다")
    void throw_exception_when_family_id_is_less_than_or_equal_to_zero() {
        // given
        Long familyId = 0L;
        Long requesterId = 2L;

        // when, then
        assertThatThrownBy(() -> new SaveFamilyJoinRequestCommand(familyId, requesterId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 Family ID입니다.");
    }

    @Test
    @DisplayName("requesterId가 null이면 예외가 발생한다")
    void throw_exception_when_requester_id_is_null() {
        // given
        Long familyId = 1L;
        Long requesterId = null;

        // when, then
        assertThatThrownBy(() -> new SaveFamilyJoinRequestCommand(familyId, requesterId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("신청자 ID는 필수입니다.");
    }

    @Test
    @DisplayName("requesterId가 0 이하면 예외가 발생한다")
    void throw_exception_when_requester_id_is_less_than_or_equal_to_zero() {
        // given
        Long familyId = 1L;
        Long requesterId = 0L;

        // when, then
        assertThatThrownBy(() -> new SaveFamilyJoinRequestCommand(familyId, requesterId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 신청자 ID입니다.");
    }
}
