package io.jhchoe.familytree.core.invite.application.port.in;

import java.util.Objects;

/**
 * SaveFamilyInviteCommand는 초대 생성에 필요한 입력 데이터를 포함하는 커맨드 객체입니다.
 */
public record SaveFamilyInviteCommand(
    Long familyId,
    Long requesterId
) {
    public SaveFamilyInviteCommand {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(requesterId, "requesterId must not be null");

        if (familyId <= 0) {
            throw new IllegalArgumentException("familyId must be greater than 0");
        }
        if (requesterId <= 0) {
            throw new IllegalArgumentException("requesterId must be greater than 0");
        }
    }
}