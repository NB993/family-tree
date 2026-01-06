package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberJpaEntityTest")
class FamilyMemberEntityTest {

    @Test
    @DisplayName("from 메서드는 유효한 FamilyMember 객체를 입력받아 FamilyMemberJpaEntity 객체를 정상적으로 생성해야 한다")
    void given_valid_family_member_when_from_then_return_family_member_entity() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        Long userId = 202L;
        String name = "Test Name";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.now();
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        FamilyMember familyMember = FamilyMember.withId(
            id, familyId, userId, name, null, null, profileUrl, birthday,
            null, status, FamilyMemberRole.MEMBER, createdBy, createdAt, modifiedBy, modifiedAt
        );

        // when
        FamilyMemberJpaEntity result = FamilyMemberJpaEntity.from(familyMember);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(result.getBirthday()).isEqualTo(birthday);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("from 메서드는 null FamilyMember를 입력받는 경우 NullPointerException을 발생시켜야 한다")
    void given_null_family_member_when_from_then_throw_exception() {
        // given
        FamilyMember familyMember = null;

        // when & then
        assertThatThrownBy(() -> FamilyMemberJpaEntity.from(familyMember))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyMember must not be null");
    }
    
    @Test
    @DisplayName("from 메서드는 역할이 지정된 FamilyMember 객체를 올바르게 변환해야 한다")
    void given_family_member_with_role_when_from_then_return_entity_with_role() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        Long userId = 202L;
        String name = "Admin User";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.now();
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        FamilyMemberRole role = FamilyMemberRole.ADMIN;
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        FamilyMember familyMember = FamilyMember.withId(
            id, familyId, userId, name, null, null, profileUrl, birthday,
            null, status, role, createdBy, createdAt, modifiedBy, modifiedAt
        );

        // when
        FamilyMemberJpaEntity result = FamilyMemberJpaEntity.from(familyMember);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(result.getBirthday()).isEqualTo(birthday);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("toFamilyMember 메서드는 JpaEntity를 도메인 객체로 올바르게 변환해야 한다")
    void given_jpa_entity_when_to_family_member_then_return_domain_object() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        Long userId = 202L;
        String name = "Test User";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.now();
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        FamilyMemberRole role = FamilyMemberRole.OWNER;
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        FamilyMember familyMember = FamilyMember.withId(id, familyId, userId, name, null, null, profileUrl, birthday,
            null, status, role, createdBy, createdAt, modifiedBy, modifiedAt);
        FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(familyMember);

        // when
        FamilyMember result = entity.toFamilyMember();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(result.getBirthday()).isEqualTo(birthday);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }
}
