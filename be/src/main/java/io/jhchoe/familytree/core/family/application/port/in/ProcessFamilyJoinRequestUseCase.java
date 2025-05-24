package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;

/**
 * Family 가입 신청 처리(승인/거부)를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface ProcessFamilyJoinRequestUseCase {

    /**
     * 지정된 가입 신청을 처리합니다.
     *
     * @param command 가입 신청 처리에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 처리된 FamilyJoinRequest 객체
     * @throws FTException 권한이 없거나 처리할 수 없는 신청인 경우
     */
    FamilyJoinRequest process(ProcessFamilyJoinRequestCommand command);
}
