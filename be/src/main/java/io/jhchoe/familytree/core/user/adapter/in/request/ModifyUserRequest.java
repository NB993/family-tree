package io.jhchoe.familytree.core.user.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import java.time.LocalDateTime;

/**
 * User 프로필 수정 요청 DTO입니다.
 *
 * @param name         사용자 이름 (필수)
 * @param birthday     생년월일 (nullable)
 * @param birthdayType 생년월일 유형 (nullable)
 */
public record ModifyUserRequest(
    String name,
    LocalDateTime birthday,
    BirthdayType birthdayType
) {
}