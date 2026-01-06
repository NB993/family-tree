package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 가족 구성원 수동 등록 요청 DTO입니다.
 *
 * @param name               구성원 이름 (필수)
 * @param birthday           생년월일 (선택)
 * @param relationshipType   관계 유형 (선택)
 * @param customRelationship 사용자 정의 관계명 (CUSTOM 타입인 경우 필수)
 */
public record SaveFamilyMemberRequest(
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    String name,

    LocalDateTime birthday,

    FamilyMemberRelationshipType relationshipType,

    @Size(max = 50, message = "사용자 정의 관계명은 50자를 초과할 수 없습니다")
    String customRelationship
) {
}
