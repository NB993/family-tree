package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberTest")
class FamilyMemberTest {

    @Test
    @DisplayName("newOwner 메서드로 OWNER 역할의 FamilyMember를 생성할 수 있다")
    void new_owner_creates_family_member_with_owner_role() {
        // given
        Long familyId = 1L;
        Long userId = 2L;
        String name = "홍길동";
        String profileUrl = "http://example.com/profile.jpg";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);

        // when
        FamilyMember familyMember = FamilyMember.newOwner(
            familyId, userId, name, profileUrl, birthday, null
        );

        // then
        assertThat(familyMember.getId()).isNull();
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        assertThat(familyMember.getRole()).isEqualTo(FamilyMemberRole.OWNER);
        assertThat(familyMember.getCreatedBy()).isNull();
        assertThat(familyMember.getCreatedAt()).isNull();
        assertThat(familyMember.getModifiedBy()).isNull();
        assertThat(familyMember.getModifiedAt()).isNull();
    }

    @Test
    @DisplayName("newMember 메서드로 MEMBER 역할의 FamilyMember를 생성할 수 있다")
    void new_member_creates_family_member_with_member_role() {
        // given
        Long familyId = 1L;
        Long userId = 2L;
        String name = "홍길동";
        String profileUrl = "http://example.com/profile.jpg";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);

        // when
        FamilyMember familyMember = FamilyMember.newMember(
            familyId, userId, name, profileUrl, birthday, null
        );

        // then
        assertThat(familyMember.getId()).isNull();
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        assertThat(familyMember.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        assertThat(familyMember.getCreatedBy()).isNull();
        assertThat(familyMember.getCreatedAt()).isNull();
        assertThat(familyMember.getModifiedBy()).isNull();
        assertThat(familyMember.getModifiedAt()).isNull();
    }

    @Test
    @DisplayName("existingMember 메서드로 기존 ID가 있는 FamilyMember를 생성할 수 있다")
    void existing_member_creates_family_member_with_id() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long userId = 3L;
        String name = "홍길동";
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.FATHER;
        String profileUrl = "http://example.com/profile.jpg";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        Long createdBy = 4L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 5L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when
        FamilyMember familyMember = FamilyMember.withId(
            id, familyId, userId, name, relationshipType, null, profileUrl, birthday,
            null, status, FamilyMemberRole.MEMBER, createdBy, createdAt, modifiedBy, modifiedAt
        );

        // then
        assertThat(familyMember.getId()).isEqualTo(id);
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getStatus()).isEqualTo(status);
        assertThat(familyMember.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        assertThat(familyMember.getCreatedBy()).isEqualTo(createdBy);
        assertThat(familyMember.getCreatedAt()).isEqualTo(createdAt);
        assertThat(familyMember.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(familyMember.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("withRole 메서드로 역할이 지정된 신규 FamilyMember를 생성할 수 있다")
    void with_role_creates_new_family_member_with_specified_role() {
        // given
        Long familyId = 2L;
        Long userId = 3L;
        String name = "홍길동";
        String profileUrl = "http://example.com/profile.jpg";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        FamilyMemberRole role = FamilyMemberRole.ADMIN;

        // when
        FamilyMember familyMember = FamilyMember.withRole(
            familyId, userId, name, profileUrl, birthday, null, role
        );

        // then
        assertThat(familyMember.getId()).isNull(); // 신규 생성이므로 ID는 null
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE); // 기본값
        assertThat(familyMember.getRole()).isEqualTo(role);
        assertThat(familyMember.getCreatedBy()).isNull(); // 신규 생성이므로 audit 필드는 null
        assertThat(familyMember.getCreatedAt()).isNull();
        assertThat(familyMember.getModifiedBy()).isNull();
        assertThat(familyMember.getModifiedAt()).isNull();
    }

    @Test
    @DisplayName("updateRole 메서드로 FamilyMember의 역할을 변경할 수 있다")
    void update_role_changes_family_member_role() {
        // given
        FamilyMember member = FamilyMember.withId(
            1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
        );
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when
        FamilyMember updatedMember = member.updateRole(newRole);

        // then
        assertThat(updatedMember.getRole()).isEqualTo(newRole);
        // 다른 필드는 변경되지 않아야 함
        assertThat(updatedMember.getId()).isEqualTo(member.getId());
        assertThat(updatedMember.getFamilyId()).isEqualTo(member.getFamilyId());
        assertThat(updatedMember.getUserId()).isEqualTo(member.getUserId());
        assertThat(updatedMember.getName()).isEqualTo(member.getName());
        assertThat(updatedMember.getProfileUrl()).isEqualTo(member.getProfileUrl());
        assertThat(updatedMember.getBirthday()).isEqualTo(member.getBirthday());
        assertThat(updatedMember.getStatus()).isEqualTo(member.getStatus());
        assertThat(updatedMember.getCreatedBy()).isEqualTo(member.getCreatedBy());
        assertThat(updatedMember.getCreatedAt()).isEqualTo(member.getCreatedAt());
        assertThat(updatedMember.getModifiedBy()).isEqualTo(member.getModifiedBy());
        assertThat(updatedMember.getModifiedAt()).isEqualTo(member.getModifiedAt());
    }

    @Test
    @DisplayName("OWNER 역할은 변경할 수 없다")
    void cannot_change_owner_role() {
        // given
        FamilyMember owner = FamilyMember.withId(
            1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
        );
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // when & then
        assertThatThrownBy(() -> {
            owner.updateRole(newRole);
        }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Cannot change role of the Family OWNER");
    }

    @Test
    @DisplayName("updateStatus 메서드로 FamilyMember의 상태를 변경할 수 있다")
    void update_status_changes_family_member_status() {
        // given
        FamilyMember member = FamilyMember.withId(
            1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
        );
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;

        // when
        FamilyMember updatedMember = member.updateStatus(newStatus);

        // then
        assertThat(updatedMember.getStatus()).isEqualTo(newStatus);
        // 다른 필드는 변경되지 않아야 함
        assertThat(updatedMember.getId()).isEqualTo(member.getId());
        assertThat(updatedMember.getFamilyId()).isEqualTo(member.getFamilyId());
        assertThat(updatedMember.getUserId()).isEqualTo(member.getUserId());
        assertThat(updatedMember.getName()).isEqualTo(member.getName());
        assertThat(updatedMember.getProfileUrl()).isEqualTo(member.getProfileUrl());
        assertThat(updatedMember.getBirthday()).isEqualTo(member.getBirthday());
        assertThat(updatedMember.getRole()).isEqualTo(member.getRole());
        assertThat(updatedMember.getCreatedBy()).isEqualTo(member.getCreatedBy());
        assertThat(updatedMember.getCreatedAt()).isEqualTo(member.getCreatedAt());
        assertThat(updatedMember.getModifiedBy()).isEqualTo(member.getModifiedBy());
        assertThat(updatedMember.getModifiedAt()).isEqualTo(member.getModifiedAt());
    }

    @Test
    @DisplayName("OWNER 상태는 변경할 수 없다")
    void cannot_change_owner_status() {
        // given
        FamilyMember owner = FamilyMember.withId(
            1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
        );
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;

        // when & then
        assertThatThrownBy(() -> {
            owner.updateStatus(newStatus);
        }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Cannot change status of the Family OWNER");
    }

    @Test
    @DisplayName("hasRoleAtLeast 메서드로 특정 역할 이상의 권한을 가지고 있는지 확인할 수 있다")
    void has_role_at_least_checks_if_member_has_required_role() {
        // given
        FamilyMember owner = FamilyMember.newOwner(1L, 2L, "Owner", "", null, null);
        FamilyMember admin = FamilyMember.withId(3L, 1L, 4L, "Admin", null, null, null, null,
            null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN, null, null, null, null);
        FamilyMember member = FamilyMember.newMember(1L, 5L, "Member", "", null, null);

        // when & then
        assertThat(owner.hasRoleAtLeast(FamilyMemberRole.OWNER)).isTrue();
        assertThat(owner.hasRoleAtLeast(FamilyMemberRole.ADMIN)).isTrue();
        assertThat(owner.hasRoleAtLeast(FamilyMemberRole.MEMBER)).isTrue();

        assertThat(admin.hasRoleAtLeast(FamilyMemberRole.OWNER)).isFalse();
        assertThat(admin.hasRoleAtLeast(FamilyMemberRole.ADMIN)).isTrue();
        assertThat(admin.hasRoleAtLeast(FamilyMemberRole.MEMBER)).isTrue();

        assertThat(member.hasRoleAtLeast(FamilyMemberRole.OWNER)).isFalse();
        assertThat(member.hasRoleAtLeast(FamilyMemberRole.ADMIN)).isFalse();
        assertThat(member.hasRoleAtLeast(FamilyMemberRole.MEMBER)).isTrue();
    }

    @Test
    @DisplayName("isActive 메서드로 구성원이 활성 상태인지 확인할 수 있다")
    void is_active_checks_if_member_is_active() {
        // given
        FamilyMember activeMember = FamilyMember.withId(1L, 2L, 3L, "Active", null, null, null, null,
            null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, null, null, null, null);
        FamilyMember suspendedMember = FamilyMember.withId(4L, 2L, 5L, "Suspended", null, null, null, null,
            null, FamilyMemberStatus.SUSPENDED, FamilyMemberRole.MEMBER, null, null, null, null);
        FamilyMember bannedMember = FamilyMember.withId(6L, 2L, 7L, "Banned", null, null, null, null,
            null, FamilyMemberStatus.BANNED, FamilyMemberRole.MEMBER, null, null, null, null);

        // when & then
        assertThat(activeMember.isActive()).isTrue();
        assertThat(suspendedMember.isActive()).isFalse();
        assertThat(bannedMember.isActive()).isFalse();
    }

    @Nested
    @DisplayName("modifyInfo 메서드")
    class ModifyInfo {

        @Test
        @DisplayName("이름, 생일, 생일타입을 변경할 수 있다")
        void should_modify_name_birthday_and_birthday_type() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), BirthdayType.SOLAR,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when
            FamilyMember modifiedMember = member.modifyInfo(
                "김철수",
                LocalDateTime.of(1985, 3, 15, 0, 0),
                BirthdayType.LUNAR
            );

            // then
            assertThat(modifiedMember.getName()).isEqualTo("김철수");
            assertThat(modifiedMember.getBirthday()).isEqualTo(LocalDateTime.of(1985, 3, 15, 0, 0));
            assertThat(modifiedMember.getBirthdayType()).isEqualTo(BirthdayType.LUNAR);
            // 다른 필드는 변경되지 않아야 함
            assertThat(modifiedMember.getId()).isEqualTo(member.getId());
            assertThat(modifiedMember.getFamilyId()).isEqualTo(member.getFamilyId());
            assertThat(modifiedMember.getUserId()).isEqualTo(member.getUserId());
            assertThat(modifiedMember.getProfileUrl()).isEqualTo(member.getProfileUrl());
            assertThat(modifiedMember.getRelationshipType()).isEqualTo(member.getRelationshipType());
            assertThat(modifiedMember.getStatus()).isEqualTo(member.getStatus());
            assertThat(modifiedMember.getRole()).isEqualTo(member.getRole());
        }

        @Test
        @DisplayName("이름이 null이면 예외가 발생한다")
        void should_throw_exception_when_name_is_null() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), BirthdayType.SOLAR,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when & then
            assertThatThrownBy(() -> member.modifyInfo(null, LocalDateTime.of(1985, 3, 15, 0, 0), BirthdayType.SOLAR))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name");
        }

        @Test
        @DisplayName("이름이 빈 문자열이면 예외가 발생한다")
        void should_throw_exception_when_name_is_blank() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), BirthdayType.SOLAR,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when & then
            assertThatThrownBy(() -> member.modifyInfo("   ", LocalDateTime.of(1985, 3, 15, 0, 0), BirthdayType.SOLAR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
        }

        @Test
        @DisplayName("생일과 생일타입이 null이어도 정상 동작한다")
        void should_modify_with_null_birthday_and_birthday_type() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), BirthdayType.SOLAR,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when
            FamilyMember modifiedMember = member.modifyInfo("김철수", null, null);

            // then
            assertThat(modifiedMember.getName()).isEqualTo("김철수");
            assertThat(modifiedMember.getBirthday()).isNull();
            assertThat(modifiedMember.getBirthdayType()).isNull();
        }
    }

    @Nested
    @DisplayName("updateRelationship 메서드")
    class UpdateRelationship {

        @Test
        @DisplayName("관계 타입을 변경할 수 있다")
        void should_update_relationship_type() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), null,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when
            FamilyMember updatedMember = member.updateRelationship(
                FamilyMemberRelationshipType.FATHER, null
            );

            // then
            assertThat(updatedMember.getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.FATHER);
            assertThat(updatedMember.getCustomRelationship()).isNull();
            // 다른 필드는 변경되지 않아야 함
            assertThat(updatedMember.getId()).isEqualTo(member.getId());
            assertThat(updatedMember.getFamilyId()).isEqualTo(member.getFamilyId());
            assertThat(updatedMember.getName()).isEqualTo(member.getName());
            assertThat(updatedMember.getStatus()).isEqualTo(member.getStatus());
            assertThat(updatedMember.getRole()).isEqualTo(member.getRole());
        }

        @Test
        @DisplayName("CUSTOM 타입일 때 customRelationship을 설정할 수 있다")
        void should_set_custom_relationship_when_custom_type() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), null,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when
            FamilyMember updatedMember = member.updateRelationship(
                FamilyMemberRelationshipType.CUSTOM, "의붓아버지"
            );

            // then
            assertThat(updatedMember.getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.CUSTOM);
            assertThat(updatedMember.getCustomRelationship()).isEqualTo("의붓아버지");
        }

        @Test
        @DisplayName("relationshipType이 null이면 예외가 발생한다")
        void should_throw_exception_when_relationship_type_is_null() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), null,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when & then
            assertThatThrownBy(() -> member.updateRelationship(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("relationshipType must not be null");
        }

        @Test
        @DisplayName("CUSTOM 타입일 때 customRelationship이 없으면 예외가 발생한다")
        void should_throw_exception_when_custom_type_without_custom_relationship() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), null,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when & then
            assertThatThrownBy(() -> member.updateRelationship(FamilyMemberRelationshipType.CUSTOM, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CUSTOM 관계 타입 선택 시 customRelationship 필수");
        }

        @Test
        @DisplayName("CUSTOM 타입일 때 customRelationship이 빈 문자열이면 예외가 발생한다")
        void should_throw_exception_when_custom_type_with_blank_custom_relationship() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), null,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );

            // when & then
            assertThatThrownBy(() -> member.updateRelationship(FamilyMemberRelationshipType.CUSTOM, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CUSTOM 관계 타입 선택 시 customRelationship 필수");
        }

        @Test
        @DisplayName("customRelationship이 50자를 초과하면 예외가 발생한다")
        void should_throw_exception_when_custom_relationship_exceeds_50_chars() {
            // given
            FamilyMember member = FamilyMember.withId(
                1L, 2L, 3L, "홍길동", null, null, "http://example.com/profile.jpg",
                LocalDateTime.of(1990, 1, 1, 0, 0), null,
                FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
                4L, LocalDateTime.now().minusDays(1), 5L, LocalDateTime.now()
            );
            String longCustomRelationship = "가".repeat(51);

            // when & then
            assertThatThrownBy(() -> member.updateRelationship(FamilyMemberRelationshipType.CUSTOM, longCustomRelationship))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("customRelationship은 50자 이내");
        }
    }
}
