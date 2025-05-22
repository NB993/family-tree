package io.jhchoe.familytree.common.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.jhchoe.familytree.common.exception.ErrorResponse;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public static final ApiResponse<Void> OK = new ApiResponse<>(true, null, null);

    private ApiResponse(
        final boolean success,
        final T data,
        final ErrorResponse error
    ) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(final T data) {
        assert !(data instanceof ErrorResponse): "ErrorResponse는 입력할 수 없습니다.";

        return new ApiResponse<>(true, data, null);
    }

    /**
     * 성공 응답을 생성합니다 (ok 별칭).
     *
     * @param data 응답 데이터
     * @return 성공 ApiResponse
     */
    public static <T> ApiResponse<T> ok(final T data) {
        return success(data);
    }

    /**
     * 성공 응답을 생성합니다 (데이터 없음).
     *
     * @return 성공 ApiResponse
     */
    public static ApiResponse<Void> ok() {
        return OK;
    }

    /**
     * 생성 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @return 생성 성공 ApiResponse
     */
    public static <T> ApiResponse<T> created(final T data) {
        return success(data);
    }

    public static <T> ApiResponse<T> error(final io.jhchoe.familytree.common.exception.ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }
}
