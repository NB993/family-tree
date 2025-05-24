package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Family 가입 신청 처리를 위한 요청 DTO 클래스입니다.
 */
public record ProcessFamilyJoinRequestRequest(
    @NotNull(message = "처리 상태는 필수입니다.")
    FamilyJoinRequestStatus status,

    String message
) {
}
