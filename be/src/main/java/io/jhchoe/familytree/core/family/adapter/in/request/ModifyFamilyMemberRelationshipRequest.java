package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 구성원 관계 변경 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class ModifyFamilyMemberRelationshipRequest {

    @NotNull(message = "관계 타입은 필수입니다.")
    private FamilyMemberRelationshipType relationshipType;

    @Size(max = 50, message = "사용자 정의 관계명은 50자 이내로 작성해주세요.")
    private String customRelationship;

    public ModifyFamilyMemberRelationshipRequest(
        FamilyMemberRelationshipType relationshipType,
        String customRelationship
    ) {
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
    }
}
