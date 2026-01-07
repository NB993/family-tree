package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * FamilyMemberTag 도메인 단위 테스트.
 */
@DisplayName("[Unit Test] FamilyMemberTagTest")
class FamilyMemberTagTest {

    @Nested
    @DisplayName("newTag 팩토리 메서드 테스트")
    class NewTagTest {

        @Test
        @DisplayName("유효한 입력으로 태그를 생성하면 COLOR_PALETTE 중 하나의 색상이 랜덤 배정됩니다")
        void newTag_creates_tag_with_random_color() {
            // given
            Long familyId = 1L;
            String name = "친가 어른들";
            Long createdBy = 100L;

            // when
            FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);

            // then
            assertThat(tag.getId()).isNull();
            assertThat(tag.getFamilyId()).isEqualTo(familyId);
            assertThat(tag.getName()).isEqualTo(name);
            assertThat(tag.getCreatedBy()).isEqualTo(createdBy);
            assertThat(FamilyMemberTag.COLOR_PALETTE).contains(tag.getColor());
        }

        @Test
        @DisplayName("태그 생성 시 생성 시간이 자동으로 설정됩니다")
        void newTag_sets_created_timestamp() {
            // given
            Long familyId = 1L;
            String name = "외가";
            Long createdBy = 100L;
            LocalDateTime before = LocalDateTime.now();

            // when
            FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);

            // then
            assertThat(tag.getCreatedAt()).isNotNull();
            assertThat(tag.getCreatedAt()).isAfterOrEqualTo(before);
        }

        @Test
        @DisplayName("이름이 null인 경우 NullPointerException이 발생합니다")
        void newTag_throws_when_name_is_null() {
            // given
            Long familyId = 1L;
            String name = null;
            Long createdBy = 100L;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTag.newTag(familyId, name, createdBy))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name");
        }

        @Test
        @DisplayName("이름이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
        void newTag_throws_when_name_is_empty() {
            // given
            Long familyId = 1L;
            String name = "";
            Long createdBy = 100L;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTag.newTag(familyId, name, createdBy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1자 이상");
        }

        @Test
        @DisplayName("이름이 공백만 있는 경우 IllegalArgumentException이 발생합니다")
        void newTag_throws_when_name_is_blank() {
            // given
            Long familyId = 1L;
            String name = "   ";
            Long createdBy = 100L;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTag.newTag(familyId, name, createdBy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1자 이상");
        }

        @Test
        @DisplayName("이름이 10자를 초과하면 IllegalArgumentException이 발생합니다")
        void newTag_throws_when_name_exceeds_10_chars() {
            // given
            Long familyId = 1L;
            String name = "12345678901"; // 11자
            Long createdBy = 100L;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTag.newTag(familyId, name, createdBy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10자 이하");
        }

        @Test
        @DisplayName("이름에 특수문자가 포함되면 IllegalArgumentException이 발생합니다")
        void newTag_throws_when_name_has_invalid_chars() {
            // given
            Long familyId = 1L;
            String name = "친가@어른들";
            Long createdBy = 100L;

            // when & then
            assertThatThrownBy(() -> FamilyMemberTag.newTag(familyId, name, createdBy))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 문자");
        }

        @Test
        @DisplayName("이름에 한글, 영문, 숫자, 이모지, 공백은 허용됩니다")
        void newTag_allows_valid_characters() {
            // given
            Long familyId = 1L;
            Long createdBy = 100L;

            // when & then - 모두 성공해야 함
            assertThat(FamilyMemberTag.newTag(familyId, "친가", createdBy).getName()).isEqualTo("친가");
            assertThat(FamilyMemberTag.newTag(familyId, "Family", createdBy).getName()).isEqualTo("Family");
            assertThat(FamilyMemberTag.newTag(familyId, "그룹1", createdBy).getName()).isEqualTo("그룹1");
            assertThat(FamilyMemberTag.newTag(familyId, "친가 어른", createdBy).getName()).isEqualTo("친가 어른");
            assertThat(FamilyMemberTag.newTag(familyId, "👨‍👩‍👧", createdBy).getName()).isEqualTo("👨‍👩‍👧");
        }
    }

    @Nested
    @DisplayName("withId 팩토리 메서드 테스트")
    class WithIdTest {

        @Test
        @DisplayName("모든 필드로 태그를 복원합니다")
        void withId_restores_tag_with_all_fields() {
            // given
            Long id = 1L;
            Long familyId = 10L;
            String name = "친가";
            String color = "#D3E5EF";
            Long createdBy = 100L;
            LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
            Long modifiedBy = 101L;
            LocalDateTime modifiedAt = LocalDateTime.of(2026, 1, 2, 10, 0);

            // when
            FamilyMemberTag tag = FamilyMemberTag.withId(
                id, familyId, name, color, createdBy, createdAt, modifiedBy, modifiedAt
            );

            // then
            assertThat(tag.getId()).isEqualTo(id);
            assertThat(tag.getFamilyId()).isEqualTo(familyId);
            assertThat(tag.getName()).isEqualTo(name);
            assertThat(tag.getColor()).isEqualTo(color);
            assertThat(tag.getCreatedBy()).isEqualTo(createdBy);
            assertThat(tag.getCreatedAt()).isEqualTo(createdAt);
            assertThat(tag.getModifiedBy()).isEqualTo(modifiedBy);
            assertThat(tag.getModifiedAt()).isEqualTo(modifiedAt);
        }

        @Test
        @DisplayName("ID가 null인 경우 NullPointerException이 발생합니다")
        void withId_throws_when_id_is_null() {
            // when & then
            assertThatThrownBy(() -> FamilyMemberTag.withId(
                null, 1L, "친가", "#D3E5EF", 100L, LocalDateTime.now(), null, null
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id");
        }
    }

    @Nested
    @DisplayName("rename 메서드 테스트")
    class RenameTest {

        @Test
        @DisplayName("이름 변경 시 새로운 태그 객체가 반환됩니다")
        void rename_returns_new_tag_with_changed_name() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.withId(
                1L, 10L, "친가", "#D3E5EF", 100L, LocalDateTime.now(), null, null
            );
            String newName = "외가";
            Long modifiedBy = 101L;

            // when
            FamilyMemberTag renamedTag = tag.rename(newName, modifiedBy);

            // then
            assertThat(renamedTag).isNotSameAs(tag);
            assertThat(renamedTag.getId()).isEqualTo(tag.getId());
            assertThat(renamedTag.getName()).isEqualTo(newName);
            assertThat(renamedTag.getColor()).isEqualTo(tag.getColor());
            assertThat(renamedTag.getModifiedBy()).isEqualTo(modifiedBy);
            assertThat(renamedTag.getModifiedAt()).isNotNull();
        }

        @Test
        @DisplayName("새 이름이 유효성 검증을 통과하지 못하면 예외가 발생합니다")
        void rename_throws_when_new_name_is_invalid() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.withId(
                1L, 10L, "친가", "#D3E5EF", 100L, LocalDateTime.now(), null, null
            );

            // when & then
            assertThatThrownBy(() -> tag.rename("12345678901", 101L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("changeColor 메서드 테스트")
    class ChangeColorTest {

        @Test
        @DisplayName("색상 변경 시 새로운 태그 객체가 반환됩니다")
        void changeColor_returns_new_tag_with_changed_color() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.withId(
                1L, 10L, "친가", "#D3E5EF", 100L, LocalDateTime.now(), null, null
            );
            String newColor = "#FFE2DD";
            Long modifiedBy = 101L;

            // when
            FamilyMemberTag changedTag = tag.changeColor(newColor, modifiedBy);

            // then
            assertThat(changedTag).isNotSameAs(tag);
            assertThat(changedTag.getId()).isEqualTo(tag.getId());
            assertThat(changedTag.getName()).isEqualTo(tag.getName());
            assertThat(changedTag.getColor()).isEqualTo(newColor);
            assertThat(changedTag.getModifiedBy()).isEqualTo(modifiedBy);
            assertThat(changedTag.getModifiedAt()).isNotNull();
        }

        @Test
        @DisplayName("팔레트에 없는 색상으로 변경하면 IllegalArgumentException이 발생합니다")
        void changeColor_throws_when_color_not_in_palette() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.withId(
                1L, 10L, "친가", "#D3E5EF", 100L, LocalDateTime.now(), null, null
            );
            String invalidColor = "#123456";

            // when & then
            assertThatThrownBy(() -> tag.changeColor(invalidColor, 101L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 색상");
        }
    }

    @Nested
    @DisplayName("COLOR_PALETTE 상수 테스트")
    class ColorPaletteTest {

        @Test
        @DisplayName("색상 팔레트는 9개의 색상을 포함합니다")
        void color_palette_contains_9_colors() {
            // then
            assertThat(FamilyMemberTag.COLOR_PALETTE).hasSize(9);
            assertThat(FamilyMemberTag.COLOR_PALETTE).containsExactly(
                "#E3E2E0", "#EEE0DA", "#FADEC9", "#FDECC8", "#DBEDDB",
                "#D3E5EF", "#E8DEEE", "#F5E0E9", "#FFE2DD"
            );
        }
    }
}
