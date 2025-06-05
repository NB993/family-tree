package io.jhchoe.familytree.core.family.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FamilyMemberRelationshipType 열거형 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FamilyMemberRelationshipTypeTest")
class FamilyMemberRelationshipTypeTest {

    @Test
    @DisplayName("직계 가족 관계 타입이 올바른 displayName을 가집니다")
    void direct_family_types_have_correct_display_names() {
        // given & when & then
        assertThat(FamilyMemberRelationshipType.FATHER.getDisplayName()).isEqualTo("아버지");
        assertThat(FamilyMemberRelationshipType.MOTHER.getDisplayName()).isEqualTo("어머니");
        assertThat(FamilyMemberRelationshipType.SON.getDisplayName()).isEqualTo("아들");
        assertThat(FamilyMemberRelationshipType.DAUGHTER.getDisplayName()).isEqualTo("딸");
    }

    @Test
    @DisplayName("조부모/손자 관계 타입이 올바른 displayName을 가집니다")
    void grandparent_grandchild_types_have_correct_display_names() {
        // given & when & then
        assertThat(FamilyMemberRelationshipType.GRANDFATHER.getDisplayName()).isEqualTo("할아버지");
        assertThat(FamilyMemberRelationshipType.GRANDMOTHER.getDisplayName()).isEqualTo("할머니");
        assertThat(FamilyMemberRelationshipType.GRANDSON.getDisplayName()).isEqualTo("손자");
        assertThat(FamilyMemberRelationshipType.GRANDDAUGHTER.getDisplayName()).isEqualTo("손녀");
    }

    @Test
    @DisplayName("형제자매 관계 타입이 올바른 displayName을 가집니다")
    void sibling_types_have_correct_display_names() {
        // given & when & then
        assertThat(FamilyMemberRelationshipType.ELDER_BROTHER.getDisplayName()).isEqualTo("형");
        assertThat(FamilyMemberRelationshipType.ELDER_SISTER.getDisplayName()).isEqualTo("누나/언니");
        assertThat(FamilyMemberRelationshipType.YOUNGER_BROTHER.getDisplayName()).isEqualTo("남동생");
        assertThat(FamilyMemberRelationshipType.YOUNGER_SISTER.getDisplayName()).isEqualTo("여동생");
    }

    @Test
    @DisplayName("배우자 관계 타입이 올바른 displayName을 가집니다")
    void spouse_types_have_correct_display_names() {
        // given & when & then
        assertThat(FamilyMemberRelationshipType.HUSBAND.getDisplayName()).isEqualTo("남편");
        assertThat(FamilyMemberRelationshipType.WIFE.getDisplayName()).isEqualTo("아내");
    }

    @Test
    @DisplayName("친척 관계 타입이 올바른 displayName을 가집니다")
    void relative_types_have_correct_display_names() {
        // given & when & then
        assertThat(FamilyMemberRelationshipType.UNCLE.getDisplayName()).isEqualTo("삼촌/외삼촌");
        assertThat(FamilyMemberRelationshipType.AUNT.getDisplayName()).isEqualTo("고모/이모");
        assertThat(FamilyMemberRelationshipType.NEPHEW.getDisplayName()).isEqualTo("조카");
        assertThat(FamilyMemberRelationshipType.NIECE.getDisplayName()).isEqualTo("조카딸");
        assertThat(FamilyMemberRelationshipType.COUSIN.getDisplayName()).isEqualTo("사촌");
    }

    @Test
    @DisplayName("사용자 정의 관계 타입이 올바른 displayName을 가집니다")
    void custom_type_has_correct_display_name() {
        // given & when & then
        assertThat(FamilyMemberRelationshipType.CUSTOM.getDisplayName()).isEqualTo("직접 입력");
    }

    @Test
    @DisplayName("모든 enum 값이 정의되어 있습니다")
    void all_enum_values_are_defined() {
        // given
        FamilyMemberRelationshipType[] values = FamilyMemberRelationshipType.values();
        
        // when & then
        assertThat(values).hasSize(20); // 총 20개의 관계 타입이 정의되어야 함
        
        // 직계 가족 (4개)
        assertThat(values).contains(
            FamilyMemberRelationshipType.FATHER,
            FamilyMemberRelationshipType.MOTHER,
            FamilyMemberRelationshipType.SON,
            FamilyMemberRelationshipType.DAUGHTER
        );
        
        // 조부모/손자 (4개)
        assertThat(values).contains(
            FamilyMemberRelationshipType.GRANDFATHER,
            FamilyMemberRelationshipType.GRANDMOTHER,
            FamilyMemberRelationshipType.GRANDSON,
            FamilyMemberRelationshipType.GRANDDAUGHTER
        );
        
        // 형제자매 (4개)
        assertThat(values).contains(
            FamilyMemberRelationshipType.ELDER_BROTHER,
            FamilyMemberRelationshipType.ELDER_SISTER,
            FamilyMemberRelationshipType.YOUNGER_BROTHER,
            FamilyMemberRelationshipType.YOUNGER_SISTER
        );
        
        // 배우자 (2개)
        assertThat(values).contains(
            FamilyMemberRelationshipType.HUSBAND,
            FamilyMemberRelationshipType.WIFE
        );
        
        // 친척 (5개)
        assertThat(values).contains(
            FamilyMemberRelationshipType.UNCLE,
            FamilyMemberRelationshipType.AUNT,
            FamilyMemberRelationshipType.NEPHEW,
            FamilyMemberRelationshipType.NIECE,
            FamilyMemberRelationshipType.COUSIN
        );
        
        // 사용자 정의 (1개)
        assertThat(values).contains(FamilyMemberRelationshipType.CUSTOM);
    }
}
