package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberTest")
class FamilyMemberTest {
    
    @Test
    @DisplayName("newKakaoMember 메서드로 카카오 인증 비회원 FamilyMember를 생성할 수 있다")
    void new_kakao_member_creates_family_member_without_user_id() {
        // given
        Long familyId = 1L;
        String kakaoId = "kakao_12345";
        String name = "카카오유저";
        String profileUrl = "http://example.com/kakao.jpg";
        
        // when
        FamilyMember familyMember = FamilyMember.newKakaoMember(
            familyId, kakaoId, name, profileUrl
        );
        
        // then
        assertThat(familyMember.getId()).isNull();
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isNull(); // 비회원이므로 userId는 null
        assertThat(familyMember.getKakaoId()).isEqualTo(kakaoId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isNull();
        assertThat(familyMember.getNationality()).isNull();
        assertThat(familyMember.getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
        assertThat(familyMember.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        assertThat(familyMember.isRegisteredUser()).isFalse(); // 비회원
        assertThat(familyMember.isKakaoAuthenticated()).isTrue(); // 카카오 인증됨
    }
    
    @Test
    @DisplayName("newKakaoMember 메서드는 familyId가 null이면 예외가 발생한다")
    void new_kakao_member_throws_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        String kakaoId = "kakao_12345";
        String name = "카카오유저";
        String profileUrl = "http://example.com/kakao.jpg";
        
        // when & then
        assertThatThrownBy(() -> 
            FamilyMember.newKakaoMember(familyId, kakaoId, name, profileUrl)
        ).isInstanceOf(NullPointerException.class)
         .hasMessage("familyId must not be null");
    }
    
    @Test
    @DisplayName("newKakaoMember 메서드는 kakaoId가 null이면 예외가 발생한다")
    void new_kakao_member_throws_exception_when_kakao_id_is_null() {
        // given
        Long familyId = 1L;
        String kakaoId = null;
        String name = "카카오유저";
        String profileUrl = "http://example.com/kakao.jpg";
        
        // when & then
        assertThatThrownBy(() -> 
            FamilyMember.newKakaoMember(familyId, kakaoId, name, profileUrl)
        ).isInstanceOf(NullPointerException.class)
         .hasMessage("kakaoId must not be null");
    }
    
    @Test
    @DisplayName("newKakaoMember 메서드는 name이 null이면 예외가 발생한다")
    void new_kakao_member_throws_exception_when_name_is_null() {
        // given
        Long familyId = 1L;
        String kakaoId = "kakao_12345";
        String name = null;
        String profileUrl = "http://example.com/kakao.jpg";
        
        // when & then
        assertThatThrownBy(() -> 
            FamilyMember.newKakaoMember(familyId, kakaoId, name, profileUrl)
        ).isInstanceOf(NullPointerException.class)
         .hasMessage("name must not be null");
    }

    @Test
    @DisplayName("newOwner 메서드로 OWNER 역할의 FamilyMember를 생성할 수 있다")
    void new_owner_creates_family_member_with_owner_role() {
        // given
        Long familyId = 1L;
        Long userId = 2L;
        String name = "홍길동";
        String profileUrl = "http://example.com/profile.jpg";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "KR";
        
        // when
        FamilyMember familyMember = FamilyMember.newOwner(
            familyId, userId, name, profileUrl, birthday, nationality
        );
        
        // then
        assertThat(familyMember.getId()).isNull();
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getNationality()).isEqualTo(nationality);
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
        String nationality = "KR";
        
        // when
        FamilyMember familyMember = FamilyMember.newMember(
            familyId, userId, name, profileUrl, birthday, nationality
        );
        
        // then
        assertThat(familyMember.getId()).isNull();
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getNationality()).isEqualTo(nationality);
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
        String profileUrl = "http://example.com/profile.jpg";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 1, 0, 0);
        String nationality = "KR";
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        Long createdBy = 4L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 5L;
        LocalDateTime modifiedAt = LocalDateTime.now();
        
        // when
        FamilyMember familyMember = FamilyMember.withId(
            id, familyId, userId, name, profileUrl, birthday, nationality, 
            status, FamilyMemberRole.MEMBER, createdBy, createdAt, modifiedBy, modifiedAt
        );
        
        // then
        assertThat(familyMember.getId()).isEqualTo(id);
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getNationality()).isEqualTo(nationality);
        assertThat(familyMember.getStatus()).isEqualTo(status);
        // withId()는 기본적으로 MEMBER 역할을 부여함 (하위 호환성 유지)
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
        String nationality = "KR";
        FamilyMemberRole role = FamilyMemberRole.ADMIN;
        
        // when
        FamilyMember familyMember = FamilyMember.withRole(
            familyId, userId, name, profileUrl, birthday, nationality, role
        );
        
        // then
        assertThat(familyMember.getId()).isNull(); // 신규 생성이므로 ID는 null
        assertThat(familyMember.getFamilyId()).isEqualTo(familyId);
        assertThat(familyMember.getUserId()).isEqualTo(userId);
        assertThat(familyMember.getName()).isEqualTo(name);
        assertThat(familyMember.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(familyMember.getBirthday()).isEqualTo(birthday);
        assertThat(familyMember.getNationality()).isEqualTo(nationality);
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
            1L, 2L, 3L, "홍길동", "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), "KR",
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
        assertThat(updatedMember.getNationality()).isEqualTo(member.getNationality());
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
            1L, 2L, 3L, "홍길동", "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), "KR",
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
            1L, 2L, 3L, "홍길동", "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), "KR",
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
        assertThat(updatedMember.getNationality()).isEqualTo(member.getNationality());
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
            1L, 2L, 3L, "홍길동", "http://example.com/profile.jpg",
            LocalDateTime.of(1990, 1, 1, 0, 0), "KR",
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
        FamilyMember owner = FamilyMember.newOwner(1L, 2L, "Owner", "", null, "");
        FamilyMember admin = FamilyMember.withId(3L, 1L, 4L, "Admin", "", null, "", 
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN, null, null, null, null);
        FamilyMember member = FamilyMember.newMember(1L, 5L, "Member", "", null, "");
        
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
        FamilyMember activeMember = FamilyMember.withId(1L, 2L, 3L, "Active", "", null, "", 
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, null, null, null, null);
        FamilyMember suspendedMember = FamilyMember.withId(4L, 2L, 5L, "Suspended", "", null, "", 
            FamilyMemberStatus.SUSPENDED, FamilyMemberRole.MEMBER, null, null, null, null);
        FamilyMember bannedMember = FamilyMember.withId(6L, 2L, 7L, "Banned", "", null, "", 
            FamilyMemberStatus.BANNED, FamilyMemberRole.MEMBER, null, null, null, null);
        
        // when & then
        assertThat(activeMember.isActive()).isTrue();
        assertThat(suspendedMember.isActive()).isFalse();
        assertThat(bannedMember.isActive()).isFalse();
    }
}
