package io.jhchoe.familytree.core.invite.domain;

/**
 * FamilyInviteStatus는 가족 초대의 상태를 나타내는 열거형입니다.
 */
public enum FamilyInviteStatus {
    /**
     * 활성 상태 - 초대가 사용 가능한 상태입니다.
     */
    ACTIVE,
    
    /**
     * 만료 상태 - 초대가 만료된 상태입니다.
     */
    EXPIRED,
    
    /**
     * 완료 상태 - 초대가 완료된 상태입니다.
     */
    COMPLETED
}