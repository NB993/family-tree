package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.util.Objects;
import lombok.Getter;

/**
 * Family 구성원 상태 변경을 위한 명령 클래스입니다.
 */
@Getter
public class ModifyFamilyMemberStatusCommand {
    
    private final Long familyId;
    private final Long memberId;
    private final Long currentUserId;
    private final FamilyMemberStatus newStatus;
    private final String reason;
    
    /**
     * 구성원 상태 변경 명령 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param memberId      변경할 구성원 ID
     * @param currentUserId 현재 요청하는 사용자 ID
     * @param newStatus     새 상태
     * @param reason        상태 변경 사유
     */
    public ModifyFamilyMemberStatusCommand(
        Long familyId,
        Long memberId,
        Long currentUserId,
        FamilyMemberStatus newStatus,
        String reason
    ) {
        this.familyId = Objects.requireNonNull(familyId, "familyId must not be null");
        this.memberId = Objects.requireNonNull(memberId, "memberId must not be null");
        this.currentUserId = Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        this.newStatus = Objects.requireNonNull(newStatus, "newStatus must not be null");
        this.reason = reason; // reason은 선택사항
        
        validate();
    }
    
    /**
     * 명령 객체의 유효성을 검증합니다.
     */
    private void validate() {
        if (familyId <= 0) {
            throw new IllegalArgumentException("familyId must be positive");
        }
        
        if (memberId <= 0) {
            throw new IllegalArgumentException("memberId must be positive");
        }
        
        if (currentUserId <= 0) {
            throw new IllegalArgumentException("currentUserId must be positive");
        }
    }
}
