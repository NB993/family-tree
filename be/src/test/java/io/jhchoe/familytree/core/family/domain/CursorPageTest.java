package io.jhchoe.familytree.core.family.domain;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] CursorPageTest")
class CursorPageTest {

    @Test
    @DisplayName("유효한 파라미터로 CursorPage 객체 생성에 성공합니다")
    void create_success_when_valid_parameters() {
        // given
        List<String> content = List.of("item1", "item2", "item3");
        String nextCursor = "eyJpZCI6MywidHlwZSI6Im5leHQifQ==";
        boolean hasNext = true;
        int size = 20;

        // when
        CursorPage<String> cursorPage = new CursorPage<>(content, nextCursor, hasNext, size);

        // then
        assertThat(cursorPage.getContent()).isEqualTo(content);
        assertThat(cursorPage.getNextCursor()).isEqualTo(nextCursor);
        assertThat(cursorPage.isHasNext()).isTrue();
        assertThat(cursorPage.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("다음 페이지가 없는 경우 nextCursor는 null이고 hasNext는 false입니다")
    void create_success_when_no_next_page() {
        // given
        List<String> content = List.of("item1", "item2");
        String nextCursor = null;
        boolean hasNext = false;
        int size = 20;

        // when
        CursorPage<String> cursorPage = new CursorPage<>(content, nextCursor, hasNext, size);

        // then
        assertThat(cursorPage.getContent()).isEqualTo(content);
        assertThat(cursorPage.getNextCursor()).isNull();
        assertThat(cursorPage.isHasNext()).isFalse();
        assertThat(cursorPage.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("빈 컬렉션으로 CursorPage 객체 생성에 성공합니다")
    void create_success_when_empty_content() {
        // given
        List<String> content = List.of();
        String nextCursor = null;
        boolean hasNext = false;
        int size = 20;

        // when
        CursorPage<String> cursorPage = new CursorPage<>(content, nextCursor, hasNext, size);

        // then
        assertThat(cursorPage.getContent()).isEmpty();
        assertThat(cursorPage.getNextCursor()).isNull();
        assertThat(cursorPage.isHasNext()).isFalse();
        assertThat(cursorPage.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("empty() 정적 메서드로 빈 페이지 생성에 성공합니다")
    void create_empty_page_success() {
        // given
        int size = 20;

        // when
        CursorPage<String> emptyPage = CursorPage.empty(size);

        // then
        assertThat(emptyPage.getContent()).isEmpty();
        assertThat(emptyPage.getNextCursor()).isNull();
        assertThat(emptyPage.isHasNext()).isFalse();
        assertThat(emptyPage.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("content가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_content_is_null() {
        // given
        List<String> content = null;
        String nextCursor = "cursor";
        boolean hasNext = true;
        int size = 20;

        // when & then
        assertThatThrownBy(() -> new CursorPage<>(content, nextCursor, hasNext, size))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("content must not be null");
    }

    @Test
    @DisplayName("size가 음수인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_size_is_negative() {
        // given
        List<String> content = List.of("item1");
        String nextCursor = "cursor";
        boolean hasNext = false;
        int invalidSize = -1;

        // when & then
        assertThatThrownBy(() -> new CursorPage<>(content, nextCursor, hasNext, invalidSize))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("size must not be negative");
    }
}
