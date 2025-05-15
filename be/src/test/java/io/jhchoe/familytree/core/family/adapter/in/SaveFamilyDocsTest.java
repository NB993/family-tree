package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.docs.ApiDocsTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("[API Docs] Family 생성 API 문서화 테스트")
class SaveFamilyDocsTest extends ApiDocsTestBase {

    @MockitoBean
    private SaveFamilyUseCase saveFamilyUseCase;

    @Test
    @DisplayName("새로운 Family를 생성한다")
    @WithMockOAuth2User
    void given_valid_save_family_request_when_save_then_success() {
        // given
        when(saveFamilyUseCase.save(any())).thenReturn(1L);

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "우리 가족",
                    "description": "가족 소개 및 설명",
                    "profileUrl": "https://example.com/profile-image.jpg"
                }
                """)
        .when()
            .post("/api/family")
        .then()
            .status(HttpStatus.CREATED)
            .apply(document("family/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING)
                        .description("가족 이름 (필수, 100자 이내)")
                        .attributes(key("constraints").value("필수 입력")),
                    fieldWithPath("description").type(JsonFieldType.STRING)
                        .optional()
                        .description("가족 설명 (선택, 200자 이내)"),
                    fieldWithPath("profileUrl").type(JsonFieldType.STRING)
                        .optional()
                        .description("프로필 이미지 URL (http:// 또는 https://로 시작해야 함)")
                )
            ));
    }

    // todo @Valid 예외처리랑 Command 혹은 Query 생성시 생성자에서 발생하는 IllegalArgumentException 예외처리할 때
    // GlobalExceptionHandler에서 각각 처리하는 구조가 다르다보니 예외 케이스 작성할 때 동일한 구조로 검증하지 못하는 문제가 있음.
    @Test
    @DisplayName("Family 생성 시 name 요청값이 비어있거나 100자를 초과한 경우 에러코드 400을 응답한다")
    @WithMockOAuth2User
    void given_invalid_name_when_save_family_then_return_status_400() {
        // given

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "name": ""
                }
                """)
        .when()
            .post("/api/family")
        .then()
            .status(HttpStatus.BAD_REQUEST)
            .apply(document("family/save-invalid-request",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // 에러 응답 문서화
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
                    fieldWithPath("traceId").type(JsonFieldType.STRING).description("오류 추적 ID"),
                    fieldWithPath("validations").type(JsonFieldType.ARRAY).description("유효성 검증 오류 목록"),
                    fieldWithPath("validations[].field").type(JsonFieldType.STRING).description("오류가 발생한 필드명"),
                    fieldWithPath("validations[].message").type(JsonFieldType.STRING).description("오류 메시지"),
                    fieldWithPath("validations[].value").type(JsonFieldType.STRING).description("오류를 발생시킨 입력값")
                )
            ));
    }

    @Test
    @DisplayName("Family 생성 시 profileUrl 요청값이 http:// 또는 https://로 시작하지 않는 경우 에러코드 400을 응답한다")
    @WithMockOAuth2User
    void given_invalid_profile_url_when_save_family_then_return_status_400() {
        // given

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "name": "우리 가족",
                    "profileUrl": "invalid_url"
                }
                """)
            .when()
            .post("/api/family")
            .then()
            .status(HttpStatus.BAD_REQUEST)
            .apply(document("family/save-invalid-profile-url",  // 문서 이름 변경
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                // 에러 응답 문서화
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
                    fieldWithPath("traceId").type(JsonFieldType.STRING).description("오류 추적 ID"),
                    fieldWithPath("validations").type(JsonFieldType.ARRAY).description("유효성 검증 오류 목록")
                )
            ));
    }
}
