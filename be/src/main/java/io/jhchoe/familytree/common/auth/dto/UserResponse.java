package io.jhchoe.familytree.common.auth.dto;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 응답 DTO
 */
@Getter
@Builder
public class UserResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String authType;
    private final String provider;

    /**
     * 인증된 사용자 정보에서 프로필 응답 DTO를 생성합니다.
     *
     * @param user 인증된 사용자
     * @return 사용자 프로필 응답 DTO
     */
    public static UserResponse fromFTUser(FTUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .authType(user.getAuthType().name())
                .provider(user.getOAuth2Provider() != null ? user.getOAuth2Provider().name() : null)
                .build();
    }
}
