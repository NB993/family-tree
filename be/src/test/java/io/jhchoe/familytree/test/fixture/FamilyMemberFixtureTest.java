package io.jhchoe.familytree.test.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FamilyMemberFixture")
class FamilyMemberFixtureTest {

    @Nested
    @DisplayName("newMember 메서드")
    class NewMemberMethod {

        @Test
        @DisplayName("기본 MEMBER 역할로 생성된다")
        void creates_member_with_default_values() {
            // when
            FamilyMember member = FamilyMemberFixture.newMember();

            // then
            assertThat(member.getId()).isNull();
            assertThat(member.getFamilyId()).isEqualTo(1L);
            assertThat(member.getUserId()).isEqualTo(1L);
            assertThat(member.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
            assertThat(member.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        }

        @Test
        @DisplayName("지정된 familyId와 userId로 생성된다")
        void creates_member_with_specified_ids() {
            // when
            FamilyMember member = FamilyMemberFixture.newMember(10L, 20L);

            // then
            assertThat(member.getFamilyId()).isEqualTo(10L);
            assertThat(member.getUserId()).isEqualTo(20L);
            assertThat(member.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        }

        @Test
        @DisplayName("지정된 familyId, userId, name으로 생성된다")
        void creates_member_with_specified_ids_and_name() {
            // when
            FamilyMember member = FamilyMemberFixture.newMember(10L, 20L, "커스텀이름");

            // then
            assertThat(member.getFamilyId()).isEqualTo(10L);
            assertThat(member.getUserId()).isEqualTo(20L);
            assertThat(member.getName()).isEqualTo("커스텀이름");
        }
    }

    @Nested
    @DisplayName("newOwner 메서드")
    class NewOwnerMethod {

        @Test
        @DisplayName("기본 OWNER 역할로 생성된다")
        void creates_owner_with_default_values() {
            // when
            FamilyMember owner = FamilyMemberFixture.newOwner();

            // then
            assertThat(owner.getId()).isNull();
            assertThat(owner.getRole()).isEqualTo(FamilyMemberRole.OWNER);
            assertThat(owner.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        }

        @Test
        @DisplayName("지정된 familyId와 userId로 생성된다")
        void creates_owner_with_specified_ids() {
            // when
            FamilyMember owner = FamilyMemberFixture.newOwner(10L, 20L);

            // then
            assertThat(owner.getFamilyId()).isEqualTo(10L);
            assertThat(owner.getUserId()).isEqualTo(20L);
            assertThat(owner.getRole()).isEqualTo(FamilyMemberRole.OWNER);
        }
    }

    @Nested
    @DisplayName("newAdmin 메서드")
    class NewAdminMethod {

        @Test
        @DisplayName("기본 ADMIN 역할로 생성된다")
        void creates_admin_with_default_values() {
            // when
            FamilyMember admin = FamilyMemberFixture.newAdmin();

            // then
            assertThat(admin.getId()).isNull();
            assertThat(admin.getRole()).isEqualTo(FamilyMemberRole.ADMIN);
            assertThat(admin.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        }

        @Test
        @DisplayName("지정된 familyId와 userId로 생성된다")
        void creates_admin_with_specified_ids() {
            // when
            FamilyMember admin = FamilyMemberFixture.newAdmin(10L, 20L);

            // then
            assertThat(admin.getFamilyId()).isEqualTo(10L);
            assertThat(admin.getUserId()).isEqualTo(20L);
            assertThat(admin.getRole()).isEqualTo(FamilyMemberRole.ADMIN);
        }
    }

    @Nested
    @DisplayName("withId 메서드")
    class WithIdMethod {

        @Test
        @DisplayName("지정된 ID로 MEMBER 역할의 FamilyMember를 생성한다")
        void creates_member_with_specified_id() {
            // when
            FamilyMember member = FamilyMemberFixture.withId(100L);

            // then
            assertThat(member.getId()).isEqualTo(100L);
            assertThat(member.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
            assertThat(member.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        }

        @Test
        @DisplayName("지정된 ID, familyId, userId로 FamilyMember를 생성한다")
        void creates_member_with_id_family_user() {
            // when
            FamilyMember member = FamilyMemberFixture.withId(100L, 10L, 20L);

            // then
            assertThat(member.getId()).isEqualTo(100L);
            assertThat(member.getFamilyId()).isEqualTo(10L);
            assertThat(member.getUserId()).isEqualTo(20L);
        }
    }

    @Nested
    @DisplayName("withIdAndRole 메서드")
    class WithIdAndRoleMethod {

        @Test
        @DisplayName("지정된 ID와 역할로 FamilyMember를 생성한다")
        void creates_member_with_id_and_role() {
            // when
            FamilyMember admin = FamilyMemberFixture.withIdAndRole(100L, FamilyMemberRole.ADMIN);

            // then
            assertThat(admin.getId()).isEqualTo(100L);
            assertThat(admin.getRole()).isEqualTo(FamilyMemberRole.ADMIN);
        }

        @Test
        @DisplayName("지정된 ID, familyId, userId, 역할로 FamilyMember를 생성한다")
        void creates_member_with_id_family_user_role() {
            // when
            FamilyMember owner = FamilyMemberFixture.withIdAndRole(100L, 10L, 20L, FamilyMemberRole.OWNER);

            // then
            assertThat(owner.getId()).isEqualTo(100L);
            assertThat(owner.getFamilyId()).isEqualTo(10L);
            assertThat(owner.getUserId()).isEqualTo(20L);
            assertThat(owner.getRole()).isEqualTo(FamilyMemberRole.OWNER);
        }
    }

    @Nested
    @DisplayName("withIdAndStatus 메서드")
    class WithIdAndStatusMethod {

        @Test
        @DisplayName("지정된 ID와 상태로 FamilyMember를 생성한다")
        void creates_member_with_id_and_status() {
            // when
            FamilyMember suspended = FamilyMemberFixture.withIdAndStatus(100L, FamilyMemberStatus.SUSPENDED);

            // then
            assertThat(suspended.getId()).isEqualTo(100L);
            assertThat(suspended.getStatus()).isEqualTo(FamilyMemberStatus.SUSPENDED);
        }
    }

    @Nested
    @DisplayName("withIdRoleAndStatus 메서드")
    class WithIdRoleAndStatusMethod {

        @Test
        @DisplayName("지정된 ID, 역할, 상태로 FamilyMember를 생성한다")
        void creates_member_with_id_role_status() {
            // when
            FamilyMember member = FamilyMemberFixture.withIdRoleAndStatus(100L, FamilyMemberRole.ADMIN, FamilyMemberStatus.BANNED);

            // then
            assertThat(member.getId()).isEqualTo(100L);
            assertThat(member.getRole()).isEqualTo(FamilyMemberRole.ADMIN);
            assertThat(member.getStatus()).isEqualTo(FamilyMemberStatus.BANNED);
        }
    }

    @Nested
    @DisplayName("withIdAndName 메서드")
    class WithIdAndNameMethod {

        @Test
        @DisplayName("지정된 ID와 이름으로 FamilyMember를 생성한다")
        void creates_member_with_id_and_name() {
            // when
            FamilyMember member = FamilyMemberFixture.withIdAndName(100L, "커스텀이름");

            // then
            assertThat(member.getId()).isEqualTo(100L);
            assertThat(member.getName()).isEqualTo("커스텀이름");
        }
    }
}
