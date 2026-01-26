package io.jhchoe.familytree.core.user.application.port.in;

import io.jhchoe.familytree.core.user.domain.User;

/**
 * User 프로필 수정 유스케이스 인터페이스입니다.
 */
public interface ModifyUserUseCase {

    /**
     * 사용자 프로필을 수정합니다.
     * User 생일 수정 시 연관된 모든 활성 FamilyMember의 생일도 함께 동기화됩니다.
     *
     * @param command 프로필 수정 명령 객체
     * @return 수정된 User 객체
     * @throws io.jhchoe.familytree.common.exception.FTException 사용자가 존재하지 않는 경우
     */
    User modify(ModifyUserCommand command);
}