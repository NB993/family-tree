package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.BirthdayType;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 구성원 기본 정보 수정을 위한 요청 DTO입니다.
 */
public record ModifyFamilyMemberInfoRequest(
    @NotBlank(message = "이름은 필수입니다.")
    String name,
    LocalDateTime birthday,
    BirthdayType birthdayType
) {
}
