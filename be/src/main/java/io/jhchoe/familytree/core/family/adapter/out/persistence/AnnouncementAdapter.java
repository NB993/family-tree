package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindAnnouncementPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveAnnouncementPort;
import io.jhchoe.familytree.core.family.domain.Announcement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 공지사항 관련 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class AnnouncementAdapter implements SaveAnnouncementPort, FindAnnouncementPort {

    private final AnnouncementJpaRepository announcementJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(Announcement announcement) {
        Objects.requireNonNull(announcement, "announcement must not be null");
        
        AnnouncementJpaEntity entity = AnnouncementJpaEntity.from(announcement);
        return announcementJpaRepository.save(entity).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        
        announcementJpaRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announcement> findAllByFamilyId(Long familyId, int page, int size) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        Pageable pageable = PageRequest.of(page, size);
        return announcementJpaRepository.findAllByFamilyIdOrderByCreatedAtDesc(familyId, pageable)
                .getContent()
                .stream()
                .map(AnnouncementJpaEntity::toDomainEntity)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Announcement> findAllByFamilyId(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        return announcementJpaRepository.findAllByFamilyIdOrderByCreatedAtDesc(familyId)
                .stream()
                .map(AnnouncementJpaEntity::toDomainEntity)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Announcement> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        
        return announcementJpaRepository.findById(id)
                .map(AnnouncementJpaEntity::toDomainEntity);
    }
}
