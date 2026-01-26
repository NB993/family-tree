package io.jhchoe.familytree.core.user.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;

/**
 * User 프로필 수정 응답 DTO입니다.
 *
 * @param id           사용자 ID
 * @param name         이름
 * @param email        이메일
 * @param profileUrl   프로필 이미지 URL
 * @param birthday     생년월일
 * @param birthdayType 생년월일 유형
 */
public record ModifyUserResponse(
    Long id,
    String name,
    String email,
    String profileUrl,
    LocalDateTime birthday,
    BirthdayType birthdayType
) {
    /**
     * User 도메인 객체를 ModifyUserResponse로 변환합니다.
     *
     * @param user User 도메인 객체
     * @return 변환된 응답 DTO
     */
    public static ModifyUserResponse from(User user) {
        return new ModifyUserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getProfileUrl(),
            user.getBirthday(),
            user.getBirthdayType()
        );
    }
}