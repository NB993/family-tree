package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.util.Objects;

/**
 * 구성원의 관계 정보를 변경하기 위한 Command입니다.
 *
 * @param familyId         가족 ID
 * @param memberId         대상 구성원 ID
 * @param currentUserId    현재 로그인한 사용자 ID (권한 체크용)
 * @param relationshipType 변경할 관계 타입
 * @param customRelationship CUSTOM 타입일 때 사용자 정의 관계명
 */
public record ModifyFamilyMemberRelationshipCommand(
    Long familyId,
    Long memberId,
    Long currentUserId,
    FamilyMemberRelationshipType relationshipType,
    String customRelationship
) {
    public ModifyFamilyMemberRelationshipCommand {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(memberId, "memberId must not be null");
        Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        Objects.requireNonNull(relationshipType, "relationshipType must not be null");
    }
}
