package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Announcement;

/**
 * 공지사항 저장을 위한 포트입니다.
 */
public interface SaveAnnouncementPort {

    /**
     * 공지사항을 저장합니다.
     *
     * @param announcement 저장할 공지사항
     * @return 저장된 공지사항의 ID
     */
    Long save(Announcement announcement);

    /**
     * 공지사항을 삭제합니다.
     *
     * @param id 삭제할 공지사항 ID
     */
    void deleteById(Long id);
}
