package io.jhchoe.familytree.core.family.domain;

import java.util.Objects;

/**
 * 가족트리에서 구성원 간의 관계를 나타내는 경량화된 객체입니다.
 * 트리 시각화에 필요한 최소한의 관계 정보만 포함합니다.
 */
public record FamilyTreeRelation(
    Long toMemberId,
    FamilyMemberRelationshipType relationshipType,
    String customRelationship,
    String displayName
) {
    /**
     * FamilyTreeRelation 객체를 생성합니다.
     *
     * @param toMemberId 관계 대상 구성원 ID
     * @param relationshipType 관계 유형
     * @param customRelationship 사용자 정의 관계명 (CUSTOM 타입인 경우)
     * @param displayName 화면에 표시될 관계명
     */
    public FamilyTreeRelation {
        Objects.requireNonNull(toMemberId, "toMemberId must not be null");
        Objects.requireNonNull(relationshipType, "relationshipType must not be null");
        Objects.requireNonNull(displayName, "displayName must not be null");
    }
    
    /**
     * FamilyMemberRelationship 도메인 객체로부터 FamilyTreeRelation을 생성합니다.
     *
     * @param relationship 가족 구성원 관계 도메인 객체
     * @return 생성된 FamilyTreeRelation 객체
     */
    public static FamilyTreeRelation from(FamilyMemberRelationship relationship) {
        Objects.requireNonNull(relationship, "relationship must not be null");
        
        String displayName = determineDisplayName(
            relationship.getRelationshipType(), 
            relationship.getCustomRelationship()
        );
        
        return new FamilyTreeRelation(
            relationship.getToMemberId(),
            relationship.getRelationshipType(),
            relationship.getCustomRelationship(),
            displayName
        );
    }
    
    /**
     * 관계 유형과 사용자 정의 관계명을 바탕으로 화면 표시용 관계명을 결정합니다.
     *
     * @param relationshipType 관계 유형
     * @param customRelationship 사용자 정의 관계명
     * @return 화면에 표시될 관계명
     */
    private static String determineDisplayName(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        if (relationshipType == FamilyMemberRelationshipType.CUSTOM) {
            return customRelationship != null ? customRelationship : "사용자 정의";
        }
        
        return relationshipType.getDisplayName();
    }
    
    /**
     * 사용자 정의 관계인지 확인합니다.
     *
     * @return 사용자 정의 관계이면 true, 그렇지 않으면 false
     */
    public boolean isCustomRelationship() {
        return relationshipType == FamilyMemberRelationshipType.CUSTOM;
    }
}
