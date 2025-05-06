package io.jhchoe.familytree.common.exception;

import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.exception.ControllerStub.TestRequestBody;
import io.jhchoe.familytree.config.FTMockUser;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.*;

@DisplayName("[Integration Test] GlobalExceptionHandler")
class GlobalExceptionHandlerTest extends AcceptanceTestBase {

    @WithMockOAuth2User
    @Test
    @DisplayName("잘못된 인자를 전달한 경우 IllegalArgumentException이 발생해야 한다.")
    void given_invalid_argument_when_get_then_illegal_argument_exception() {
        RestAssuredMockMvc
            .given()
            .when()
            .get("/test/illegal-argument")
            .then()
            .statusCode(400) // 기본 처리 결과 확인
            .body("code", equalTo("400"))
            .body("message", containsString("Invalid argument"))
            .body("validations", empty());
    }

    @Test
    @DisplayName("미인증 사용자가 접근 시 UNAUTHORIZED 코드와 메시지가 반환되어야 한다.")
    void given_unauthenticated_user_when_access_then_return_unauthorized() {
        RestAssuredMockMvc
            .given()
            .when()
            .get("/test/ft-exception")
            .then()
            .statusCode(AuthExceptionCode.UNAUTHORIZED.getStatus().value())
            .body("code", equalTo(AuthExceptionCode.UNAUTHORIZED.getCode()))
            .body("message", equalTo(AuthExceptionCode.UNAUTHORIZED.getMessage()));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("권한 없는 사용자가 접근 시 ACCESS_DENIED 코드와 메시지가 반환되어야 한다.")
    void given_forbidden_user_when_access_then_return_access_denied() {
        RestAssuredMockMvc
            .given()
            .when()
            .get("/api/admin")
            .then()
            .statusCode(AuthExceptionCode.ACCESS_DENIED.getStatus().value())
            .body("code", equalTo(AuthExceptionCode.ACCESS_DENIED.getCode()))
            .body("message", equalTo(AuthExceptionCode.ACCESS_DENIED.getMessage()));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("지원되지 않는 HTTP 메서드를 호출한 경우 405 상태 코드를 반환해야 한다.")
    void given_unsupported_http_method_when_called_then_return_405_status_code() {
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .when()
            .delete("/test/http-request-method-not-supported-exception")
            .then()
            .statusCode(405) // HTTP 405
            .body("code", equalTo("405"))
            .body("message", not(empty()))
            .body("validations", empty());
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("읽을 수 없는 HTTP 메시지가 전달된 경우 400 상태 코드를 반환해야 한다.")
    void given_unreadable_http_message_when_post_then_return_400_status_code() {
        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE) // ContentType 설정만 하고 Body 없음
            .when()
            .post("/test/http-message-not-readable-exception")
            .then()
            .statusCode(400) // HTTP 400
            .body("code", equalTo("400"))
            .body("message", not(empty()))
            .body("validations", empty());
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("유효하지 않은 JSON 요청 본문이 들어오면 403 코드와 메시지가 반환되어야 한다.")
    void given_invalid_request_body_when_post_then_return_403_status_code() {
        TestRequestBody invalidBody = new TestRequestBody("1234", -1, List.of());

        RestAssuredMockMvc
            .given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(invalidBody) // JSON Request Body 설정
            .when()
            .post("/test/method-argument-not-valid")
            .then()
            .statusCode(400) // HTTP 400
            .body("code", equalTo("400"))
            .body("message", equalTo("입력 조건을 위반하였습니다."))
            .body("validations.size()", greaterThan(0));
    }

    @WithMockOAuth2User
    @Test
    @DisplayName("FTException이 발생한 경우 코드와 기본 메시지를 반환해야 한다.")
    void given_ft_exception_when_get_then_return_default_response() {
        RestAssuredMockMvc
            .given()
            .when()
            .get("/test/ft-exception")
            .then()
            .statusCode(400) // FTException의 기본 상태 코드 확인
            .body("code", equalTo("C001"))
            .body("message", containsString("파라미터 누락."))
            .body("validations", empty());
    }

    @Test
    @DisplayName("예기치 않은 서버 오류가 발생한 경우 500 코드와 메시지가 반환되어야 한다.")
    @WithMockOAuth2User
    void given_unexpected_error_when_get_then_return_internal_server_error() {
        RestAssuredMockMvc
            .given()
            .when()
            .get("/test/internal-server-error")
            .then()
            .statusCode(500) // HTTP 500
            .body("code", equalTo("500"))
            .body("message", equalTo("서버 에러"));
    }
}
