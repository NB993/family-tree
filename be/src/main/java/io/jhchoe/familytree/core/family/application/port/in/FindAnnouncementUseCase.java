package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.Announcement;
import java.util.List;

/**
 * 공지사항 조회를 위한 유스케이스입니다.
 */
public interface FindAnnouncementUseCase {

    /**
     * 특정 가족의 모든 공지사항을 조회합니다.
     *
     * @param query 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 공지사항 목록
     */
    List<Announcement> findAll(FindAnnouncementQuery query);

    /**
     * 특정 ID의 공지사항을 조회합니다.
     *
     * @param query 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 공지사항
     */
    Announcement findById(FindAnnouncementByIdQuery query);
}
