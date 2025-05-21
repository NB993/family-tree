package io.jhchoe.familytree.core.family.domain;

/**
 * Family 구성원의 역할을 나타내는 열거형입니다.
 * <p>
 * 역할별 권한 수준을 정의하고, 특정 역할이 다른 역할보다 높은 권한을 가지는지 비교할 수 있습니다.
 * </p>
 */
public enum FamilyMemberRole {
    OWNER,  // 소유자 (최상위 권한)
    ADMIN,  // 관리자
    MEMBER; // 일반 구성원
    
    /**
     * 현재 역할이 매개변수로 전달된 역할 이상의 권한을 가지고 있는지 확인합니다.
     * <p>
     * ordinal() 값이 작을수록 더 높은 권한을 의미합니다.
     * </p>
     * @param role 비교할 역할
     * @return 현재 역할이 매개변수로 전달된 역할 이상의 권한을 가지고 있으면 true, 그렇지 않으면 false
     */
    public boolean isAtLeast(FamilyMemberRole role) {
        return this.ordinal() <= role.ordinal();
    }
}