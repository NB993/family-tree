package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJoinRequestJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJoinRequestJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] FindFamilyJoinRequestControllerTest")
class FindFamilyJoinRequestAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private FamilyJoinRequestJpaRepository familyJoinRequestJpaRepository;

    @AfterEach
    void tearDown() {
        familyJoinRequestJpaRepository.deleteAll();
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER 권한으로 가입 신청 목록 조회 시 200 OK와 목록을 반환합니다")
    void find_returns_200_and_join_requests_when_owner_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        
        // Family 생성
        Family family = Family.withId(
            null, "테스트 가족", "가족 설명", "profile.jpg",
            ownerUserId, now, ownerUserId, now
        );
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // 가입 신청 2개 생성
        FamilyJoinRequest joinRequest1 = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequest joinRequest2 = FamilyJoinRequest.newRequest(familyId, 102L);
        
        FamilyJoinRequestJpaEntity savedRequest1 = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest1)
        );
        FamilyJoinRequestJpaEntity savedRequest2 = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest2)
        );

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/join-requests", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(2))
            .body("[0].id", equalTo(savedRequest2.getId().intValue())) // 최근 요청 순으로 정렬하여 응답한다
            .body("[0].requesterId", equalTo(102))
            .body("[0].status", equalTo("PENDING"))
            .body("[1].id", equalTo(savedRequest1.getId().intValue()))
            .body("[1].requesterId", equalTo(101))
            .body("[1].status", equalTo("PENDING"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("ADMIN 권한으로 가입 신청 목록 조회 시 200 OK와 목록을 반환합니다")
    void find_returns_200_and_join_requests_when_admin_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long adminUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        Long ownerUserId = 100L;
        
        // Family 생성
        Family family = Family.withId(
            null, "테스트 가족", "가족 설명", "profile.jpg",
            ownerUserId, now, ownerUserId, now
        );
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // ADMIN 구성원 생성 (현재 인증된 사용자)
        FamilyMember adminMember = FamilyMember.withRole(
            familyId, adminUserId, "김관리자", "admin.jpg", 
            now.minusYears(25), "한국", FamilyMemberRole.ADMIN
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(adminMember));
        
        // 가입 신청 1개 생성
        FamilyJoinRequest joinRequest = FamilyJoinRequest.newRequest(familyId, 101L);
        FamilyJoinRequestJpaEntity savedRequest = familyJoinRequestJpaRepository.save(
            FamilyJoinRequestJpaEntity.from(joinRequest)
        );

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/join-requests", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(1))
            .body("[0].id", equalTo(savedRequest.getId().intValue()))
            .body("[0].requesterId", equalTo(101))
            .body("[0].status", equalTo("PENDING"));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("MEMBER 권한으로 가입 신청 목록 조회 시 403 Forbidden을 반환합니다")
    void find_returns_403_when_member_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long memberUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        Long ownerUserId = 100L;
        
        // Family 생성
        Family family = Family.withId(
            null, "테스트 가족", "가족 설명", "profile.jpg",
            ownerUserId, now, ownerUserId, now
        );
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));
        
        // MEMBER 구성원 생성 (현재 인증된 사용자)
        FamilyMember normalMember = FamilyMember.newMember(
            familyId, memberUserId, "김일반", "member.jpg",
            now.minusYears(20), "한국"
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(normalMember));

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/join-requests", familyId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("Family 구성원이 아닌 사용자가 조회 시 403 Forbidden을 반환합니다")
    void find_returns_403_when_non_family_member_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 100L;
        Long currentUserId = 1L; // WithMockOAuth2User 기본 사용자 ID (Family 구성원 아님)
        
        // Family 생성
        Family family = Family.withId(
            null, "테스트 가족", "가족 설명", "profile.jpg",
            ownerUserId, now, ownerUserId, now
        );
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
            .get("/api/families/{familyId}/join-requests", familyId)
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
            .get("/api/families/{familyId}/join-requests", nonExistentFamilyId)
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
            .get("/api/families/{familyId}/join-requests", familyId)
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("가입 신청이 없는 경우 빈 목록을 반환합니다")
    void find_returns_empty_list_when_no_join_requests() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long ownerUserId = 1L; // WithMockOAuth2User 기본 사용자 ID
        
        // Family 생성
        Family family = Family.withId(
            null, "테스트 가족", "가족 설명", "profile.jpg",
            ownerUserId, now, ownerUserId, now
        );
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();
        
        // OWNER 구성원 생성
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "김소유자", "owner.jpg", 
            now.minusYears(30), "한국", FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // when & then
        given()
        .when()
            .get("/api/families/{familyId}/join-requests", familyId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(0));
    }
}
