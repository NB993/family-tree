package io.jhchoe.familytree.core.family.application.port.in;

/**
 * 공지사항 삭제를 위한 유스케이스입니다.
 */
public interface DeleteAnnouncementUseCase {

    /**
     * 공지사항을 삭제합니다.
     *
     * @param command 삭제에 필요한 입력 데이터를 포함하는 커맨드 객체
     */
    void delete(DeleteAnnouncementCommand command);
}
