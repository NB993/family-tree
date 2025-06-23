package io.jhchoe.familytree.common.auth.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.docs.ApiDocsTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * User 조회 API 문서화 테스트
 */
@Tag("user-docs") //JUnit 5에서 제공하는 테스트 태깅(tagging) 기능.
/*
기능 1. 테스트 필터링: 특정 태그가 붙은 테스트만 선택적으로 실행가능. -> API 문서 먼저 전달해야 할 때 편하긴 할듯?
ex) ./gradlew test --tests "*.UserControllerDocsTest" -PincludeTags=user-docs
기능 2. 테스트 그룹화: 유사한 특성이나 목적을 가진 테스트들을 논리적으로 그룹화 가능.
기능 3. 빌드 프로세스 커스터마이징: Gradle이나 Maven에서 태그를 기반으로 특정 테스트 세트만 실행하도록 설정 가능.
 */
@DisplayName("[Docs Test] UserControllerDocsTest")
public class UserControllerDocsTest extends ApiDocsTestBase {

    @Test
    @DisplayName("OAuth2 인증된 사용자 정보를 조회한다")
    @WithMockOAuth2User(email = "oauth@example.com")
    void get_current_oauth2_user() {
        // given
        // FTMockUser 어노테이션으로 인증된 사용자 설정됨
        // 실제 OAuth2 인증 테스트는 별도의 설정이 필요하므로 여기서는 기본 인증 사용자로 대체

        // when & then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/api/user/me")
        .then()
            .status(HttpStatus.OK)
            .log().all()
            .apply(document("user/me-oauth2",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("name").type(JsonFieldType.STRING).optional().description("사용자 이름"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                    fieldWithPath("authType").type(JsonFieldType.STRING).description("인증 유형 (FORM_LOGIN, OAUTH2)"),
                    fieldWithPath("provider").type(JsonFieldType.STRING).optional().description("OAuth2 공급자 (GOOGLE, KAKAO 등)")
                )
            ));
    }
}
