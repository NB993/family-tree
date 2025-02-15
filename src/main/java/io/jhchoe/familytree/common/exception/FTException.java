package io.jhchoe.familytree.common.exception;

import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import org.springframework.http.HttpStatus;

public class FTException extends RuntimeException {

    public static final FTException UNAUTHORIZED = new FTException(AuthExceptionCode.UNAUTHORIZED);
    public static final FTException ACCESS_DENIED = new FTException(AuthExceptionCode.ACCESS_DENIED);

    public static final FTException MISSING_PARAMETER = new FTException(CommonExceptionCode.MISSING_PARAMETER);
    public static final FTException INVALID_PARAMETER = new FTException(CommonExceptionCode.INVALID_PARAMETER);
    public static final FTException NOT_FOUND = new FTException(CommonExceptionCode.NOT_FOUND);
    public static final FTException DUPLICATED = new FTException(CommonExceptionCode.DUPLICATED);
    public static final FTException ENUM_CONVERT = new FTException(CommonExceptionCode.ENUM_CONVERT);

    private static final String PARAMETER_MEESAGE_FORMAT = "%s -> [parameter]: %s";
    private final ExceptionCodeType exceptionCodeType;

    public FTException(final ExceptionCodeType exceptionCodeType) {
        super(exceptionCodeType.getMessage());
        this.exceptionCodeType = exceptionCodeType;
    }

    public FTException(final ExceptionCodeType exceptionCodeType, final String parameterName) {
        super(String.format(PARAMETER_MEESAGE_FORMAT, exceptionCodeType.getMessage(), parameterName));
        this.exceptionCodeType = exceptionCodeType;
    }

    //todo 발생한 예외까지 받을 수 있는 생성자 추가

    public String getCode() {
        return this.exceptionCodeType.getCode();
    }

    public String getMessage() {
        return super.getMessage();
    }

    public HttpStatus getStatus() {
        return this.exceptionCodeType.getStatus();
    }
}
