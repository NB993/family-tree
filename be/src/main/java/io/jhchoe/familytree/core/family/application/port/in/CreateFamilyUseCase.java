package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 생성을 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface CreateFamilyUseCase {

    /**
     * Family 생성을 처리합니다.
     *
     * @param command Family 생성에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 생성된 Family의 고유 식별자
     */
    Long create(CreateFamilyCommand command);
}
