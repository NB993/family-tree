package io.jhchoe.familytree.core.family.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import org.springframework.http.HttpStatus;

/**
 * Family 도메인에서 발생하는 예외의 코드를 정의하는 열거형입니다.
 */
public enum FamilyExceptionCode implements ExceptionCodeType {

    NOT_FAMILY_MEMBER("F001", "가족 구성원만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),
    FAMILY_NOT_FOUND("F002", "존재하지 않는 패밀리입니다.", HttpStatus.NOT_FOUND),
    ALREADY_JOINED_FAMILY("F003", "이미 가입된 패밀리입니다.", HttpStatus.CONFLICT),
    JOIN_REQUEST_ALREADY_PENDING("F004", "가입 신청을 처리중이에요.", HttpStatus.CONFLICT),
    EXCEEDED_FAMILY_JOIN_LIMIT("F005", "최대 패밀리 가입 수를 초과했습니다.", HttpStatus.BAD_REQUEST),
    JOIN_REQUEST_REJECTED("F006", "가입 신청이 불가능합니다.", HttpStatus.FORBIDDEN);

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
