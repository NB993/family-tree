package io.jhchoe.familytree.core.family.application.port.out;


import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;

/**
 * 가족 관계 저장 포트 인터페이스입니다.
 */
public interface SaveFamilyMemberRelationshipPort {

    /**
     * 가족 관계 정보를 저장합니다.
     *
     * @param relationship 저장할 가족 관계 도메인 객체
     * @return 저장된 가족 관계의 ID
     */
    Long saveRelationship(FamilyMemberRelationship relationship);
}
