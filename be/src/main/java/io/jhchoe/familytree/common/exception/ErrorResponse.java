package io.jhchoe.familytree.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final String traceId;
    private List<FieldError<?>> validations = new ArrayList<>();

    private ErrorResponse(final String code, final String message) {
        this.code = code;
        this.message = message;
        this.traceId = generateTraceId();
    }

    private ErrorResponse(
        final String code,
        final String message,
        final List<FieldError<?>> validations
    ) {
        this.code = code;
        this.message = message;
        this.traceId = generateTraceId();
        this.validations = validations;
    }

    /**
     * 모든 오류 응답에 사용할 고유한 추적 ID를 생성합니다.
     * @return UUID 기반의 고유 문자열
     */
    private static String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    public static ErrorResponse badRequest(final IllegalArgumentException e) {
        return new ErrorResponse("400", e.getMessage());
    }

    /**
     * 지원하지 않는 HTTP 메소드 요청 예외
     */
    public static ErrorResponse methodNotAllowed(final HttpRequestMethodNotSupportedException e) {
        return new ErrorResponse("405", e.getMessage());
    }

    /**
     * 요청 body 파싱, 형변환 불가 예외
     */
    public static ErrorResponse badRequest(final HttpMessageNotReadableException e) {
        return new ErrorResponse("400", e.getMessage());
    }

    /**
     * @Valid 위반 예외
     */
    public static ErrorResponse badRequest(final MethodArgumentNotValidException e) {
        final BindingResult bindingResult = e.getBindingResult();
        final List<FieldError<?>> fieldErrors = bindingResult.getFieldErrors().stream()
            .map(FieldError::of)
            .collect(Collectors.toList());

        return new ErrorResponse("400", "입력 조건을 위반하였습니다.", fieldErrors);
    }

    /**
     * DB 제약조건 위반 예외
     */
    public static ErrorResponse badRequest(final ConstraintViolationException e) {
        final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        final List<FieldError<?>> fieldErrors = violations.stream()
            .map(FieldError::of)
            .collect(Collectors.toList());

        return new ErrorResponse("400", "입력 조건을 위반하였습니다.", fieldErrors);
    }

    /**
     * 요청된 필수 매개변수 누락 예외
     */
    public static ErrorResponse badRequest(final MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        String errorMessage = String.format("'%s' 파라미터가 누락되었습니다.", parameterName);
        return new ErrorResponse("400", errorMessage);
    }

    /**
     * 비즈니스 예외
     */
    public static ErrorResponse commonException(final FTException e) {
        return new ErrorResponse(e.getCode(), e.getMessage());
    }

    /**
     * 서버 측 에러
     */
    public static ErrorResponse internalServerError(final Exception e) {
        return new ErrorResponse("500", e.getMessage());
    }

    /**
     * 제약 조건을 위반한 필드의 예외 정보
     * @param field 필드명
     * @param message 예외 메시지
     * @param value 입력받은 값
     * @param <T> value 타입
     */
    private record FieldError<T>(
        String field,
        String message,
        T value
    ) {
        public static <T> FieldError<T> of(final org.springframework.validation.FieldError error) {
            return new FieldError<T>(
                error.getField(),
                error.getDefaultMessage(),
                (T) error.getRejectedValue()
            );
        }

        public static <T> FieldError<T> of(final ConstraintViolation<?> violation) {
            return new FieldError<>(
                violation.getPropertyPath().toString(),
                violation.getMessage(),
                (T) violation.getInvalidValue()
            );
        }
    }
}
