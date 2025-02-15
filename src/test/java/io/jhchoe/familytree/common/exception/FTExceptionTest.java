package io.jhchoe.familytree.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FTExceptionTest {

    private ExceptionCodeType baseException;

    @RequiredArgsConstructor
    @Getter
    enum TestExceptionCode implements ExceptionCodeType {
        TEST_BAD_REQUEST("T999", "테스트 메시지", HttpStatus.BAD_REQUEST);

        private final String code;
        private final String message;
        private final HttpStatus status;
    }

    @Test
    @DisplayName("FTException(BaseExceptionType) 생성자 테스트")
    void testConstructorWithBaseException() {
        FTException ftException = new FTException(TestExceptionCode.TEST_BAD_REQUEST);
        assertThat(ftException.getCode()).isEqualTo("T999");
        assertThat(ftException.getMessage()).isEqualTo("테스트 메시지");
        assertThat(ftException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FTException(BaseExceptionType, String) 생성자 테스트")
    void testConstructorWithBaseExceptionAndParameter() {
        String parameterName = "username";
        FTException exception = new FTException(TestExceptionCode.TEST_BAD_REQUEST, parameterName);

        assertThat(exception.getMessage())
            .isEqualTo("테스트 메시지 -> [parameter]: username");
        assertThat(exception.getCode()).isEqualTo("T999");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

