package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.MemberStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberEntity")
class FamilyMemberEntityTest {

    @Test
    @DisplayName("from 메서드는 유효한 FamilyMember 객체를 입력받아 FamilyMemberEntity 객체를 정상적으로 생성해야 한다.")
    void given_valid_family_member_when_from_then_return_family_member_entity() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        Long userId = 202L;
        String name = "Test Name";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.now();
        String nationality = "Korean";
        MemberStatus status = MemberStatus.ACTIVE;
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        FamilyMember familyMember = FamilyMember.existingMember(
            id, familyId, userId, name, profileUrl, birthday,
            nationality, status, createdBy, createdAt, modifiedBy, modifiedAt
        );

        // when
        FamilyMemberEntity result = FamilyMemberEntity.from(familyMember);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(result.getBirthday()).isEqualTo(birthday);
        assertThat(result.getNationality()).isEqualTo(nationality);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("from 메서드는 null FamilyMember를 입력받는 경우 NullPointerException을 발생시켜야 한다.")
    void given_null_family_member_when_from_then_throw_exception() {
        // given
        FamilyMember familyMember = null;

        // when & then
        assertThatThrownBy(() -> FamilyMemberEntity.from(familyMember))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyMember must not be null");
    }
}
