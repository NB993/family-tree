package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.time.LocalDateTime;

/**
 * 가족 관계 정보 응답 DTO입니다.
 */
public record FamilyMemberRelationshipResponse(
    Long id,
    Long familyId,
    Long fromMemberId,
    Long toMemberId,
    FamilyMemberRelationshipType relationshipType,
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
    public static FamilyMemberRelationshipResponse from(FamilyMemberRelationship relationship) {
        FamilyMemberRelationshipType type = relationship.getRelationshipType();
        String displayName = type.getDisplayName();
        
        if (type == FamilyMemberRelationshipType.CUSTOM && relationship.getCustomRelationship() != null) {
            displayName = relationship.getCustomRelationship();
        }
        
        return new FamilyMemberRelationshipResponse(
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
