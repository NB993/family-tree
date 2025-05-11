package io.jhchoe.familytree.core.family.adapter.in;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("[Acceptance Test] FamilyControllerTest")
class SaveFamilyControllerTest extends AcceptanceTestBase {

    @WithMockOAuth2User
    @Test
    @DisplayName("Family 생성 성공 테스트")
    void testSaveFamily() {
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
            .post("/api/family")
            .then()
            .statusCode(201);
    }

    @WithMockOAuth2User
    @ValueSource(strings = {"", " ", "\\t", "\\n"})
    @ParameterizedTest
    @DisplayName("Family 생성시 이름을 입력하지 않으면 예외를 응답한다.")
    void test_saveFamily_fail_when_name_is_empty(String emptyName) {
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
            .post("/api/family")
            .then()
            .body("validations.size()", greaterThan(0)) // validations 배열 크기 확인
            .body("validations.field", hasItem("name")) // validations 목록 중 field: "name" 존재
            .body("validations.find { it.field == 'name' }.message",
                not(emptyOrNullString())) // 해당 필드의 message가 비어있지 않음
            .statusCode(400);
    }
}
