package io.jhchoe.familytree.common.exception;

import org.springframework.http.HttpStatus;

public class FTException extends RuntimeException {

    private static final String PARAMETER_MEESAGE_FORMAT = "%s -> [parameter]: %s";
    private final BaseExceptionType baseException;

    public FTException(final BaseExceptionType baseException) {
        super(baseException.getMessage());
        this.baseException = baseException;
    }

    public FTException(final BaseExceptionType baseException, final String parameterName) {
        super(String.format(PARAMETER_MEESAGE_FORMAT, baseException.getMessage(), parameterName));
        this.baseException = baseException;
    }

    public String getCode() {
        return this.baseException.getCode();
    }

    public String getMessage() {
        return super.getMessage();
    }

    public HttpStatus getStatus() {
        return this.baseException.getStatus();
    }
}
