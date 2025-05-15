package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
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
import org.springframework.transaction.annotation.Transactional;

@DisplayName("[Acceptance Test] ModifyFamilyControllerTest")
@Transactional
class ModifyFamilyControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        familyJpaRepository.deleteAll();
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 유효한 request를 받으면 성공적으로 수정 후 상태코드 200을 응답해야 한다.")
    void given_valid_request_when_modifyFamily_then_return_status_200() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = Family.newFamily(
            "Original Family Name",
            "Original description",
            "http://example.com/original-profile"
        );
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

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

        // 데이터베이스에 실제로 변경이 적용되었는지 확인
        FamilyJpaEntity updatedFamily = familyJpaRepository.findById(familyId).orElseThrow();

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
    void given_request_without_name_when_modifyFamily_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = Family.newFamily(
            "Original Family Name",
            "Original description",
            "http://example.com/original-profile"
        );
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

        // 데이터베이스 변경이 없는지 확인
        FamilyJpaEntity notUpdatedFamily = familyJpaRepository.findById(familyId).orElseThrow();

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("Original Family Name"))
            .body("description", equalTo("Original description"))
            .body("profileUrl", equalTo("http://example.com/original-profile"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 이름이 100자를 초과하는 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_name_over_100_chars_when_modifyFamily_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = Family.newFamily(
            "Original Family Name",
            "Original description",
            "http://example.com/original-profile"
        );
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

        // 데이터베이스 변경이 없는지 확인
        FamilyJpaEntity notUpdatedFamily = familyJpaRepository.findById(familyId).orElseThrow();

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("Original Family Name"))
            .body("description", equalTo("Original description"))
            .body("profileUrl", equalTo("http://example.com/original-profile"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 설명이 200자를 초과하는 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_description_over_200_chars_when_modifyFamily_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = Family.newFamily(
            "Original Family Name",
            "Original description",
            "http://example.com/original-profile"
        );
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

        // 데이터베이스 변경이 없는지 확인
        FamilyJpaEntity notUpdatedFamily = familyJpaRepository.findById(familyId).orElseThrow();

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("Original Family Name"))
            .body("description", equalTo("Original description"))
            .body("profileUrl", equalTo("http://example.com/original-profile"));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 프로필 URL이 유효하지 않은 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_invalid_profile_url_when_modifyFamily_then_return_status_400() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        FTUser ftUser = (FTUser) authentication.getPrincipal();
        Long userId = ftUser.getId();

        Family family = Family.newFamily(
            "Original Family Name",
            "Original description",
            "http://example.com/original-profile"
        );
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

        // 데이터베이스 변경이 없는지 확인
        FamilyJpaEntity notUpdatedFamily = familyJpaRepository.findById(familyId).orElseThrow();

        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(200)
            .body("id", equalTo(familyId.intValue()))
            .body("name", equalTo("Original Family Name"))
            .body("description", equalTo("Original description"))
            .body("profileUrl", equalTo("http://example.com/original-profile"));
    }
}
