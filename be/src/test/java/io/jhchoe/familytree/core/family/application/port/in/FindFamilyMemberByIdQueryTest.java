package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FindFamilyMemberByIdQuery 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FindFamilyMemberByIdQueryTest")
class FindFamilyMemberByIdQueryTest {

    @Test
    @DisplayName("유효한 파라미터로 객체를 생성합니다")
    void create_query_when_valid_parameters() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = 3L;

        // when
        FindFamilyMemberByIdQuery query = new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId);

        // then
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
        assertThat(query.getTargetMemberId()).isEqualTo(targetMemberId);
    }

    @Test
    @DisplayName("familyId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long currentUserId = 2L;
        Long targetMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @Test
    @DisplayName("currentUserId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        Long familyId = 1L;
        Long currentUserId = null;
        Long targetMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 현재 사용자 ID가 필요합니다.");
    }

    @Test
    @DisplayName("targetMemberId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_target_member_id_is_null() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = null;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 대상 구성원 ID가 필요합니다.");
    }

    @Test
    @DisplayName("familyId가 0 이하일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_zero_or_negative() {
        // given
        Long familyId = 0L;
        Long currentUserId = 2L;
        Long targetMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @Test
    @DisplayName("currentUserId가 0 이하일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_current_user_id_is_zero_or_negative() {
        // given
        Long familyId = 1L;
        Long currentUserId = -1L;
        Long targetMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 현재 사용자 ID가 필요합니다.");
    }

    @Test
    @DisplayName("targetMemberId가 0 이하일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_target_member_id_is_zero_or_negative() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = 0L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 대상 구성원 ID가 필요합니다.");
    }
}
