package io.jhchoe.familytree.common.exception;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ControllerStub {

    @GetMapping("/test/ft-exception")
    public void throwFTException() {
        throw new FTException(CommonExceptionCode.MISSING_PARAMETER, "testParameter");
    }

    @GetMapping("/test/illegal-argument")
    public void throwIllegalArgumentException() {
        throw new IllegalArgumentException("Invalid argument");
    }

    @GetMapping("/test/internal-server-error")
    public void throwInternalServerError() {
        throw new RuntimeException("서버 에러");
    }

    @GetMapping("/test/http-request-method-not-supported-exception")
    public void unsupportedHttpRequestMethod(@AuthFTUser FTUser ftUser) {
    }

    // request body가 필요한 엔드포인트에 body를 입력하지 않고 요청 시 발생하는 예외 테스트
    @PostMapping("/test/http-message-not-readable-exception")
    public void throwHttpMessageNotReadableException(@RequestBody @Valid TestRequestBody body) {
    }

    // request body의 유효성 검사를 통과하지 못한 경우(@Valid) 발생하는 예외 테스트
    @PostMapping("/test/method-argument-not-valid")
    public void handleMethodArgumentNotValid(@AuthFTUser FTUser ftUser, @RequestBody @Valid TestRequestBody requestBody) {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestRequestBody {
        @NotEmpty
        @Size(max = 3)
        private String string;
        @NotNull
        @Positive
        private Integer integer;
        @NotEmpty
        private List<String> list;
    }
}
