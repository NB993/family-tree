package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Family 가입 신청 요청을 위한 DTO 클래스입니다.
 */
public record SaveFamilyJoinRequestRequest(
    @NotNull(message = "가입할 Family ID는 필수입니다.")
    @Positive(message = "유효하지 않은 Family ID입니다.")
    Long familyId
) {
}
