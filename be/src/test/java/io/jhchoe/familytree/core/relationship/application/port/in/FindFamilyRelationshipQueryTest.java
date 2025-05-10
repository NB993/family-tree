package io.jhchoe.familytree.core.relationship.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("[Unit Test] FindFamilyRelationshipQuery")
class FindFamilyRelationshipQueryTest {

    @Test
    @DisplayName("유효한 입력값으로 쿼리 객체를 생성할 수 있다")
    void given_valid_inputs_when_create_query_then_create_query_object() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;

        // when
        FindFamilyRelationshipQuery query = new FindFamilyRelationshipQuery(familyId, fromMemberId, toMemberId);

        // then
        assertThat(query).isNotNull();
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(query.getToMemberId()).isEqualTo(toMemberId);
    }

    @Test
    @DisplayName("familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_create_query_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(familyId, fromMemberId, toMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("familyId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_family_id_when_create_query_then_throw_exception(Long invalidFamilyId) {
        // given
        Long fromMemberId = 2L;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(invalidFamilyId, fromMemberId, toMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @Test
    @DisplayName("fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_create_query_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(familyId, fromMemberId, toMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 구성원 ID가 필요합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("fromMemberId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_from_member_id_when_create_query_then_throw_exception(Long invalidFromMemberId) {
        // given
        Long familyId = 1L;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(familyId, invalidFromMemberId, toMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 구성원 ID가 필요합니다.");
    }

    @Test
    @DisplayName("toMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_to_member_id_when_create_query_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = null;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(familyId, fromMemberId, toMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 대상 구성원 ID가 필요합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("toMemberId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_to_member_id_when_create_query_then_throw_exception(Long invalidToMemberId) {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(familyId, fromMemberId, invalidToMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 대상 구성원 ID가 필요합니다.");
    }

    @Test
    @DisplayName("fromMemberId와 toMemberId가 같으면 예외를 발생시켜야 한다")
    void given_same_member_ids_when_create_query_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyRelationshipQuery(familyId, memberId, memberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관계를 정의하는 사람과 대상은 서로 달라야 합니다.");
    }
}
