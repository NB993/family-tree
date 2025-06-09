package io.jhchoe.familytree.core.family.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] CursorUtilsTest")
class CursorUtilsTest {

    @Test
    @DisplayName("Family ID와 memberCount로 커서를 성공적으로 인코딩합니다")
    void encode_cursor_success_when_valid_parameters() {
        // given
        Long familyId = 123L;
        int memberCount = 5;

        // when
        String cursor = CursorUtils.encodeCursor(familyId, memberCount);

        // then
        assertThat(cursor).isNotNull();
        assertThat(cursor).isNotBlank();
    }

    @Test
    @DisplayName("인코딩된 커서를 성공적으로 디코딩합니다")
    void decode_cursor_success_when_valid_cursor() {
        // given
        Long familyId = 123L;
        int memberCount = 5;
        String cursor = CursorUtils.encodeCursor(familyId, memberCount);

        // when
        CursorUtils.CursorInfo cursorInfo = CursorUtils.decodeCursor(cursor);

        // then
        assertThat(cursorInfo.familyId()).isEqualTo(familyId);
        assertThat(cursorInfo.memberCount()).isEqualTo(memberCount);
    }

    @Test
    @DisplayName("Family ID가 null인 경우 커서 인코딩 시 IllegalArgumentException이 발생합니다")
    void throw_exception_when_family_id_is_null_for_encoding() {
        // given
        Long familyId = null;
        int memberCount = 5;

        // when & then
        assertThatThrownBy(() -> CursorUtils.encodeCursor(familyId, memberCount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("memberCount가 음수인 경우 커서 인코딩 시 IllegalArgumentException이 발생합니다")
    void throw_exception_when_member_count_is_negative_for_encoding() {
        // given
        Long familyId = 123L;
        int memberCount = -1;

        // when & then
        assertThatThrownBy(() -> CursorUtils.encodeCursor(familyId, memberCount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("memberCount must not be negative");
    }

    @Test
    @DisplayName("커서가 null인 경우 디코딩 시 IllegalArgumentException이 발생합니다")
    void throw_exception_when_cursor_is_null_for_decoding() {
        // given
        String cursor = null;

        // when & then
        assertThatThrownBy(() -> CursorUtils.decodeCursor(cursor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("cursor must not be null or blank");
    }

    @Test
    @DisplayName("커서가 빈 문자열인 경우 디코딩 시 IllegalArgumentException이 발생합니다")
    void throw_exception_when_cursor_is_blank_for_decoding() {
        // given
        String cursor = "";

        // when & then
        assertThatThrownBy(() -> CursorUtils.decodeCursor(cursor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("cursor must not be null or blank");
    }

    @Test
    @DisplayName("잘못된 형식의 커서 디코딩 시 IllegalArgumentException이 발생합니다")
    void throw_exception_when_cursor_format_is_invalid() {
        // given
        String invalidCursor = "aW52YWxpZCBjdXJzb3I="; // "invalid cursor" in base64

        // when & then
        assertThatThrownBy(() -> CursorUtils.decodeCursor(invalidCursor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid cursor format");
    }

    @Test
    @DisplayName("CursorInfo 생성 시 familyId가 null이면 IllegalArgumentException이 발생합니다")
    void throw_exception_when_cursor_info_family_id_is_null() {
        // given
        int memberCount = 5;
        Long familyId = null;

        // when & then
        assertThatThrownBy(() -> new CursorUtils.CursorInfo(memberCount, familyId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("CursorInfo 생성 시 memberCount가 음수이면 IllegalArgumentException이 발생합니다")
    void throw_exception_when_cursor_info_member_count_is_negative() {
        // given
        int memberCount = -1;
        Long familyId = 123L;

        // when & then
        assertThatThrownBy(() -> new CursorUtils.CursorInfo(memberCount, familyId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("memberCount must not be negative");
    }
}
