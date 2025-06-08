package io.jhchoe.familytree.common.auth.dto;

/**
 * 로그아웃 응답 DTO입니다.
 *
 * @param success 로그아웃 성공 여부
 * @param message 응답 메시지
 */
public record LogoutResponse(
    boolean success,
    String message
) {

    /**
     * 로그아웃 성공 응답을 생성합니다.
     *
     * @return 성공 응답
     */
    public static LogoutResponse createSuccess() {
        return new LogoutResponse(true, "로그아웃이 성공적으로 완료되었습니다.");
    }

    /**
     * 로그아웃 실패 응답을 생성합니다.
     *
     * @param message 실패 메시지
     * @return 실패 응답
     */
    public static LogoutResponse createFailure(String message) {
        return new LogoutResponse(false, message);
    }
}
