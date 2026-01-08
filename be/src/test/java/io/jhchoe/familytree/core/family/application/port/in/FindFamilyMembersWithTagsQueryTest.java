package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FindFamilyMembersWithTagsQuery 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FindFamilyMembersWithTagsQueryTest")
class FindFamilyMembersWithTagsQueryTest {

    @Test
    @DisplayName("familyId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_familyId_is_null() {
        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersWithTagsQuery(null, 1L))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId");
    }

    @Test
    @DisplayName("currentUserId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_currentUserId_is_null() {
        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersWithTagsQuery(1L, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("currentUserId");
    }

    @Test
    @DisplayName("familyId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_familyId_is_not_positive() {
        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersWithTagsQuery(0L, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("familyId");
    }

    @Test
    @DisplayName("currentUserId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_currentUserId_is_not_positive() {
        // when & then
        assertThatThrownBy(() -> new FindFamilyMembersWithTagsQuery(1L, 0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("currentUserId");
    }

    @Test
    @DisplayName("유효한 값으로 쿼리 객체가 생성됩니다")
    void create_query_when_values_are_valid() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;

        // when
        FindFamilyMembersWithTagsQuery query = new FindFamilyMembersWithTagsQuery(familyId, currentUserId);

        // then
        assertThat(query.familyId()).isEqualTo(familyId);
        assertThat(query.currentUserId()).isEqualTo(currentUserId);
    }
}
