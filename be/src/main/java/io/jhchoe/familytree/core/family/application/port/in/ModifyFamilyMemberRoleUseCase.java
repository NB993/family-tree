package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 구성원의 역할을 변경하는 유스케이스 인터페이스입니다.
 */
public interface ModifyFamilyMemberRoleUseCase {
    
    /**
     * 구성원의 역할을 변경합니다.
     *
     * @param command 역할 변경 명령 객체
     * @return 업데이트된 구성원의 ID
     */
    Long modifyRole(ModifyFamilyMemberRoleCommand command);
}
