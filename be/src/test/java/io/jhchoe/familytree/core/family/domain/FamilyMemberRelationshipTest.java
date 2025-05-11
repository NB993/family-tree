package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyRelationship")
class FamilyMemberRelationshipTest {

    @Test
    @DisplayName("createRelationship 메서드는 유효한 입력을 받으면 FamilyRelationship 객체를 생성해야 한다")
    void given_valid_inputs_when_create_relationship_then_create_family_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";

        // when
        FamilyMemberRelationship result = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(result.getToMemberId()).isEqualTo(toMemberId);
        assertThat(result.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(result.getCustomRelationship()).isEqualTo(customRelationship);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getModifiedBy()).isNull();
        assertThat(result.getModifiedAt()).isNull();
    }

    @Test
    @DisplayName("createRelationship 메서드는 CUSTOM 타입일 때 customRelationship이 제공되면 객체를 생성해야 한다")
    void given_custom_type_with_name_when_create_relationship_then_create_family_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "사촌 동생";
        String description = "관계 설명";

        // when
        FamilyMemberRelationship result = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(result.getCustomRelationship()).isEqualTo(customRelationship);
    }

    @Test
    @DisplayName("createRelationship 메서드는 familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_create_relationship_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("createRelationship 메서드는 fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_create_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fromMemberId must not be null");
    }

    @Test
    @DisplayName("createRelationship 메서드는 toMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_to_member_id_when_create_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = null;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("toMemberId must not be null");
    }

    @Test
    @DisplayName("createRelationship 메서드는 fromMemberId와 toMemberId가 같으면 예외를 발생시켜야 한다")
    void given_same_member_ids_when_create_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 2L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("fromMemberId와 toMemberId는 서로 달라야 합니다");
    }

    @Test
    @DisplayName("createRelationship 메서드는 relationshipType이 null이면 예외를 발생시켜야 한다")
    void given_null_relationship_type_when_create_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = null;
        String customRelationship = null;
        String description = "관계 설명";

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("relationshipType must not be null");
    }

    @Test
    @DisplayName("createRelationship 메서드는 relationshipType이 CUSTOM이고 customRelationship이 null이면 예외를 발생시켜야 한다")
    void given_custom_type_without_name_when_create_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = null;
        String description = "관계 설명";

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CUSTOM 관계 타입을 선택한 경우 customRelationship은 필수입니다");
    }

    @Test
    @DisplayName("updateRelationship 메서드는 관계 타입과 설명을 업데이트해야 한다")
    void given_relationship_when_update_relationship_then_return_updated_relationship() {
        // given
        FamilyMemberRelationship original = FamilyMemberRelationship.newRelationship(
            1L,
            2L,
            3L,
            FamilyMemberRelationshipType.PARENT,
            null,
            "원래 설명"
        );

        FamilyMemberRelationshipType newType = FamilyMemberRelationshipType.GRANDPARENT;
        String newCustomRelationship = null;
        String newDescription = "새로운 설명";

        // when
        FamilyMemberRelationship updated = original.update(
            newType,
            newCustomRelationship,
            newDescription
        );

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getFamilyId()).isEqualTo(original.getFamilyId());
        assertThat(updated.getFromMemberId()).isEqualTo(original.getFromMemberId());
        assertThat(updated.getToMemberId()).isEqualTo(original.getToMemberId());
        assertThat(updated.getRelationshipType()).isEqualTo(newType);
        assertThat(updated.getCustomRelationship()).isEqualTo(newCustomRelationship);
        assertThat(updated.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("updateRelationship 메서드는 CUSTOM 타입으로 변경하고 customRelationship을 제공하면 업데이트해야 한다")
    void given_relationship_when_update_to_custom_type_then_return_updated_relationship() {
        // given
        FamilyMemberRelationship original = FamilyMemberRelationship.newRelationship(
            1L,
            2L,
            3L,
            FamilyMemberRelationshipType.PARENT,
            null,
            "원래 설명"
        );

        FamilyMemberRelationshipType newType = FamilyMemberRelationshipType.CUSTOM;
        String newCustomRelationship = "할머니";
        String newDescription = "새로운 설명";

        // when
        FamilyMemberRelationship updated = original.update(
            newType,
            newCustomRelationship,
            newDescription
        );

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getRelationshipType()).isEqualTo(newType);
        assertThat(updated.getCustomRelationship()).isEqualTo(newCustomRelationship);
        assertThat(updated.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("updateRelationship 메서드는 relationshipType이 null이면 예외를 발생시켜야 한다")
    void given_null_relationship_type_when_update_relationship_then_throw_exception() {
        // given
        FamilyMemberRelationship original = FamilyMemberRelationship.newRelationship(
            1L,
            2L,
            3L,
            FamilyMemberRelationshipType.PARENT,
            null,
            "원래 설명"
        );

        FamilyMemberRelationshipType newType = null;
        String newCustomRelationship = null;
        String newDescription = "새로운 설명";

        // when & then
        assertThatThrownBy(() -> original.update(
            newType,
            newCustomRelationship,
            newDescription
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("relationshipType must not be null");
    }

    @Test
    @DisplayName("updateRelationship 메서드는 CUSTOM 타입으로 변경하고 customRelationship이 null이면 예외를 발생시켜야 한다")
    void given_custom_type_without_name_when_update_relationship_then_throw_exception() {
        // given
        FamilyMemberRelationship original = FamilyMemberRelationship.newRelationship(
            1L,
            2L,
            3L,
            FamilyMemberRelationshipType.PARENT,
            null,
            "원래 설명"
        );

        FamilyMemberRelationshipType newType = FamilyMemberRelationshipType.CUSTOM;
        String newCustomRelationship = null;
        String newDescription = "새로운 설명";

        // when & then
        assertThatThrownBy(() -> original.update(
            newType,
            newCustomRelationship,
            newDescription
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CUSTOM 관계 타입을 선택한 경우 customRelationship은 필수입니다");
    }

    @Test
    @DisplayName("withId 메서드는 유효한 입력을 받으면 ID가 있는 FamilyRelationship 객체를 생성해야 한다")
    void given_valid_inputs_when_with_id_then_create_family_relationship_with_id() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";
        Long createdBy = 5L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 6L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when
        FamilyMemberRelationship result = FamilyMemberRelationship.withId(
            id,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            createdBy,
            createdAt,
            modifiedBy,
            modifiedAt
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(result.getToMemberId()).isEqualTo(toMemberId);
        assertThat(result.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(result.getCustomRelationship()).isEqualTo(customRelationship);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("withId 메서드는 id가 null이면 예외를 발생시켜야 한다")
    void given_null_id_when_with_id_then_throw_exception() {
        // given
        Long id = null;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "관계 설명";
        Long createdBy = 5L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 6L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> FamilyMemberRelationship.withId(
            id,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            createdBy,
            createdAt,
            modifiedBy,
            modifiedAt
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("id must not be null");
    }
}
