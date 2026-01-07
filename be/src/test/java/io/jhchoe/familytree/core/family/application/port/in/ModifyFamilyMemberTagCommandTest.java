package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ModifyFamilyMemberTagCommand 단위 테스트.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberTagCommandTest")
class ModifyFamilyMemberTagCommandTest {

    @Nested
    @DisplayName("Command 생성 테스트")
    class CreateCommandTest {

        @Test
        @DisplayName("유효한 입력으로 Command를 생성합니다")
        void create_command_with_valid_inputs() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            String name = "외가";
            String color = "#D3E5EF";

            // when
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, name, color
            );

            // then
            assertThat(command.familyId()).isEqualTo(familyId);
            assertThat(command.tagId()).isEqualTo(tagId);
            assertThat(command.name()).isEqualTo(name);
            assertThat(command.color()).isEqualTo(color);
        }

        @Test
        @DisplayName("color가 null인 경우 (이름만 변경) Command가 생성됩니다")
        void create_command_with_null_color() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            String name = "외가";
            String color = null;

            // when
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, name, color
            );

            // then
            assertThat(command.name()).isEqualTo(name);
            assertThat(command.color()).isNull();
        }

        @Test
        @DisplayName("familyId가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_family_id_is_null() {
            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagCommand(null, 10L, "친가", "#D3E5EF"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가족 ID");
        }

        @Test
        @DisplayName("tagId가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_tag_id_is_null() {
            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagCommand(1L, null, "친가", "#D3E5EF"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("태그 ID");
        }

        @Test
        @DisplayName("name이 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_name_is_null() {
            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagCommand(1L, 10L, null, "#D3E5EF"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름");
        }

        @Test
        @DisplayName("name이 10자를 초과하면 IllegalArgumentException이 발생합니다")
        void throw_when_name_exceeds_10_chars() {
            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagCommand(1L, 10L, "12345678901", "#D3E5EF"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10자");
        }

        @Test
        @DisplayName("color가 팔레트에 없으면 IllegalArgumentException이 발생합니다")
        void throw_when_color_not_in_palette() {
            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagCommand(1L, 10L, "친가", "#123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 색상");
        }
    }
}
