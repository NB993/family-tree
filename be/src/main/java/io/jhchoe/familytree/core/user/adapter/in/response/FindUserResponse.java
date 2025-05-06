package io.jhchoe.familytree.core.user.adapter.in.response;

import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;

/**
 * 사용자 조회 응답을 위한 DTO 클래스입니다.
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
     * User 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param user 변환할 User 도메인 객체
     * @return 생성된 FindUserResponse 객체
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
