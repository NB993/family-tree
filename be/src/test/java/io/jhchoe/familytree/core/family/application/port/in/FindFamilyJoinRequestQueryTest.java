package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FindFamilyJoinRequestQuery")
class FindFamilyJoinRequestQueryTest {

    @Test
    @DisplayName("유효한 familyId와 currentUserId로 쿼리 객체를 생성할 수 있다")
    void should_create_query_when_valid_parameters() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;

        // when
        FindFamilyJoinRequestQuery query = new FindFamilyJoinRequestQuery(familyId, currentUserId);

        // then
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
    }

    @Test
    @DisplayName("familyId가 null이면 예외를 발생시킨다")
    void should_throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyJoinRequestQuery(familyId, currentUserId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("currentUserId가 null이면 예외를 발생시킨다")
    void should_throw_exception_when_current_user_id_is_null() {
        // given
        Long familyId = 1L;
        Long currentUserId = null;

        // when & then
        assertThatThrownBy(() -> new FindFamilyJoinRequestQuery(familyId, currentUserId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("currentUserId must not be null");
    }

    @Test
    @DisplayName("familyId가 0이면 예외를 발생시킨다")
    void should_throw_exception_when_family_id_is_zero() {
        // given
        Long familyId = 0L;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyJoinRequestQuery(familyId, currentUserId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("familyId must be positive");
    }

    @Test
    @DisplayName("familyId가 음수이면 예외를 발생시킨다")
    void should_throw_exception_when_family_id_is_negative() {
        // given
        Long familyId = -1L;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyJoinRequestQuery(familyId, currentUserId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("familyId must be positive");
    }

    @Test
    @DisplayName("currentUserId가 0이면 예외를 발생시킨다")
    void should_throw_exception_when_current_user_id_is_zero() {
        // given
        Long familyId = 1L;
        Long currentUserId = 0L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyJoinRequestQuery(familyId, currentUserId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("currentUserId must be positive");
    }

    @Test
    @DisplayName("currentUserId가 음수이면 예외를 발생시킨다")
    void should_throw_exception_when_current_user_id_is_negative() {
        // given
        Long familyId = 1L;
        Long currentUserId = -1L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyJoinRequestQuery(familyId, currentUserId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("currentUserId must be positive");
    }
}
