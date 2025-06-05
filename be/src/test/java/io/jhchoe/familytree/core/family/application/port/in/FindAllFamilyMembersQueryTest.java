package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FindAllFamilyMembersQuery 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FindAllFamilyMembersQueryTest")
class FindAllFamilyMembersQueryTest {

    @Test
    @DisplayName("유효한 familyId와 currentUserId로 객체를 생성합니다")
    void create_query_when_valid_parameters() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;

        // when
        FindAllFamilyMembersQuery query = new FindAllFamilyMembersQuery(familyId, currentUserId);

        // then
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
    }

    @Test
    @DisplayName("familyId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMembersQuery(familyId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @Test
    @DisplayName("currentUserId가 null일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        Long familyId = 1L;
        Long currentUserId = null;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMembersQuery(familyId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 현재 사용자 ID가 필요합니다.");
    }

    @Test
    @DisplayName("familyId가 0 이하일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_zero_or_negative() {
        // given
        Long familyId = 0L;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMembersQuery(familyId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @Test
    @DisplayName("currentUserId가 0 이하일 때 IllegalArgumentException이 발생합니다")
    void throw_exception_when_current_user_id_is_zero_or_negative() {
        // given
        Long familyId = 1L;
        Long currentUserId = -1L;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMembersQuery(familyId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 현재 사용자 ID가 필요합니다.");
    }
}
