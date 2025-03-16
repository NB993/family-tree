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

    public static <T> ApiResponse<T> error(final io.jhchoe.familytree.common.exception.ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }
}
