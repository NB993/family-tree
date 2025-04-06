package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 수정 기능을 정의하는 유스케이스 인터페이스입니다.
 */
public interface ModifyFamilyUseCase {


    /**
     * Family 수정 요청을 처리합니다.
     *
     * @param command Family 수정을 위한 명령 객체 (null 불가)
     * @return 수정된 Family의 고유 식별자
     */
    Long modify(ModifyFamilyCommand command);
}
