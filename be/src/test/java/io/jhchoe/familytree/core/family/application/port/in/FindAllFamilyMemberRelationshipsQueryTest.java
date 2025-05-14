package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("[Unit Test] FindMemberRelationshipsQuery")
class FindAllFamilyMemberRelationshipsQueryTest {

    @Test
    @DisplayName("유효한 입력값으로 쿼리 객체를 생성할 수 있다")
    void given_valid_inputs_when_create_query_then_create_query_object() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;

        // when
        FindAllFamilyMemberRelationshipsQuery query = new FindAllFamilyMemberRelationshipsQuery(familyId, fromMemberId);

        // then
        assertThat(query).isNotNull();
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getFromMemberId()).isEqualTo(fromMemberId);
    }

    @Test
    @DisplayName("familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_create_query_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMemberRelationshipsQuery(familyId, fromMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("familyId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_family_id_when_create_query_then_throw_exception(Long invalidFamilyId) {
        // given
        Long fromMemberId = 2L;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMemberRelationshipsQuery(invalidFamilyId, fromMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 가족 ID가 필요합니다.");
    }

    @Test
    @DisplayName("fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_create_query_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMemberRelationshipsQuery(familyId, fromMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 구성원 ID가 필요합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("fromMemberId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_from_member_id_when_create_query_then_throw_exception(Long invalidFromMemberId) {
        // given
        Long familyId = 1L;

        // when & then
        assertThatThrownBy(() -> new FindAllFamilyMemberRelationshipsQuery(familyId, invalidFromMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 구성원 ID가 필요합니다.");
    }
}
