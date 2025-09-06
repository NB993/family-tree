package io.jhchoe.familytree.core.invite.adapter.in.response;

import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;

import java.time.LocalDateTime;

/**
 * 초대 정보 조회 응답 DTO입니다.
 */
public record FindFamilyInviteResponse(
    Long id,
    Long requesterId,
    String inviteCode,
    LocalDateTime expiresAt,
    String status,
    LocalDateTime createdAt
) {
    /**
     * FamilyInvite 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param familyInvite 초대 도메인 객체
     * @return 응답 DTO
     */
    public static FindFamilyInviteResponse from(final FamilyInvite familyInvite) {
        return new FindFamilyInviteResponse(
            familyInvite.getId(),
            familyInvite.getRequesterId(),
            familyInvite.getInviteCode(),
            familyInvite.getExpiresAt(),
            familyInvite.getStatus().name(),
            familyInvite.getCreatedAt()
        );
    }
}