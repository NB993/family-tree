package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] SaveFamilyMemberCommandTest")
class SaveFamilyMemberCommandTest {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("필수 필드만으로 Command가 정상 생성됩니다")
        void create_command_with_required_fields_only() {
            // given
            Long familyId = 1L;
            String name = "홍길동";

            // when
            SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                null,
                null
            );

            // then
            assertThat(command.getFamilyId()).isEqualTo(familyId);
            assertThat(command.getName()).isEqualTo(name);
            assertThat(command.getBirthday()).isNull();
            assertThat(command.getRelationshipType()).isNull();
            assertThat(command.getCustomRelationship()).isNull();
        }

        @Test
        @DisplayName("모든 필드를 포함하여 Command가 정상 생성됩니다")
        void create_command_with_all_fields() {
            // given
            Long familyId = 1L;
            String name = "홍길동";
            LocalDateTime birthday = LocalDateTime.of(1990, 1, 15, 0, 0);
            FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.FATHER;

            // when
            SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
                familyId,
                name,
                birthday,
                relationshipType,
                null
            );

            // then
            assertThat(command.getFamilyId()).isEqualTo(familyId);
            assertThat(command.getName()).isEqualTo(name);
            assertThat(command.getBirthday()).isEqualTo(birthday);
            assertThat(command.getRelationshipType()).isEqualTo(relationshipType);
        }

        @Test
        @DisplayName("CUSTOM 관계 타입과 사용자 정의 관계명으로 Command가 정상 생성됩니다")
        void create_command_with_custom_relationship() {
            // given
            Long familyId = 1L;
            String name = "홍길동";
            FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
            String customRelationship = "외할아버지";

            // when
            SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                relationshipType,
                customRelationship
            );

            // then
            assertThat(command.getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.CUSTOM);
            assertThat(command.getCustomRelationship()).isEqualTo(customRelationship);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("familyId가 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_family_id_is_null() {
            // given
            Long familyId = null;
            String name = "홍길동";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가족 ID는 필수입니다");
        }

        @Test
        @DisplayName("familyId가 0 이하인 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_family_id_is_not_positive() {
            // given
            Long familyId = 0L;
            String name = "홍길동";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 가족 ID입니다");
        }

        @Test
        @DisplayName("name이 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_name_is_null() {
            // given
            Long familyId = 1L;
            String name = null;

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름은 필수입니다");
        }

        @Test
        @DisplayName("name이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_name_is_blank() {
            // given
            Long familyId = 1L;
            String name = "   ";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름은 필수입니다");
        }

        @Test
        @DisplayName("CUSTOM 관계 타입인데 customRelationship이 null인 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_custom_relationship_is_null_for_custom_type() {
            // given
            Long familyId = 1L;
            String name = "홍길동";
            FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
            String customRelationship = null;

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                relationshipType,
                customRelationship
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CUSTOM 관계 유형을 선택한 경우 사용자 정의 관계명은 필수입니다");
        }

        @Test
        @DisplayName("CUSTOM 관계 타입인데 customRelationship이 빈 문자열인 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_custom_relationship_is_blank_for_custom_type() {
            // given
            Long familyId = 1L;
            String name = "홍길동";
            FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
            String customRelationship = "   ";

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                relationshipType,
                customRelationship
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CUSTOM 관계 유형을 선택한 경우 사용자 정의 관계명은 필수입니다");
        }

        @Test
        @DisplayName("customRelationship이 50자를 초과하는 경우 IllegalArgumentException이 발생합니다")
        void throw_exception_when_custom_relationship_exceeds_max_length() {
            // given
            Long familyId = 1L;
            String name = "홍길동";
            FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
            String customRelationship = "가".repeat(51);

            // when & then
            assertThatThrownBy(() -> new SaveFamilyMemberCommand(
                familyId,
                name,
                null,
                relationshipType,
                customRelationship
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 정의 관계명은 50자 이내로 작성해주세요");
        }
    }
}
