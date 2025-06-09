package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.*;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@DisplayName("[Acceptance Test] FamilyControllerTest")
class SaveFamilyControllerTest extends AcceptanceTestBase {

    @WithMockOAuth2User
    @Test
    @DisplayName("Family 생성 요청 시 성공하면 201 상태코드를 반환한다")
    void test_save_family_success() {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "family name",
                    "description": "family description",
                    "profileUrl": "https://example.com/profile.jpg"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(201)
            .body("id", greaterThan(0));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("Family 생성 요청 시 필수값인 name만 전송해도 성공한다")
    void test_save_family_success_with_only_name() {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "family name"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(201)
            .body("id", greaterThan(0));
    }

    @WithMockOAuth2User
    @ValueSource(strings = {"", " ", "\\t", "\\n"})
    @ParameterizedTest
    @DisplayName("Family 생성 시 이름을 입력하지 않으면 400 상태코드와 유효성 검증 오류를 반환한다")
    void test_save_family_fail_when_name_is_empty(String emptyName) {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body(String.format("""
                {
                    "name": "%s"
                }
                """, emptyName))
            .when()
            .post("/api/families")
            .then()
            .body("validations.size()", greaterThan(0)) // validations 배열 크기 확인
            .body("validations.field", hasItem("name")) // validations 목록 중 field: "name" 존재
            .body("validations.find { it.field == 'name' }.message",
                not(emptyOrNullString())) // 해당 필드의 message가 비어있지 않음
            .statusCode(400);
    }
    
    @WithMockOAuth2User
    @Test
    @DisplayName("Family 생성 시 프로필 URL이 http:// 또는 https://로 시작하지 않으면 400 상태코드를 반환한다")
    void test_save_family_fail_when_profile_url_is_invalid() {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "family name",
                    "profileUrl": "invalid-url"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(400);
    }
    
    @Test
    @DisplayName("Family 생성 시 인증되지 않은 사용자는 401 상태코드를 반환한다")
    void test_save_family_fail_when_user_is_not_authenticated() {
        // given & when & then
        RestAssuredMockMvc
            .given()
            .contentType(MediaType.APPLICATION_JSON)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "family name"
                }
                """)
            .when()
            .post("/api/families")
            .then()
            .statusCode(401);
    }
}
