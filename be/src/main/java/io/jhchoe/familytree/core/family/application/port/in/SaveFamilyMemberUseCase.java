package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;

/**
 * 가족 구성원 수동 등록을 위한 유스케이스 인터페이스입니다.
 */
public interface SaveFamilyMemberUseCase {

    /**
     * 가족 구성원을 수동으로 등록합니다.
     *
     * @param command 구성원 등록에 필요한 정보를 담은 커맨드 객체
     * @return 저장된 구성원의 ID
     * @throws FTException 가족이 존재하지 않거나, 현재 사용자가 가족의 구성원이 아닌 경우
     */
    Long save(SaveFamilyMemberCommand command);
}
