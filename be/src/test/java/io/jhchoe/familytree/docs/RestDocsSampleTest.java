package io.jhchoe.familytree.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

@DisplayName("[Docs Test] Rest Docs 생성 테스트")
public class RestDocsSampleTest extends ApiDocsTestBase {

    @Test
    @WithMockOAuth2User
    @DisplayName("테스트1")
    void testOneDepth() {
        RestAssuredMockMvc.given()
            .when()
            .get("/api/test/e2e-rest-docs")
            .then()
            .statusCode(200)
            .apply(document("domain",
                responseFields(
                    fieldWithPath("name").description("이름"),
                    fieldWithPath("age").description("나이"),
                    fieldWithPath("dept").description("부서명")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("테스트2")
    void testTwoDepth() {
        RestAssuredMockMvc.given()
            .when()
            .get("/api/test/e2e-rest-docs-2")
            .then()
            .statusCode(200)
            .apply(document("domain2",
                responseFields(
                    fieldWithPath("name").description("이름"),
                    fieldWithPath("age").description("나이"),
                    fieldWithPath("dept").description("부서명")
                )
            ));
    }
}
