package io.jhchoe.familytree.core.relationship.application.port.in;

/**
 * 가족 관계 정의를 위한 유스케이스 인터페이스입니다.
 */
public interface SaveFamilyMemberRelationshipUseCase {

    /**
     * 새로운 가족 관계를 정의하거나 기존 관계를 업데이트합니다.
     *
     * @param command 관계 정의에 필요한 정보가 담긴 커맨드 객체
     * @return 생성/수정된 관계의 ID
     */
    Long saveRelationship(SaveFamilyMemberRelationshipCommand command);
}
