package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import java.time.LocalDateTime;

/**
 * Family 가입 신청 목록 조회를 위한 응답 DTO 클래스입니다.
 */
public record FindFamilyJoinRequestResponse(
    Long id,
    Long requesterId,
    String status,
    LocalDateTime createdAt
) {
    /**
     * FamilyJoinRequest 도메인 객체로부터 응답 객체를 생성합니다.
     *
     * @param joinRequest 가입 신청 도메인 객체
     * @return 생성된 응답 객체
     */
    public static FindFamilyJoinRequestResponse from(final FamilyJoinRequest joinRequest) {
        return new FindFamilyJoinRequestResponse(
            joinRequest.getId(),
            joinRequest.getRequesterId(),
            joinRequest.getStatus().name(),
            joinRequest.getCreatedAt()
        );
    }
}
