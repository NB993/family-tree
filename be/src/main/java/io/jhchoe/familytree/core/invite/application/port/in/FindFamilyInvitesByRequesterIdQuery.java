package io.jhchoe.familytree.core.invite.application.port.in;

import java.util.Objects;

/**
 * FindFamilyInvitesByRequesterIdQuery는 요청자 ID로 모든 초대를 조회하기 위한 쿼리 객체입니다.
 */
public record FindFamilyInvitesByRequesterIdQuery(
    Long requesterId
) {
    public FindFamilyInvitesByRequesterIdQuery {
        Objects.requireNonNull(requesterId, "requesterId must not be null");
        
        if (requesterId <= 0) {
            throw new IllegalArgumentException("requesterId must be greater than 0");
        }
    }
}