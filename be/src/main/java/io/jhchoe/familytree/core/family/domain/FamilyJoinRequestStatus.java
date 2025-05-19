package io.jhchoe.familytree.core.family.domain;

/**
 * Family 가입 신청의 상태를 나타내는 열거형입니다.
 */
public enum FamilyJoinRequestStatus {
    /**
     * 가입 신청이 대기 중인 상태
     */
    PENDING,
    
    /**
     * 가입 신청이 승인된 상태
     */
    APPROVED,
    
    /**
     * 가입 신청이 거절된 상태
     */
    REJECTED
}
