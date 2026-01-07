package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * FamilyMemberTagMapping 도메인 단위 테스트.
 */
@DisplayName("[Unit Test] FamilyMemberTagMappingTest")
class FamilyMemberTagMappingTest {

    @Nested
    @DisplayName("newMapping 팩토리 메서드 테스트")
    class NewMappingTest {

        @Test
        @DisplayName("유효한 입력으로 매핑을 생성하면 생성 시간이 자동 설정됩니다")
        void newMapping_creates_mapping_with_timestamp() {
            // given
            Long tagId = 1L;
            Long memberId = 100L;
            LocalDateTime before = LocalDateTime.now();

            // when
            FamilyMemberTagMapping mapping = FamilyMemberTagMapping.newMapping(tagId, memberId);

            // then
            assertThat(mapping.getId()).isNull();
            assertThat(mapping.getTagId()).isEqualTo(tagId);
            assertThat(mapping.getMemberId()).isEqualTo(memberId);
            assertThat(mapping.getCreatedAt()).isNotNull();
            assertThat(mapping.getCreatedAt()).isAfterOrEqualTo(before);
        }

        @Test
        @DisplayName("tagId가 null인 경우 NullPointerException이 발생합니다")
        void newMapping_throws_when_tagId_is_null() {
            // given
            Long tagId = null;
            Long memberId = 100L;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMapping.newMapping(tagId, memberId))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("tagId");
        }

        @Test
        @DisplayName("memberId가 null인 경우 NullPointerException이 발생합니다")
        void newMapping_throws_when_memberId_is_null() {
            // given
            Long tagId = 1L;
            Long memberId = null;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMapping.newMapping(tagId, memberId))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("memberId");
        }
    }

    @Nested
    @DisplayName("withId 팩토리 메서드 테스트")
    class WithIdTest {

        @Test
        @DisplayName("모든 필드로 매핑을 복원합니다")
        void withId_restores_mapping_with_all_fields() {
            // given
            Long id = 1L;
            Long tagId = 10L;
            Long memberId = 100L;
            LocalDateTime createdAt = LocalDateTime.of(2026, 1, 7, 10, 0);

            // when
            FamilyMemberTagMapping mapping = FamilyMemberTagMapping.withId(
                id, tagId, memberId, createdAt
            );

            // then
            assertThat(mapping.getId()).isEqualTo(id);
            assertThat(mapping.getTagId()).isEqualTo(tagId);
            assertThat(mapping.getMemberId()).isEqualTo(memberId);
            assertThat(mapping.getCreatedAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("ID가 null인 경우 NullPointerException이 발생합니다")
        void withId_throws_when_id_is_null() {
            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMapping.withId(
                null, 10L, 100L, LocalDateTime.now()
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id");
        }

        @Test
        @DisplayName("tagId가 null인 경우 NullPointerException이 발생합니다")
        void withId_throws_when_tagId_is_null() {
            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMapping.withId(
                1L, null, 100L, LocalDateTime.now()
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("tagId");
        }

        @Test
        @DisplayName("memberId가 null인 경우 NullPointerException이 발생합니다")
        void withId_throws_when_memberId_is_null() {
            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMapping.withId(
                1L, 10L, null, LocalDateTime.now()
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("memberId");
        }

        @Test
        @DisplayName("createdAt이 null인 경우 NullPointerException이 발생합니다")
        void withId_throws_when_createdAt_is_null() {
            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMapping.withId(
                1L, 10L, 100L, null
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("createdAt");
        }
    }
}
