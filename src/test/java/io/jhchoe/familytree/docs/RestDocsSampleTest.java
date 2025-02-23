package io.jhchoe.familytree.docs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import io.jhchoe.familytree.config.FTMockUser;
import io.restassured.authentication.FormAuthConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[E2E Test] Rest Docs 생성 테스트")
public class RestDocsSampleTest extends E2ETestBase {

    @Test
    @FTMockUser
    void sample() {
        given().spec(this.spec)
            .auth().form("ftuser@email.com", "password", new FormAuthConfig("/login", "username", "password"))
            .when()
                .get("/api/test/e2e-rest-docs")
            .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.name", equalTo("testName"))
                .body("data.age", equalTo(10))
                .body("data.dept", equalTo("testDept"))
                .body("error", nullValue())
                .log().all();
    }
}
