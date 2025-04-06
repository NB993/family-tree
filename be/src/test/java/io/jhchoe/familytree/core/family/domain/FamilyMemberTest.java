package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMember")
class FamilyMemberTest {

    @Test
    @DisplayName("newMember 메서드는 familyId와 userId가 유효하면 FamilyMember 객체를 생성해야 한다.")
    void given_valid_inputs_when_new_member_then_create_family_member() {
        // given
        Long familyId = 1L;
        Long userId = 2L;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";

        // when
        FamilyMember result = FamilyMember.newMember(familyId, userId, name, profileUrl, birthday, nationality);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(result.getBirthday()).isEqualTo(birthday);
        assertThat(result.getNationality()).isEqualTo(nationality);
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("newMember 메서드는 familyId가 null이면 예외를 발생시켜야 한다.")
    void given_null_family_id_when_new_member_then_throw_exception() {
        // given
        Long familyId = null;
        Long userId = 2L;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";

        // when & then
        assertThatThrownBy(() -> FamilyMember.newMember(familyId, userId, name, profileUrl, birthday, nationality))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("newMember 메서드는 userId가 null이면 예외를 발생시켜야 한다.")
    void given_null_user_id_when_new_member_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long userId = null;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";

        // when & then
        assertThatThrownBy(() -> FamilyMember.newMember(familyId, userId, name, profileUrl, birthday, nationality))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }

    @Test
    @DisplayName("newMember 메서드는 optional 필드(name, profileUrl, birthday, nationality)가 null이어도 FamilyMember 객체를 생성해야 한다.")
    void given_null_optional_fields_when_new_member_then_create_family_member() {
        // given
        Long familyId = 1L;
        Long userId = 2L;

        // when
        FamilyMember result = FamilyMember.newMember(familyId, userId, null, null, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isNull();
        assertThat(result.getProfileUrl()).isNull();
        assertThat(result.getBirthday()).isNull();
        assertThat(result.getNationality()).isNull();
    }

    @Test
    @DisplayName("existingMember 메서드는 유효한 모든 필드로 FamilyMember 객체를 생성해야 한다.")
    void given_valid_inputs_when_existing_member_then_create_family_member() {
        // given
        Long id = 1L;
        Long familyId = 1L;
        Long userId = 2L;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";
        MemberStatus status = MemberStatus.ACTIVE;
        Long createdBy = 3L;
        LocalDateTime createdAt = LocalDateTime.now();
        Long modifiedBy = 4L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when
        FamilyMember result = FamilyMember.existingMember(id, familyId, userId, name, profileUrl, birthday, nationality,
            status, createdBy, createdAt, modifiedBy, modifiedAt);

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
    @DisplayName("existingMember 메서드는 id가 null이면 예외를 발생시켜야 한다.")
    void given_null_id_when_existing_member_then_throw_exception() {
        // given
        Long id = null;
        Long familyId = 1L;
        Long userId = 2L;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";
        MemberStatus status = MemberStatus.ACTIVE;
        Long createdBy = null;
        LocalDateTime createdAt = null;
        Long modifiedBy = null;
        LocalDateTime modifiedAt = null;

        // when & then
        assertThatThrownBy(
            () -> FamilyMember.existingMember(id, familyId, userId, name, profileUrl, birthday, nationality, status,
                createdBy, createdAt, modifiedBy, modifiedAt))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("existingMember 메서드는 familyId가 null이면 예외를 발생시켜야 한다.")
    void given_null_familyId_when_existing_member_then_throw_exception() {
        // given
        Long id = 1L;
        Long familyId = null;
        Long userId = 2L;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";
        MemberStatus status = MemberStatus.ACTIVE;
        Long createdBy = null;
        LocalDateTime createdAt = null;
        Long modifiedBy = null;
        LocalDateTime modifiedAt = null;

        // when & then
        assertThatThrownBy(
            () -> FamilyMember.existingMember(id, familyId, userId, name, profileUrl, birthday, nationality, status,
                createdBy, createdAt, modifiedBy, modifiedAt))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("existingMember 메서드는 userId가 null이면 예외를 발생시켜야 한다.")
    void given_null_userId_when_existing_member_then_throw_exception() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long userId = null;
        String name = "John Doe";
        String profileUrl = "http://example.com/profile";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "US";
        MemberStatus status = MemberStatus.ACTIVE;
        Long createdBy = null;
        LocalDateTime createdAt = null;
        Long modifiedBy = null;
        LocalDateTime modifiedAt = null;

        // when & then
        assertThatThrownBy(
            () -> FamilyMember.existingMember(id, familyId, userId, name, profileUrl, birthday, nationality, status,
                createdBy, createdAt, modifiedBy, modifiedAt))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }
}
