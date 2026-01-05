package io.jhchoe.familytree.common.auth.dto;

import io.jhchoe.familytree.common.auth.domain.FTUser;

/**
 * 사용자 응답 DTO
 */
public record UserResponse(
    Long id,
    String name,
    String email,
    String provider
) {
    /**
     * 인증된 사용자 정보에서 프로필 응답 DTO를 생성합니다.
     *
     * @param user 인증된 사용자
     * @return 사용자 프로필 응답 DTO
     */
    public static UserResponse fromFTUser(FTUser user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getOAuth2Provider() != null ? user.getOAuth2Provider().name() : null
        );
    }
}
