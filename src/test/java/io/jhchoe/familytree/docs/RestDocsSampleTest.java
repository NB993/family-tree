package io.jhchoe.familytree.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.jhchoe.familytree.config.FTMockUser;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Docs Test] Rest Docs 생성 테스트")
public class RestDocsSampleTest extends ApiDocsTestBase {

    @Test
    @FTMockUser
    @DisplayName("테스트1")
    void testOneDepth() {
        RestAssuredMockMvc.given()
            .when()
            .get("/api/test/e2e-rest-docs")
            .then()
            .statusCode(200)
            .apply(document("domain",
                responseFields(
                    fieldWithPath("success").description("응답 성공 여부"),
                    fieldWithPath("data.name").description("이름"),
                    fieldWithPath("data.age").description("나이"),
                    fieldWithPath("data.dept").description("부서명")
                )
            ));
    }

    @Test
    @FTMockUser
    @DisplayName("테스트2")
    void testTwoDepth() {
        RestAssuredMockMvc.given()
            .when()
            .get("/api/test/e2e-rest-docs-2")
            .then()
            .statusCode(200)
            .apply(document("domain2",
                responseFields(
                    fieldWithPath("success").description("응답 성공 여부"),
                    fieldWithPath("data.name").description("이름"),
                    fieldWithPath("data.age").description("나이"),
                    fieldWithPath("data.dept").description("부서명")
                )
            ));
    }
}
