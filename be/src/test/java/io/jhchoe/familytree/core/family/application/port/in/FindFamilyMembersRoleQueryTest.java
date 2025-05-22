package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FindFamilyMembersRoleQuery 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FindFamilyMembersRoleQueryTest")
class FindFamilyMembersRoleQueryTest {

    @Test
    @DisplayName("올바른 매개변수로 쿼리 객체를 생성할 수 있습니다")
    void create_query_with_valid_parameters() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;

        // when
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(familyId, currentUserId);

        // then
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
    }

    @Test
    @DisplayName("familyId가 null인 경우 예외가 발생합니다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersRoleQuery(familyId, currentUserId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("currentUserId가 null인 경우 예외가 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        Long familyId = 1L;
        Long currentUserId = null;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersRoleQuery(familyId, currentUserId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("currentUserId must not be null");
    }

    @Test
    @DisplayName("familyId가 0 이하인 경우 예외가 발생합니다")
    void throw_exception_when_family_id_is_not_positive() {
        // given
        Long familyId = 0L;
        Long currentUserId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersRoleQuery(familyId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("familyId must be positive");
    }

    @Test
    @DisplayName("currentUserId가 0 이하인 경우 예외가 발생합니다")
    void throw_exception_when_current_user_id_is_not_positive() {
        // given
        Long familyId = 1L;
        Long currentUserId = -1L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersRoleQuery(familyId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("currentUserId must be positive");
    }
}
