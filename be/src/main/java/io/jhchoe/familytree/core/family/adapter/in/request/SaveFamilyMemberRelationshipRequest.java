package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 가족 관계 정의 요청 DTO입니다.
 */
public record SaveFamilyMemberRelationshipRequest(
    @NotNull(message = "가족 ID는 필수입니다")
    Long familyId,

    @NotNull(message = "관계를 정의할 대상 구성원 ID는 필수입니다")
    Long toMemberId,
    
    @NotNull(message = "관계 유형은 필수입니다")
    FamilyMemberRelationshipType relationshipType,
    
    @Size(max = 50, message = "사용자 정의 관계명은 50자 이내로 작성해주세요")
    String customRelationship,
    
    @Size(max = 200, message = "설명은 200자 이내로 작성해주세요")
    String description
) {
}
