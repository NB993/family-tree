package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 구성원의 기본 정보(이름, 생일, 생일타입)를 변경하는 유스케이스 인터페이스입니다.
 */
public interface ModifyFamilyMemberInfoUseCase {

    /**
     * 구성원의 기본 정보를 변경합니다.
     *
     * @param command 정보 변경 명령 객체
     * @return 업데이트된 구성원의 ID
     */
    Long modifyInfo(ModifyFamilyMemberInfoCommand command);
}
