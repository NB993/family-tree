package io.jhchoe.familytree.core.user.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;

/**
 * 사용자 조회 응답 DTO
 *
 * @param id           사용자 ID
 * @param email        이메일
 * @param name         이름
 * @param profileUrl   프로필 이미지 URL
 * @param birthday     생년월일
 * @param birthdayType 생년월일 유형 (양력/음력)
 * @param createdAt    생성일시
 * @param modifiedAt   수정일시
 */
public record FindUserResponse(
    Long id,
    String email,
    String name,
    String profileUrl,
    LocalDateTime birthday,
    BirthdayType birthdayType,
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
            user.getBirthday(),
            user.getBirthdayType(),
            user.getCreatedAt(),
            user.getModifiedAt()
        );
    }
}
