package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ModifyFamilyMemberTagMappingCommand 단위 테스트.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberTagMappingCommandTest")
class ModifyFamilyMemberTagMappingCommandTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 입력으로 Command를 생성합니다")
        void create_command_with_valid_inputs() {
            // given
            Long familyId = 1L;
            Long memberId = 100L;
            List<Long> tagIds = List.of(1L, 2L, 3L);

            // when
            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            // then
            assertThat(command.familyId()).isEqualTo(familyId);
            assertThat(command.memberId()).isEqualTo(memberId);
            assertThat(command.tagIds()).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("빈 tagIds로 Command를 생성합니다 (모든 태그 해제)")
        void create_command_with_empty_tagIds() {
            // given
            Long familyId = 1L;
            Long memberId = 100L;
            List<Long> tagIds = Collections.emptyList();

            // when
            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            // then
            assertThat(command.familyId()).isEqualTo(familyId);
            assertThat(command.memberId()).isEqualTo(memberId);
            assertThat(command.tagIds()).isEmpty();
        }

        @Test
        @DisplayName("null tagIds는 빈 리스트로 변환됩니다")
        void create_command_with_null_tagIds_converts_to_empty_list() {
            // given
            Long familyId = 1L;
            Long memberId = 100L;
            List<Long> tagIds = null;

            // when
            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            // then
            assertThat(command.tagIds()).isNotNull();
            assertThat(command.tagIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("familyId가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_family_id_is_null() {
            // given
            Long familyId = null;
            Long memberId = 100L;
            List<Long> tagIds = List.of(1L);

            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가족 ID");
        }

        @Test
        @DisplayName("familyId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_family_id_is_not_positive() {
            // given
            Long familyId = 0L;
            Long memberId = 100L;
            List<Long> tagIds = List.of(1L);

            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가족 ID");
        }

        @Test
        @DisplayName("memberId가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_member_id_is_null() {
            // given
            Long familyId = 1L;
            Long memberId = null;
            List<Long> tagIds = List.of(1L);

            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("멤버 ID");
        }

        @Test
        @DisplayName("memberId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
        void throw_when_member_id_is_not_positive() {
            // given
            Long familyId = 1L;
            Long memberId = 0L;
            List<Long> tagIds = List.of(1L);

            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("멤버 ID");
        }

        @Test
        @DisplayName("tagIds가 10개를 초과하면 IllegalArgumentException이 발생합니다")
        void throw_when_tag_ids_exceeds_10() {
            // given
            Long familyId = 1L;
            Long memberId = 100L;
            List<Long> tagIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);

            // when & then
            assertThatThrownBy(() -> new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10개");
        }
    }
}
