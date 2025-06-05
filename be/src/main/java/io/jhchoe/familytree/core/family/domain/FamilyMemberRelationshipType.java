package io.jhchoe.familytree.core.family.domain;

/**
 * 가족 구성원 관계 타입을 정의하는 열거형 클래스입니다.
 * 각 가족 구성원 간의 관계를 나타냅니다.
 */
public enum FamilyMemberRelationshipType {
    // 직계 가족
    /**
     * 아버지
     */
    FATHER("아버지"),
    
    /**
     * 어머니
     */
    MOTHER("어머니"), 
    
    /**
     * 아들
     */
    SON("아들"),
    
    /**
     * 딸
     */
    DAUGHTER("딸"),
    
    // 조부모/손자
    /**
     * 할아버지
     */
    GRANDFATHER("할아버지"),
    
    /**
     * 할머니
     */
    GRANDMOTHER("할머니"),
    
    /**
     * 손자
     */
    GRANDSON("손자"),
    
    /**
     * 손녀
     */
    GRANDDAUGHTER("손녀"),
    
    // 형제자매
    /**
     * 형/오빠
     */
    ELDER_BROTHER("형"),
    
    /**
     * 누나/언니
     */
    ELDER_SISTER("누나/언니"),
    
    /**
     * 남동생
     */
    YOUNGER_BROTHER("남동생"),
    
    /**
     * 여동생
     */
    YOUNGER_SISTER("여동생"),
    
    // 배우자
    /**
     * 남편
     */
    HUSBAND("남편"),
    
    /**
     * 아내
     */
    WIFE("아내"),
    
    // 친척
    /**
     * 삼촌/외삼촌
     */
    UNCLE("삼촌/외삼촌"),
    
    /**
     * 고모/이모
     */
    AUNT("고모/이모"),
    
    /**
     * 조카 (남)
     */
    NEPHEW("조카"),
    
    /**
     * 조카딸 (여)
     */
    NIECE("조카딸"),
    
    /**
     * 사촌
     */
    COUSIN("사촌"),
    
    // 사용자 정의
    /**
     * 직접 입력
     */
    CUSTOM("직접 입력");
    
    private final String displayName;
    
    FamilyMemberRelationshipType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
