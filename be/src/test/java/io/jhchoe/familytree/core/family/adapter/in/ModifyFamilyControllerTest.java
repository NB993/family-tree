package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyUseCase;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;

@DisplayName("[Acceptance Test] ModifyFamilyControllerTest")
@Transactional
class ModifyFamilyControllerTest extends AcceptanceTestBase {

    @MockitoBean
    private ModifyFamilyUseCase modifyFamilyUseCase;

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 유효한 request를 받으면 성공적으로 수정 후 상태코드 200을 응답해야 한다.")
    void given_valid_request_when_modifyFamily_then_return_status_200() {
        // when & then
        when(modifyFamilyUseCase.modify(any(ModifyFamilyCommand.class))).thenReturn(1L);

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
            .put("/api/v1/families/1")
            .then()
            .statusCode(200);
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 이름이 없는 경우 상태코드 400을 응답해야 한다.")
    void given_request_without_name_when_modifyFamily_then_return_status_400() {

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
            .put("/api/v1/families/1")
            .then()
            .statusCode(400);
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 이름이 100자를 초과하는 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_name_over_100_chars_when_modifyFamily_then_return_status_400() {
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
            .put("/api/v1/families/1")
            .then()
            .statusCode(400);
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 설명이 200자를 초과하는 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_description_over_200_chars_when_modifyFamily_then_return_status_400() {
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
            .put("/api/v1/families/1")
            .then()
            .statusCode(400);
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("modifyFamily 메서드는 프로필 URL이 유효하지 않은 경우 상태코드 400을 응답해야 한다.")
    void given_request_with_invalid_profile_url_when_modifyFamily_then_return_status_400() {
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
            .put("/api/v1/families/1")
            .then()
            .statusCode(400);
    }
}
