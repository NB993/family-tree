package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 구성원의 상태를 변경하는 유스케이스 인터페이스입니다.
 */
public interface ModifyFamilyMemberStatusUseCase {
    
    /**
     * 구성원의 상태를 변경합니다.
     *
     * @param command 상태 변경 명령 객체
     * @return 업데이트된 구성원의 ID
     */
    Long modifyStatus(ModifyFamilyMemberStatusCommand command);
}
