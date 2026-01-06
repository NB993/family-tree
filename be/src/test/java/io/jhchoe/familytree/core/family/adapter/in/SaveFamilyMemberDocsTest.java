package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.application.service.SaveFamilyMemberService;
import io.jhchoe.familytree.docs.ApiDocsTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("[API Docs] FamilyMember 수동 등록 API 문서화 테스트")
class SaveFamilyMemberDocsTest extends ApiDocsTestBase {

    @MockitoBean
    private SaveFamilyMemberService saveFamilyMemberService;

    @Test
    @DisplayName("가족 구성원을 수동으로 등록한다")
    @WithMockOAuth2User(id = 1L)
    void given_valid_request_when_save_family_member_then_success() {
        // given
        when(saveFamilyMemberService.save(any(), eq(1L))).thenReturn(100L);

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "홍길동",
                    "birthday": "1990-01-15T00:00:00",
                    "relationshipType": "FATHER"
                }
                """)
        .when()
            .post("/api/families/{familyId}/members", 1L)
        .then()
            .status(HttpStatus.CREATED)
            .apply(document("family-member/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("familyId").description("가족 ID")
                ),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING)
                        .description("구성원 이름 (필수, 50자 이내)")
                        .attributes(key("constraints").value("필수 입력")),
                    fieldWithPath("birthday").type(JsonFieldType.STRING)
                        .optional()
                        .description("생년월일 (선택, ISO 8601 형식)"),
                    fieldWithPath("relationshipType").type(JsonFieldType.STRING)
                        .optional()
                        .description("관계 유형 (선택, FATHER/MOTHER/SON/DAUGHTER/SPOUSE/SIBLING/PET/CUSTOM 등)")
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER)
                        .description("저장된 구성원 ID")
                )
            ));
    }

    @Test
    @DisplayName("CUSTOM 관계 유형으로 가족 구성원을 등록한다")
    @WithMockOAuth2User(id = 1L)
    void given_custom_relationship_when_save_family_member_then_success() {
        // given
        when(saveFamilyMemberService.save(any(), eq(1L))).thenReturn(101L);

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": "외할아버지",
                    "relationshipType": "CUSTOM",
                    "customRelationship": "외할아버지"
                }
                """)
        .when()
            .post("/api/families/{familyId}/members", 1L)
        .then()
            .status(HttpStatus.CREATED)
            .apply(document("family-member/save-custom-relationship",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("familyId").description("가족 ID")
                ),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING)
                        .description("구성원 이름 (필수)")
                        .attributes(key("constraints").value("필수 입력")),
                    fieldWithPath("relationshipType").type(JsonFieldType.STRING)
                        .description("관계 유형 (CUSTOM)"),
                    fieldWithPath("customRelationship").type(JsonFieldType.STRING)
                        .description("사용자 정의 관계명 (CUSTOM 타입인 경우 필수, 50자 이내)")
                        .attributes(key("constraints").value("CUSTOM 타입 시 필수"))
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER)
                        .description("저장된 구성원 ID")
                )
            ));
    }

    @Test
    @DisplayName("이름이 비어있는 경우 400 Bad Request를 반환한다")
    @WithMockOAuth2User(id = 1L)
    void given_blank_name_when_save_family_member_then_return_400() {
        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .body("""
                {
                    "name": ""
                }
                """)
        .when()
            .post("/api/families/{familyId}/members", 1L)
        .then()
            .status(HttpStatus.BAD_REQUEST)
            .apply(document("family-member/save-invalid-name",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
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
}
