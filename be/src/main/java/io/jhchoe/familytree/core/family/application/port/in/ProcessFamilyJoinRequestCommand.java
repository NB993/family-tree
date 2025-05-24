package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import java.util.Objects;
import lombok.Getter;

/**
 * Family 가입 신청 처리를 위한 커맨드 객체입니다.
 */
@Getter
public class ProcessFamilyJoinRequestCommand {
    private final Long familyId;
    private final Long requestId;
    private final FamilyJoinRequestStatus status;
    private final String message;
    private final Long currentUserId;

    /**
     * ProcessFamilyJoinRequestCommand 객체를 생성하는 생성자입니다.
     *
     * @param familyId  Family ID
     * @param requestId 가입 신청 ID
     * @param status    처리할 상태 (APPROVED 또는 REJECTED)
     * @param message   처리 사유 (선택사항)
     * @param currentUserId 현재 사용자 ID
     */
    public ProcessFamilyJoinRequestCommand(
        final Long familyId,
        final Long requestId,
        final FamilyJoinRequestStatus status,
        final String message,
        final Long currentUserId
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        
        if (status != FamilyJoinRequestStatus.APPROVED && status != FamilyJoinRequestStatus.REJECTED) {
            throw new IllegalArgumentException("status must be APPROVED or REJECTED");
        }
        
        this.familyId = familyId;
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.currentUserId = currentUserId;
    }
}
