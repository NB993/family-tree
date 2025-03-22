package io.jhchoe.familytree.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.config.FTSpringSecurityExceptionHandler;
import io.jhchoe.familytree.common.config.SecurityConfig;
import io.jhchoe.familytree.common.exception.ControllerStub.TestRequestBody;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Integration Test] GlobalExceptionHandler")
@WebMvcTest(ControllerStub.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, FTSpringSecurityExceptionHandler.class, ObjectMapper.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("IllegalArgumentException")
    @WithMockUser
    void testHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
            .andExpect(status().isBadRequest()) // 기본 처리 결과 확인
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message", Matchers.containsString("Invalid argument")))
            .andExpect(jsonPath("$.validations").isEmpty())
            .andReturn();
    }

    @Test
    @DisplayName("미인증 사용자가 접근 시 AuthExceptionCode.UNAUTHORIZED 반환")
    void unauthorized() throws Exception {
        mockMvc.perform(get("/test/ft-exception"))
            .andExpect(status().is(AuthExceptionCode.UNAUTHORIZED.getStatus().value()))
            .andExpect(jsonPath("$.code").value(AuthExceptionCode.UNAUTHORIZED.getCode()))
            .andExpect(jsonPath("$.message").value(AuthExceptionCode.UNAUTHORIZED.getMessage()))
            .andReturn();
    }

    @Test
    @DisplayName("권한 없는 사용자가 접근 시 AuthExceptionCode.ACCESS_DENIED 반환")
    @WithMockUser(username = "user", roles = {"USER"})
    void accessDenied() throws Exception {
        mockMvc.perform(get("/api/admin"))
            .andExpect(status().is(AuthExceptionCode.ACCESS_DENIED.getStatus().value()))
            .andExpect(jsonPath("$.code").value(AuthExceptionCode.ACCESS_DENIED.getCode()))
            .andExpect(jsonPath("$.message").value(AuthExceptionCode.ACCESS_DENIED.getMessage()))
            .andReturn();
    }

    @Test
    @DisplayName("handleHttpRequestMethodNotSupportedException 테스트")
    @WithMockUser
    void testHandleHttpRequestMethodNotSupportedException() throws Exception {
        //GET 엔드포인트에 DELETE 메서드로 요청
        mockMvc.perform(delete("/test/http-request-method-not-supported-exception")
                .with(csrf()))
            .andExpect(status().isMethodNotAllowed()) // HTTP 405
            .andExpect(jsonPath("$.code").value("405"))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.validations").isEmpty());
    }

    @Test
    @DisplayName("handleHttpMessageNotReadableException 테스트")
    @WithMockUser
    void testHandleHttpMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/test/http-message-not-readable-exception")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)) // ContentType 설정만 하고 Body 없음
            .andExpect(status().isBadRequest()) // HTTP 400
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.validations").isEmpty());
    }

    @Test
    @DisplayName("handleMethodArgumentNotValidException 테스트")
    @WithMockUser
    void testHandleMethodArgumentNotValidException() throws Exception {
        TestRequestBody invalidBody = new TestRequestBody("1234", -1, List.of());

        mockMvc.perform(post("/test/method-argument-not-valid")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBody))) // JSON Request Body 설정
            .andExpect(status().isBadRequest()) // HTTP 400
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").value("입력 조건을 위반하였습니다."))
            .andExpect(jsonPath("$.validations", Matchers.hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("handleFTException 테스트")
    @WithMockUser
    void testHandleFTException() throws Exception {
        mockMvc.perform(get("/test/ft-exception"))
            .andExpect(status().isBadRequest()) // FTException의 기본 상태 코드 확인
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.message", Matchers.containsString("파라미터 누락.")))
            .andExpect(jsonPath("$.validations").isEmpty())
            .andReturn();
    }

    @Test
    @DisplayName("handleException 테스트")
    @WithMockUser
    void testHandleException() throws Exception {
        mockMvc.perform(get("/test/internal-server-error"))
            .andExpect(status().isInternalServerError()) // HTTP 500
            .andExpect(jsonPath("$.code").value("500"))
            .andExpect(jsonPath("$.message").value("서버 에러"))
            .andExpect(jsonPath("$.validations").isEmpty());
    }
}
