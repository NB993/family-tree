package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 구성원의 상태를 변경하기 위한 유스케이스입니다.
 */
public interface UpdateFamilyMemberStatusUseCase {

    /**
     * 구성원의 상태를 변경합니다.
     *
     * @param command 상태 변경에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 변경된 구성원의 ID
     */
    Long updateStatus(UpdateFamilyMemberStatusCommand command);
}
