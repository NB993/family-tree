package io.jhchoe.familytree.core.family.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FindPublicFamiliesQueryTest")
class FindPublicFamiliesQueryTest {

    @Test
    @DisplayName("유효한 파라미터로 Query 객체 생성에 성공합니다")
    void create_success_when_valid_parameters() {
        // given
        String keyword = "테스트가족";
        String cursor = "eyJpZCI6MSwidHlwZSI6Im5leHQifQ==";
        int size = 20;
        Long currentUserId = 1L;

        // when
        FindPublicFamiliesQuery query = new FindPublicFamiliesQuery(keyword, cursor, size, currentUserId);

        // then
        assertThat(query.getKeyword()).isEqualTo(keyword);
        assertThat(query.getCursor()).isEqualTo(cursor);
        assertThat(query.getSize()).isEqualTo(size);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
    }

    @Test
    @DisplayName("키워드가 null인 경우 Query 객체 생성에 성공합니다")
    void create_success_when_keyword_is_null() {
        // given
        String keyword = null;
        String cursor = null;
        int size = 20;
        Long currentUserId = 1L;

        // when
        FindPublicFamiliesQuery query = new FindPublicFamiliesQuery(keyword, cursor, size, currentUserId);

        // then
        assertThat(query.getKeyword()).isNull();
        assertThat(query.getCursor()).isNull();
        assertThat(query.getSize()).isEqualTo(size);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
    }

    @Test
    @DisplayName("키워드가 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_keyword_is_blank() {
        // given
        String keyword = "";
        String cursor = null;
        int size = 20;
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new FindPublicFamiliesQuery(keyword, cursor, size, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("키워드는 빈 문자열일 수 없습니다");
    }

    @Test
    @DisplayName("조회 개수가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_size_is_zero_or_negative() {
        // given
        String keyword = "테스트가족";
        String cursor = null;
        int invalidSize = 0;
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new FindPublicFamiliesQuery(keyword, cursor, invalidSize, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("조회 개수는 1~50 사이여야 합니다");
    }

    @Test
    @DisplayName("조회 개수가 50을 초과하는 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_size_exceeds_maximum() {
        // given
        String keyword = "테스트가족";
        String cursor = null;
        int invalidSize = 51;
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new FindPublicFamiliesQuery(keyword, cursor, invalidSize, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("조회 개수는 1~50 사이여야 합니다");
    }

    @Test
    @DisplayName("사용자 ID가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        String keyword = "테스트가족";
        String cursor = null;
        int size = 20;
        Long currentUserId = null;

        // when & then
        assertThatThrownBy(() -> new FindPublicFamiliesQuery(keyword, cursor, size, currentUserId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("currentUserId must not be null");
    }
}
