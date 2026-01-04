package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] FindFamilyMemberRoleControllerTest")
class FindFamilyMemberRoleAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("Family 구성원이 역할 조회 시 200 OK와 구성원 목록을 반환합니다")
    void find_returns_200_and_member_roles_when_family_member_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long userId = 1L; // WithMockOAuth2User 기본 사용자 ID
        
        // Family 생성 (withId로 필요한 필드 포함)
        Family family = FamilyFixture.newFamily("테스트 가족", "가족 설명", "profile.jpg", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, userId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // ADMIN 구성원 생성
        FamilyMember adminMember = FamilyMember.withRole(
            familyId, 101L, "김관리자", "admin.jpg",
            now.minusYears(25), "한국", FamilyMemberRole.ADMIN
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(adminMember));
        
        // MEMBER 구성원 생성
        FamilyMember normalMember = FamilyMember.newMember(
            familyId, 102L, "김일반", "member.jpg",
            now.minusYears(20), "한국"
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(normalMember));

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/members/roles", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(3))
            .body("[0].userId", equalTo(userId.intValue()))
            .body("[0].name", equalTo("김소유자"))
            .body("[0].role", equalTo("OWNER"))
            .body("[0].status", equalTo("ACTIVE"))
            .body("[1].userId", equalTo(101))
            .body("[1].name", equalTo("김관리자"))
            .body("[1].role", equalTo("ADMIN"))
            .body("[1].status", equalTo("ACTIVE"))
            .body("[2].userId", equalTo(102))
            .body("[2].name", equalTo("김일반"))
            .body("[2].role", equalTo("MEMBER"))
            .body("[2].status", equalTo("ACTIVE"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("Family 구성원이 아닌 사용자가 조회 시 403 Forbidden을 반환합니다")
    void find_returns_403_when_non_family_member_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 100L;
        Long currentUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        
        // Family 생성
        Family family = FamilyFixture.newFamily("테스트 가족", "가족 설명", "profile.jpg", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // 다른 사용자의 OWNER 구성원 생성 (현재 인증된 사용자 ID=1과 다름)
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/members/roles", familyId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 Family ID로 조회 시 404 Not Found를 반환합니다")
    void find_returns_404_when_family_not_exists() {
        // given
        Long nonExistentFamilyId = 999L;

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/members/roles", nonExistentFamilyId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 조회 시 401 Unauthorized를 반환합니다")
    void find_returns_401_when_unauthenticated_user_requests() {
        // given
        Long familyId = 1L;

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/members/roles", familyId)
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("SUSPENDED 상태의 구성원도 목록에 포함되어 반환됩니다")
    void find_returns_suspended_members_in_list() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long userId = 1L; // WithMockOAuth2User 기본 사용자 ID
        
        // Family 생성
        Family family = FamilyFixture.newFamily("테스트 가족", "가족 설명", "profile.jpg", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, userId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // SUSPENDED 상태의 구성원을 withRole로 MEMBER로 생성 후 상태만 변경
        FamilyMember memberForSuspension = FamilyMember.withRole(
            familyId, 101L, "김정지", "suspended.jpg",
            now.minusYears(25), "한국", FamilyMemberRole.MEMBER
        );
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(memberForSuspension));
        
        // 저장된 엔티티를 도메인으로 변환 후 상태 변경하여 다시 저장
        FamilyMember savedMember = savedEntity.toFamilyMember();
        FamilyMember suspendedMember = savedMember.updateStatus(FamilyMemberStatus.SUSPENDED);
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(suspendedMember));

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/members/roles", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(2))
            .body("[1].userId", equalTo(101))
            .body("[1].name", equalTo("김정지"))
            .body("[1].role", equalTo("MEMBER"))
            .body("[1].status", equalTo("SUSPENDED"));
    }
}
