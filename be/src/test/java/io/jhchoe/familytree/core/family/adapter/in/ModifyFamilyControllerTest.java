package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] ModifyFamilyControllerTest")
class ModifyFamilyControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 유효한 request를 받으면 성공적으로 수정 후 상태코드 200을 응답해야 한다.")
    void given_valid_request_when_modify_family_then_return_status_200() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // OWNER 권한 구성원 생성 (권한 검증을 위해 필요)
        FamilyMember ownerMember = FamilyMember.newOwner(
            familyId, userId, "테스트소유자", "profile.jpg",
            LocalDateTime.now(), null
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "Updated Family Name",
                    "profileUrl": "http://example.com/updated-profile",
                    "description": "Updated description"
                }
                """)
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(200);

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("Updated Family Name"))
            .body("description", equalTo("Updated description"))
            .body("profileUrl", equalTo("http://example.com/updated-profile"))
            .body("createdBy", is(notNullValue()))
            .body("createdAt", is(notNullValue()))
            .body("modifiedBy", is(notNullValue()))
            .body("modifiedAt", is(notNullValue()));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 이름이 없는 경우 상태코드 400을 응답해야 한다.")
    void given_request_without_name_when_modify_family_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "",
                    "profileUrl": "http://example.com/updated-profile",
                    "description": "Updated description"
                }
                """)
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(400);

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("테스트가족"))
            .body("description", equalTo("테스트 가족 설명"))
            .body("profileUrl", equalTo("https://example.com/family-profile.jpg"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 이름이 100자를 초과하는 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_name_over_100_chars_when_modify_family_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                {
                    "name": "%s",
                    "profileUrl": "http://example.com/updated-profile",
                    "description": "Updated description"
                }
                """, "a".repeat(101)))
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(400);

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("테스트가족"))
            .body("description", equalTo("테스트 가족 설명"))
            .body("profileUrl", equalTo("https://example.com/family-profile.jpg"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 설명이 200자를 초과하는 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_description_over_200_chars_when_modify_family_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body(String.format("""
                {
                    "name": "Valid Name",
                    "profileUrl": "http://example.com/updated-profile",
                    "description": "%s"
                }
                """, "a".repeat(201)))
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(400);

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("테스트가족"))
            .body("description", equalTo("테스트 가족 설명"))
            .body("profileUrl", equalTo("https://example.com/family-profile.jpg"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 프로필 URL이 유효하지 않은 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_invalid_profile_url_when_modify_family_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        // when & then
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .body("""
                {
                    "name": "Valid Name",
                    "profileUrl": "invalid-url",
                    "description": "Valid description"
                }
                """)
            .when()
            .put("/api/families/{id}", familyId)
            .then()
            .statusCode(400);

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("테스트가족"))
            .body("description", equalTo("테스트 가족 설명"))
            .body("profileUrl", equalTo("https://example.com/family-profile.jpg"));
    }
}
