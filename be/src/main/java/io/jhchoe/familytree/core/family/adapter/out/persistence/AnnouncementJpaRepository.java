package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 공지사항에 대한 JPA 리포지토리 인터페이스입니다.
 */
public interface AnnouncementJpaRepository extends JpaRepository<AnnouncementJpaEntity, Long> {

    /**
     * 특정 가족의 모든 공지사항을 조회합니다.
     *
     * @param familyId 가족 ID
     * @param pageable 페이징 정보
     * @return 공지사항 목록 (페이징)
     */
    Page<AnnouncementJpaEntity> findAllByFamilyIdOrderByCreatedAtDesc(Long familyId, Pageable pageable);

    /**
     * 특정 가족의 모든 공지사항을 조회합니다.
     *
     * @param familyId 가족 ID
     * @return 공지사항 목록
     */
    List<AnnouncementJpaEntity> findAllByFamilyIdOrderByCreatedAtDesc(Long familyId);
}
