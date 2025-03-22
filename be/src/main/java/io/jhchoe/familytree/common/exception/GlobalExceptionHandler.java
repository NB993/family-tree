package io.jhchoe.familytree.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    
    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    /**
     * 지원하지 않는 HTTP 메소드 요청 예외
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        ErrorResponse errorResponse = ErrorResponse.methodNotAllowed(e);
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(errorResponse);
    }

    /**
     * 요청 body 파싱, 형변환 불가 예외
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    /**
     * @Valid 위반 예외
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    /**
     * DB 제약 조건 위반 예외
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    /**
     * 비즈니스 예외
     */
    @ExceptionHandler(FTException.class)
    public ResponseEntity<ErrorResponse> handleFTException(final FTException e) {
        ErrorResponse response = ErrorResponse.commonException(e);
        return ResponseEntity
            .status(e.getStatus())
            .body(response);
    }

    /**
     * 서버 측 에러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("INTERNAL_SERVER_ERROR: ", e);

        ErrorResponse errorResponse = ErrorResponse.internalServerError(e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }
}
