package io.jhchoe.familytree.core.family.adapter.in.response;

/**
 * 가족 관계 타입 응답 DTO입니다.
 */
public record FamilyMemberRelationshipTypeResponse(
    String code,
    String displayName
) {
}
