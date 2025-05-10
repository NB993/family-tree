package io.jhchoe.familytree.core.relationship.adapter.in.response;

import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationshipType;
import java.time.LocalDateTime;

/**
 * 가족 관계 정보 응답 DTO입니다.
 */
public record FamilyRelationshipResponse(
    Long id,
    Long familyId,
    Long fromMemberId,
    Long toMemberId,
    FamilyRelationshipType relationshipType,
    String relationshipDisplayName,
    String customRelationship,
    String description,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     * 
     * @param relationship 가족 관계 도메인 객체
     * @return 생성된 응답 DTO
     */
    public static FamilyRelationshipResponse from(FamilyRelationship relationship) {
        FamilyRelationshipType type = relationship.getRelationshipType();
        String displayName = type.getDisplayName();
        
        if (type == FamilyRelationshipType.CUSTOM && relationship.getCustomRelationship() != null) {
            displayName = relationship.getCustomRelationship();
        }
        
        return new FamilyRelationshipResponse(
            relationship.getId(),
            relationship.getFamilyId(),
            relationship.getFromMemberId(),
            relationship.getToMemberId(),
            relationship.getRelationshipType(),
            displayName,
            relationship.getCustomRelationship(),
            relationship.getDescription(),
            relationship.getCreatedAt(),
            relationship.getModifiedAt()
        );
    }
}
