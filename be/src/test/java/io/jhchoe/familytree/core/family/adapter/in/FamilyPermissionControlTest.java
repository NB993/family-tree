package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] FamilyPermissionControlTest")
class FamilyPermissionControlTest extends AcceptanceTestBase {

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
    @DisplayName("Family 수정 - OWNER 권한 성공")
    @WithMockOAuth2User(id = 1L, email = "owner@test.com")
    void modify_family_owner_role_success() {
        // given
        Long userId = 1L;
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 권한 구성원 생성
        FamilyMember ownerMember = FamilyMember.newOwner(
            familyId, userId, null, "소유자", "profile.jpg", LocalDateTime.now(), "KR"
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "수정된가족명",
                    "profileUrl": "http://example.com/new-profile",
                    "description": "수정된설명"
                }
                """)
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Family 수정 - ADMIN 권한 실패")
    @WithMockOAuth2User(id = 2L, email = "admin@test.com")
    void modify_family_admin_role_fail() {
        // given
        Long ownerId = 1L;
        Long adminId = 2L;
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 권한 구성원 생성 (다른 사용자)
        FamilyMember ownerMember = FamilyMember.newOwner(
            familyId, ownerId, null, "소유자", "profile.jpg", LocalDateTime.now(), "KR"
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // ADMIN 권한 구성원 생성 (현재 사용자)
        FamilyMember adminMember = FamilyMember.withRole(
            familyId, adminId, "관리자", "profile.jpg", LocalDateTime.now(), "KR", FamilyMemberRole.ADMIN
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(adminMember));

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "수정된가족명",
                    "profileUrl": "http://example.com/new-profile",
                    "description": "수정된설명"
                }
                """)
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(403)
            .body("message", equalTo("해당 작업을 수행할 권한이 없습니다."));
    }

    @Test
    @DisplayName("Family 수정 - MEMBER 권한 실패")
    @WithMockOAuth2User(id = 3L, email = "member@test.com")
    void modify_family_member_role_fail() {
        // given
        Long ownerId = 1L;
        Long memberId = 3L;
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 권한 구성원 생성 (다른 사용자)
        FamilyMember ownerMember = FamilyMember.newOwner(
            familyId, ownerId, null, "소유자", "profile.jpg", LocalDateTime.now(), "KR"
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // MEMBER 권한 구성원 생성 (현재 사용자)
        FamilyMember member = FamilyMember.withRole(
            familyId, memberId, "일반구성원", "profile.jpg", LocalDateTime.now(), "KR", FamilyMemberRole.MEMBER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "수정된가족명",
                    "profileUrl": "http://example.com/new-profile",
                    "description": "수정된설명"
                }
                """)
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(403)
            .body("message", equalTo("해당 작업을 수행할 권한이 없습니다."));
    }

    @Test
    @DisplayName("비공개 Family 조회 - 구성원 성공")
    @WithMockOAuth2User(id = 1L, email = "member@test.com")
    void find_family_private_member_success() {
        // given
        Long userId = 1L;
        Family privateFamily = FamilyFixture.newFamily("비공개가족", null, null, false);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(privateFamily));
        Long familyId = savedFamily.getId();

        // 구성원 생성
        FamilyMember member = FamilyMember.withRole(
            familyId, userId, "구성원", "profile.jpg", LocalDateTime.now(), "KR", FamilyMemberRole.MEMBER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("비공개가족"));
    }

    @Test
    @DisplayName("비공개 Family 조회 - 비구성원 실패")
    @WithMockOAuth2User(id = 999L, email = "outsider@test.com")
    void find_family_private_non_member_fail() {
        // given
        Long ownerId = 1L;
        Family privateFamily = FamilyFixture.newFamily("비공개가족", null, null, false);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(privateFamily));
        Long familyId = savedFamily.getId();

        // 다른 사용자를 구성원으로 생성
        FamilyMember member = FamilyMember.withRole(
            familyId, ownerId, "구성원", "profile.jpg", LocalDateTime.now(), "KR", FamilyMemberRole.MEMBER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(403)
            .body("message", equalTo("접근이 거부되었습니다."));
    }

    @Test
    @DisplayName("공개 Family 조회 - 비구성원 성공")
    @WithMockOAuth2User(id = 999L, email = "outsider@test.com")
    void find_family_public_non_member_success() {
        // given
        Long ownerId = 1L;
        Family publicFamily = FamilyFixture.newFamily("공개가족");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(publicFamily));
        Long familyId = savedFamily.getId();

        // 다른 사용자를 구성원으로 생성
        FamilyMember member = FamilyMember.withRole(
            familyId, ownerId, "구성원", "profile.jpg", LocalDateTime.now(), "KR", FamilyMemberRole.MEMBER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));

        // when & then
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("공개가족"));
    }
}
