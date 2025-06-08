package io.jhchoe.familytree.common.auth.application.port.in;

/**
 * 보안 이벤트 로깅을 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface LogSecurityEventUseCase {

    /**
     * 보안 이벤트를 로깅합니다.
     *
     * @param command 보안 이벤트 로깅에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 생성된 보안 이벤트의 ID
     */
    Long logEvent(LogSecurityEventCommand command);
}
