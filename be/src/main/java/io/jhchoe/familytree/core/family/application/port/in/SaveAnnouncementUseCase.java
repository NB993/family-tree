package io.jhchoe.familytree.core.family.application.port.in;

/**
 * 공지사항 저장을 위한 유스케이스입니다.
 */
public interface SaveAnnouncementUseCase {

    /**
     * 공지사항을 저장합니다.
     *
     * @param command 저장에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 저장된 공지사항의 ID
     */
    Long save(SaveAnnouncementCommand command);
}
