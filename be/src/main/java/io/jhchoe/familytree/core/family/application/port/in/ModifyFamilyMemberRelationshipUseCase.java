package io.jhchoe.familytree.core.family.application.port.in;

/**
 * 구성원의 관계 정보를 변경하는 UseCase입니다.
 */
public interface ModifyFamilyMemberRelationshipUseCase {

    /**
     * 구성원의 관계 정보를 변경합니다.
     *
     * @param command 관계 변경 명령
     * @return 변경된 구성원의 ID
     */
    Long modify(ModifyFamilyMemberRelationshipCommand command);
}
