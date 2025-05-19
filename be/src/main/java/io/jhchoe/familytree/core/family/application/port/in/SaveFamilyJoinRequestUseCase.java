package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 가입 신청을 처리하는 유스케이스 인터페이스입니다.
 */
public interface SaveFamilyJoinRequestUseCase {

    /**
     * Family 가입 신청을 처리합니다.
     *
     * @param command 가입 신청에 필요한 정보가 담긴 커맨드 객체
     * @return 생성된 가입 신청의 ID
     */
    Long save(SaveFamilyJoinRequestCommand command);
}
