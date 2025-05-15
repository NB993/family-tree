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

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyRequest;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.docs.ApiDocsTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Family 생성 API 문서화 테스트
 */
@Tag("family-docs")
public class SaveFamilyDocsTest extends ApiDocsTestBase {

    @MockitoBean
    private SaveFamilyUseCase saveFamilyUseCase;

    @Test
    @DisplayName("새로운 Family를 생성한다")
    @WithMockOAuth2User(email = "oauth@example.com")
    void save_family() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "우리 가족",
            "가족 소개 및 설명",
            "https://example.com/profile-image.jpg"
        );
        
        when(saveFamilyUseCase.save(any())).thenReturn(1L);

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/api/family")
        .then()
            .status(HttpStatus.CREATED)
            .log().all()
            .apply(document("family/save-family",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("가족 이름 (필수, 100자 이내)"),
                    fieldWithPath("description").type(JsonFieldType.STRING).optional().description("가족 설명 (선택, 200자 이내)"),
                    fieldWithPath("profileUrl").type(JsonFieldType.STRING).optional().description("프로필 이미지 URL (http:// 또는 https://로 시작)")
                )
            ));
    }
}
