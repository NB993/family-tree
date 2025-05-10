package io.jhchoe.familytree.core.relationship.domain;

/**
 * 가족 관계 타입을 정의하는 열거형 클래스입니다.
 * 각 가족 구성원 간의 관계를 나타냅니다.
 */
public enum FamilyRelationshipType {
    /**
     * 부모-자녀 관계 (부모 → 자녀)
     */
    PARENT("부모"),
    
    /**
     * 자녀-부모 관계 (자녀 → 부모)
     */
    CHILD("자녀"),
    
    /**
     * 배우자 관계
     */
    SPOUSE("배우자"),
    
    /**
     * 형제자매 관계
     */
    SIBLING("형제자매"),
    
    /**
     * 삼촌/이모 관계 (삼촌/이모 → 조카)
     */
    UNCLE_AUNT("삼촌/이모"),
    
    /**
     * 조카 관계 (조카 → 삼촌/이모)
     */
    NEPHEW_NIECE("조카"),
    
    /**
     * 사촌 관계
     */
    COUSIN("사촌"),
    
    /**
     * 조부모 관계 (조부모 → 손자녀)
     */
    GRANDPARENT("조부모"),
    
    /**
     * 손자녀 관계 (손자녀 → 조부모)
     */
    GRANDCHILD("손자녀"),
    
    /**
     * 그 외 관계 (사용자가 직접 정의)
     */
    CUSTOM("사용자 정의");
    
    private final String displayName;
    
    FamilyRelationshipType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
