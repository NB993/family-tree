package io.jhchoe.familytree.core.user.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import org.springframework.http.HttpStatus;

/**
 * User 도메인에서 발생하는 예외의 코드를 정의하는 열거형입니다.
 */
public enum UserExceptionCode implements ExceptionCodeType {

    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserExceptionCode(
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
