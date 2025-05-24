package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import java.time.LocalDateTime;

/**
 * Family 가입 신청 처리 결과를 위한 응답 DTO 클래스입니다.
 */
public record ProcessFamilyJoinRequestResponse(
    Long id,
    FamilyJoinRequestStatus status,
    Long modifiedBy,
    LocalDateTime modifiedAt
) {
    /**
     * 처리된 FamilyJoinRequest 도메인 객체로부터 응답 객체를 생성합니다.
     *
     * @param familyJoinRequest 처리된 가입 신청 도메인 객체
     * @return 생성된 응답 객체
     */
    public static ProcessFamilyJoinRequestResponse from(final FamilyJoinRequest familyJoinRequest) {
        return new ProcessFamilyJoinRequestResponse(
            familyJoinRequest.getId(),
            familyJoinRequest.getStatus(),
            familyJoinRequest.getModifiedBy(),
            familyJoinRequest.getModifiedAt()
        );
    }
}
