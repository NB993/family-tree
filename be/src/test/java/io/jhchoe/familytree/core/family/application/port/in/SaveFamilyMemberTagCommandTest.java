package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SaveFamilyMemberTagCommand 단위 테스트.
 */
@DisplayName("[Unit Test] SaveFamilyMemberTagCommandTest")
class SaveFamilyMemberTagCommandTest {

    @Nested
    @DisplayName("Command 생성 테스트")
    class CreateCommandTest {

        @Test
        @DisplayName("유효한 입력으로 Command를 생성합니다")
        void create_command_with_valid_inputs() {
            // given
            Long familyId = 1L;
            String name = "친가 어른들";

            // when
            SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(familyId, name);

            // then
            assertThat(command.familyId()).isEqualTo(familyId);
            assertThat(command.name()).isEqualTo(name);
        }

        @Test
        @DisplayName("familyId가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_family_id_is_null() {
            // given
            Long familyId = null;
            String name = "친가";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberTagCommand(familyId, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가족 ID");
        }

        @Test
        @DisplayName("familyId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_family_id_is_not_positive() {
            // given
            Long familyId = 0L;
            String name = "친가";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberTagCommand(familyId, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은");
        }

        @Test
        @DisplayName("name이 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_name_is_null() {
            // given
            Long familyId = 1L;
            String name = null;

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberTagCommand(familyId, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름");
        }

        @Test
        @DisplayName("name이 공백만 있는 경우 IllegalArgumentException이 발생합니다")
        void throw_when_name_is_blank() {
            // given
            Long familyId = 1L;
            String name = "   ";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberTagCommand(familyId, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름");
        }

        @Test
        @DisplayName("name이 10자를 초과하면 IllegalArgumentException이 발생합니다")
        void throw_when_name_exceeds_10_chars() {
            // given
            Long familyId = 1L;
            String name = "12345678901"; // 11자

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberTagCommand(familyId, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10자");
        }

        @Test
        @DisplayName("name에 특수문자가 포함되면 IllegalArgumentException이 발생합니다")
        void throw_when_name_has_invalid_pattern() {
            // given
            Long familyId = 1L;
            String name = "친가@어른";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberTagCommand(familyId, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는");
        }
    }
}
