package io.jhchoe.familytree.core.relationship.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.relationship.domain.FamilyMemberRelationshipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("[Unit Test] DefineFamilyRelationshipCommand")
class SaveFamilyMemberRelationshipCommandTest {

    @Test
    @DisplayName("유효한 입력값으로 명령 객체를 생성할 수 있다")
    void given_valid_inputs_when_create_command_then_create_command_object() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when
        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        // then
        assertThat(command).isNotNull();
        assertThat(command.getFamilyId()).isEqualTo(familyId);
        assertThat(command.getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(command.getToMemberId()).isEqualTo(toMemberId);
        assertThat(command.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(command.getCustomRelationship()).isEqualTo(customRelationship);
        assertThat(command.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("CUSTOM 관계 유형으로 유효한 명령 객체를 생성할 수 있다")
    void given_valid_custom_relationship_when_create_command_then_create_command_object() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "큰아버지";
        String description = "아버지의 형제";

        // when
        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        // then
        assertThat(command).isNotNull();
        assertThat(command.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(command.getCustomRelationship()).isEqualTo(customRelationship);
    }

    @Test
    @DisplayName("familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_create_command_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족 ID는 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("familyId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_family_id_when_create_command_then_throw_exception(Long invalidFamilyId) {
        // given
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            invalidFamilyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효하지 않은 가족 ID입니다.");
    }

    @Test
    @DisplayName("fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관계를 정의하는 구성원 ID는 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("fromMemberId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_from_member_id_when_create_command_then_throw_exception(Long invalidFromMemberId) {
        // given
        Long familyId = 1L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            invalidFromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효하지 않은 구성원 ID입니다.");
    }

    @Test
    @DisplayName("toMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_to_member_id_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = null;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관계를 정의할 대상 구성원 ID는 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("toMemberId가 0 이하면 예외를 발생시켜야 한다")
    void given_invalid_to_member_id_when_create_command_then_throw_exception(Long invalidToMemberId) {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            invalidToMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효하지 않은 대상 구성원 ID입니다.");
    }

    @Test
    @DisplayName("fromMemberId와 toMemberId가 같으면 예외를 발생시켜야 한다")
    void given_same_member_ids_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            memberId,
            memberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관계를 정의하는 사람과 대상은 서로 달라야 합니다.");
    }

    @Test
    @DisplayName("relationshipType이 null이면 예외를 발생시켜야 한다")
    void given_null_relationship_type_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = null;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관계 유형은 필수입니다.");
    }

    @Test
    @DisplayName("relationshipType이 CUSTOM이고 customRelationship이 null이면 예외를 발생시켜야 한다")
    void given_custom_type_with_null_name_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = null;
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CUSTOM 관계 유형을 선택한 경우 사용자 정의 관계명은 필수입니다.");
    }

    @Test
    @DisplayName("relationshipType이 CUSTOM이고 customRelationship이 빈 문자열이면 예외를 발생시켜야 한다")
    void given_custom_type_with_empty_name_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "";
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CUSTOM 관계 유형을 선택한 경우 사용자 정의 관계명은 필수입니다.");
    }

    @Test
    @DisplayName("relationshipType이 CUSTOM이고 customRelationship이 50자를 초과하면 예외를 발생시켜야 한다")
    void given_custom_type_with_too_long_name_when_create_command_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "a".repeat(51); // 51자
        String description = "부모 관계";

        // when & then
        assertThatThrownBy(() -> new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 정의 관계명은 50자 이내로 작성해주세요.");
    }
}
