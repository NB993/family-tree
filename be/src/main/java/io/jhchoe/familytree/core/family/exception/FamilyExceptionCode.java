package io.jhchoe.familytree.core.family.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import org.springframework.http.HttpStatus;

/**
 * Family 도메인에서 발생하는 예외의 코드를 정의하는 열거형입니다.
 */
public enum FamilyExceptionCode implements ExceptionCodeType {

    NOT_FAMILY_MEMBER("F001", "가족 구성원만 접근할 수 있습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    FamilyExceptionCode(
        String code,
        String message,
        HttpStatus status
    ) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
