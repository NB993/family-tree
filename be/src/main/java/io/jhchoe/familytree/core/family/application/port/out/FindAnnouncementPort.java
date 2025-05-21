package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Announcement;
import java.util.List;
import java.util.Optional;

/**
 * 공지사항 조회를 위한 포트입니다.
 */
public interface FindAnnouncementPort {

    /**
     * 특정 가족의 모든 공지사항을 조회합니다.
     *
     * @param familyId 가족 ID
     * @param page     페이지 번호 (0부터 시작)
     * @param size     페이지 크기
     * @return 공지사항 목록
     */
    List<Announcement> findAllByFamilyId(Long familyId, int page, int size);

    /**
     * 특정 가족의 모든 공지사항을 조회합니다 (페이징 없음).
     *
     * @param familyId 가족 ID
     * @return 공지사항 목록
     */
    List<Announcement> findAllByFamilyId(Long familyId);

    /**
     * 특정 ID의 공지사항을 조회합니다.
     *
     * @param id 공지사항 ID
     * @return 공지사항 (Optional)
     */
    Optional<Announcement> findById(Long id);
}
