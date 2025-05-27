package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FindFamilyTreeQueryTest")
class FindFamilyTreeQueryTest {

    @Test
    @DisplayName("유효한 파라미터로 생성 시 정상적으로 객체가 생성됩니다")
    void create_success_with_valid_parameters() {
        // given
        Long familyId = 1L;
        Long centerMemberId = 2L;
        Integer maxGenerations = 3;

        // when
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(familyId, centerMemberId, maxGenerations);

        // then
        assertThat(query.familyId()).isEqualTo(familyId);
        assertThat(query.centerMemberId()).isEqualTo(centerMemberId);
        assertThat(query.maxGenerations()).isEqualTo(maxGenerations);
    }

    @Test
    @DisplayName("familyId가 null인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_null() {
        assertThatThrownBy(() -> new FindFamilyTreeQuery(null, 1L, 3))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("maxGenerations가 0인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_max_generations_is_zero() {
        assertThatThrownBy(() -> new FindFamilyTreeQuery(1L, 1L, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("maxGenerations must be positive number");
    }

    @Test
    @DisplayName("maxGenerations가 음수인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_max_generations_is_negative() {
        assertThatThrownBy(() -> new FindFamilyTreeQuery(1L, 1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("maxGenerations must be positive number");
    }

    @Test
    @DisplayName("maxGenerations가 10을 초과하는 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_max_generations_exceeds_limit() {
        assertThatThrownBy(() -> new FindFamilyTreeQuery(1L, 1L, 11))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("maxGenerations must not exceed 10");
    }

    @Test
    @DisplayName("withDefaults 팩토리 메서드로 생성 시 기본값이 적용됩니다")
    void create_with_defaults_success() {
        // given
        Long familyId = 1L;

        // when
        FindFamilyTreeQuery query = FindFamilyTreeQuery.withDefaults(familyId);

        // then
        assertThat(query.familyId()).isEqualTo(familyId);
        assertThat(query.centerMemberId()).isNull();
        assertThat(query.maxGenerations()).isEqualTo(3);
    }

    @Test
    @DisplayName("withCenterMember 팩토리 메서드로 생성 시 중심 구성원이 설정됩니다")
    void create_with_center_member_success() {
        // given
        Long familyId = 1L;
        Long centerMemberId = 2L;

        // when
        FindFamilyTreeQuery query = FindFamilyTreeQuery.withCenterMember(familyId, centerMemberId);

        // then
        assertThat(query.familyId()).isEqualTo(familyId);
        assertThat(query.centerMemberId()).isEqualTo(centerMemberId);
        assertThat(query.maxGenerations()).isEqualTo(3);
    }

    @Test
    @DisplayName("maxGenerations가 null인 경우 getMaxGenerationsOrDefault는 기본값 3을 반환합니다")
    void return_default_when_max_generations_is_null() {
        // given
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(1L, null, null);

        // when
        int result = query.getMaxGenerationsOrDefault();

        // then
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("maxGenerations가 설정된 경우 getMaxGenerationsOrDefault는 설정된 값을 반환합니다")
    void return_set_value_when_max_generations_is_not_null() {
        // given
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(1L, null, 5);

        // when
        int result = query.getMaxGenerationsOrDefault();

        // then
        assertThat(result).isEqualTo(5);
    }
}
