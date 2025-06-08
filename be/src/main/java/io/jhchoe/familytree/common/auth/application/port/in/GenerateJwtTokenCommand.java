package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.domain.FTUser;

import java.util.Objects;

/**
 * JWT 토큰 생성을 위한 Command 객체
 * OAuth2 로그인 성공 시 Access Token과 Refresh Token을 생성하기 위해 사용됩니다.
 *
 * @param user JWT 토큰에 포함될 사용자 정보
 */
public record GenerateJwtTokenCommand(
        FTUser user
) {
    /**
     * Command 객체를 생성합니다.
     *
     * @param user JWT 토큰에 포함될 사용자 정보 (null 불허)
     * @throws NullPointerException user가 null인 경우
     */
    public GenerateJwtTokenCommand {
        Objects.requireNonNull(user, "user must not be null");
    }

    /**
     * 사용자 ID를 반환합니다.
     *
     * @return 사용자 ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 사용자 이메일을 반환합니다.
     *
     * @return 사용자 이메일
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 사용자 이름을 반환합니다.
     *
     * @return 사용자 이름
     */
    public String getName() {
        return user.getName();
    }

    /**
     * 사용자 역할을 반환합니다.
     * Spring Security의 권한에서 ROLE_ 접두사를 제거하여 반환합니다.
     *
     * @return 사용자 역할
     */
    public String getRole() {
        return user.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
    }
}
