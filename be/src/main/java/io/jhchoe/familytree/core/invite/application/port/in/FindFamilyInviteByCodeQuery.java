package io.jhchoe.familytree.core.invite.application.port.in;

import java.util.Objects;

/**
 * FindFamilyInviteByCodeQuery는 초대 코드로 가족 초대를 조회하기 위한 쿼리 객체입니다.
 */
public record FindFamilyInviteByCodeQuery(
    String inviteCode
) {
    public FindFamilyInviteByCodeQuery {
        Objects.requireNonNull(inviteCode, "inviteCode must not be null");
        
        if (inviteCode.isBlank()) {
            throw new IllegalArgumentException("inviteCode must not be blank");
        }
    }
}