package io.jhchoe.familytree.core.invite.application.port.in;

import java.util.Objects;

/**
 * FindFamilyInviteByIdQuery는 ID로 가족 초대를 조회하기 위한 쿼리 객체입니다.
 */
public record FindFamilyInviteByIdQuery(
    Long id
) {
    public FindFamilyInviteByIdQuery {
        Objects.requireNonNull(id, "id must not be null");
        
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }
}