package io.jhchoe.familytree.core.user.adapter.in.response;

import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;

/**
 * 사용자 조회 응답 DTO
 */
public record FindUserResponse(
    Long id,
    String email,
    String name,
    String profileUrl,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    /**
     * User 도메인 객체를 FindUserResponse로 변환
     *
     * @param user 변환할 User 도메인 객체
     * @return 변환된 응답 DTO
     */
    public static FindUserResponse from(User user) {
        return new FindUserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getProfileUrl(),
            user.getCreatedAt(),
            user.getModifiedAt()
        );
    }
}
