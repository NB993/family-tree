package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FamilyMemberWithRelationshipResponse(가족 구성원과 관계 응답 DTO)")
class FamilyMemberWithRelationshipResponseTest {

    @Test
    @DisplayName("생성자에서 null member 전달시 예외가 발생한다")
    void should_throw_exception_when_null_member() {
        // when & then
        assertThatThrownBy(() -> new FamilyMemberWithRelationshipResponse(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("member must not be null");
    }

    @Test
    @DisplayName("관계가 있는 구성원의 정보를 올바르게 반환한다")
    void should_return_correct_info_when_relationship_exists() {
        // given
        FamilyMember member = createFamilyMemberWithRelationship(FamilyMemberRelationshipType.FATHER, null);

        // when
        FamilyMemberWithRelationshipResponse response = new FamilyMemberWithRelationshipResponse(member);

        // then
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getMemberName()).isEqualTo("김아버지");
        assertThat(response.getMemberAge()).isNotNull();
        assertThat(response.getMemberBirthday()).isEqualTo(member.getBirthday());
        assertThat(response.getMemberPhoneNumber()).isNull();
        assertThat(response.getMemberProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");

        assertThat(response.hasRelationship()).isTrue();
        assertThat(response.getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.FATHER);
        assertThat(response.getRelationshipDisplayName()).isEqualTo("아버지");
        assertThat(response.getCustomRelationshipName()).isNull();
        assertThat(response.isRelationshipSetupRequired()).isFalse();
        assertThat(response.getRelationshipGuideMessage()).isEqualTo("아버지");
    }

    @Test
    @DisplayName("관계가 없는 구성원의 정보를 올바르게 반환한다")
    void should_return_correct_info_when_no_relationship() {
        // given
        FamilyMember member = createFamilyMemberWithRelationship(null, null);

        // when
        FamilyMemberWithRelationshipResponse response = new FamilyMemberWithRelationshipResponse(member);

        // then
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getMemberName()).isEqualTo("김아버지");

        assertThat(response.hasRelationship()).isFalse();
        assertThat(response.getRelationshipType()).isNull();
        assertThat(response.getRelationshipDisplayName()).isNull();
        assertThat(response.getCustomRelationshipName()).isNull();
        assertThat(response.isRelationshipSetupRequired()).isTrue();
        assertThat(response.getRelationshipGuideMessage()).isEqualTo("관계를 설정해주세요");
    }

    @Test
    @DisplayName("CUSTOM 관계 타입의 정보를 올바르게 반환한다")
    void should_return_correct_info_when_custom_relationship() {
        // given
        FamilyMember member = createFamilyMemberWithRelationship(FamilyMemberRelationshipType.CUSTOM, "의붓아버지");

        // when
        FamilyMemberWithRelationshipResponse response = new FamilyMemberWithRelationshipResponse(member);

        // then
        assertThat(response.hasRelationship()).isTrue();
        assertThat(response.getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.CUSTOM);
        assertThat(response.getRelationshipDisplayName()).isEqualTo("의붓아버지");
        assertThat(response.getCustomRelationshipName()).isEqualTo("의붓아버지");
        assertThat(response.getRelationshipGuideMessage()).isEqualTo("의붓아버지");
    }

    @Test
    @DisplayName("연락처가 없는 경우 적절한 메시지를 반환한다")
    void should_return_appropriate_message_when_no_phone_number() {
        // given
        FamilyMember member = createFamilyMemberWithRelationship(null, null);

        // when
        FamilyMemberWithRelationshipResponse response = new FamilyMemberWithRelationshipResponse(member);

        // then
        assertThat(response.getPhoneNumberDisplay()).isEqualTo("연락처 없음");
    }

    @Test
    @DisplayName("toString 메서드가 올바른 정보를 반환한다")
    void should_return_correct_string_representation() {
        // given
        FamilyMember member = createFamilyMemberWithRelationship(FamilyMemberRelationshipType.FATHER, null);

        // when
        FamilyMemberWithRelationshipResponse response = new FamilyMemberWithRelationshipResponse(member);

        // then
        String expected = "FamilyMemberWithRelationshipResponse{memberId=1, memberName='김아버지', relationshipDisplayName='아버지', hasRelationship=true}";
        assertThat(response.toString()).isEqualTo(expected);
    }

    // Helper methods

    private FamilyMember createFamilyMemberWithRelationship(FamilyMemberRelationshipType relationshipType, String customRelationship) {
        return FamilyMember.withId(
            1L, 1L, 1L, "김아버지", relationshipType, customRelationship, "https://example.com/profile.jpg",
            LocalDateTime.of(1970, 1, 1, 0, 0), null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
}
