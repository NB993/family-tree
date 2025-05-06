package io.jhchoe.familytree.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        log.warn("IllegalArgumentException: [TraceId: {}] [Path: {}] [Method: {}] [Error: {}]", 
            errorResponse.getTraceId(),
            request.getRequestURI(),
            request.getMethod(),
            e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }
    
    /**
     * 지원하지 않는 HTTP 메소드 요청 예외
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.methodNotAllowed(e);
        log.warn("Method Not Allowed: [TraceId: {}] [Path: {}] [Method: {}] [Error: {}]", 
            errorResponse.getTraceId(),
            request.getRequestURI(),
            request.getMethod(),
            e.getMessage());
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(errorResponse);
    }
    
    /**
     * 요청 body 파싱, 형변환 불가 예외
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        log.warn("Message Not Readable: [TraceId: {}] [Path: {}] [Method: {}] [Error: {}]", 
            errorResponse.getTraceId(),
            request.getRequestURI(),
            request.getMethod(),
            e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }
    
    /**
     * @Valid 위반 예외
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        log.warn("Validation Failed: [TraceId: {}] [Path: {}] [Method: {}] [Error: {}]", 
            errorResponse.getTraceId(),
            request.getRequestURI(),
            request.getMethod(),
            e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }
    
    /**
     * DB 제약 조건 위반 예외
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        log.warn("Constraint Violation: [TraceId: {}] [Path: {}] [Method: {}] [Error: {}]", 
            errorResponse.getTraceId(),
            request.getRequestURI(),
            request.getMethod(),
            e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    /**
     * 요청된 필수 매개변수 누락 예외
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.badRequest(e);
        log.warn("Missing Request Parameter: [TraceId: {}] [Path: {}] [Method: {}] [Parameter: {}] [Error: {}]", 
            errorResponse.getTraceId(),
            request.getRequestURI(),
            request.getMethod(),
            e.getParameterName(),
            e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    /**
     * 비즈니스 예외
     */
    @ExceptionHandler(FTException.class)
    public ResponseEntity<ErrorResponse> handleFTException(final FTException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.commonException(e);
        // FTException에는 현재로서는 400번대 예외만 던질 예정
        if (e.getStatus().is4xxClientError()) {
            log.warn("Business Exception: [TraceId: {}] [Path: {}] [Code: {}] [Message: {}]", 
                response.getTraceId(), 
                request.getRequestURI(), 
                e.getCode(), 
                e.getMessage());
        }
        
        return ResponseEntity
            .status(e.getStatus())
            .body(response);
    }

    /**
     * 서버 측 에러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.internalServerError(e);
        log.error("INTERNAL_SERVER_ERROR: [TraceId: {}] [Path: {}] [Method: {}] [Error: {}]", 
            errorResponse.getTraceId(), 
            request.getRequestURI(), 
            request.getMethod(), 
            e.getMessage(), 
            e);
    
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }
}
