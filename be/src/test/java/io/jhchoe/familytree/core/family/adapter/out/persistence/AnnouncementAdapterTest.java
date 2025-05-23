package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.Announcement;
import io.jhchoe.familytree.helper.AdapterTestBase;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Unit Test] AnnouncementAdapterTest")
class AnnouncementAdapterTest extends AdapterTestBase {

    @Autowired
    private AnnouncementJpaRepository announcementJpaRepository;

    private AnnouncementAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new AnnouncementAdapter(announcementJpaRepository);
    }

    @Test
    @DisplayName("save 메서드는 Announcement를 성공적으로 저장한다")
    void save_announcement_successfully() {
        // given
        Long familyId = 1L;
        String title = "중요 공지";
        String content = "가족 모임 안내입니다.";
        
        Announcement announcement = Announcement.create(familyId, title, content);

        // when
        Long savedId = sut.save(announcement);

        // then
        assertThat(savedId).isNotNull();
        assertThat(savedId).isPositive();
        
        // 저장 확인
        AnnouncementJpaEntity savedEntity = announcementJpaRepository.findById(savedId).orElseThrow();
        assertThat(savedEntity.getFamilyId()).isEqualTo(familyId);
        assertThat(savedEntity.getTitle()).isEqualTo(title);
        assertThat(savedEntity.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("save 메서드는 null announcement로 호출 시 예외를 발생시킨다")
    void throw_exception_when_save_with_null_announcement() {
        // given
        Announcement nullAnnouncement = null;

        // when & then
        assertThatThrownBy(() -> sut.save(nullAnnouncement))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("announcement must not be null");
    }

    @Test
    @DisplayName("findById 메서드는 ID로 Announcement를 조회할 수 있다")
    void return_announcement_when_exists_by_id() {
        // given
        Announcement announcement = Announcement.create(1L, "제목", "내용");
        AnnouncementJpaEntity entity = AnnouncementJpaEntity.from(announcement);
        AnnouncementJpaEntity savedEntity = announcementJpaRepository.save(entity);
        
        // when
        Optional<Announcement> result = sut.findById(savedEntity.getId());
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getTitle()).isEqualTo("제목");
        assertThat(result.get().getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("findById 메서드는 존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
    void return_empty_optional_when_not_exists_by_id() {
        // given
        Long nonExistentId = 999L;
        
        // when
        Optional<Announcement> result = sut.findById(nonExistentId);
        
        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllByFamilyId 메서드는 특정 Family의 모든 공지사항을 페이징하여 조회한다")
    void return_all_announcements_by_family_id_with_paging() {
        // given
        Long familyId = 1L;
        
        // 여러 공지사항 생성
        createAnnouncement(familyId, "공지 1", "내용 1");
        createAnnouncement(familyId, "공지 2", "내용 2");
        createAnnouncement(familyId, "공지 3", "내용 3");
        createAnnouncement(familyId, "공지 4", "내용 4");
        createAnnouncement(familyId, "공지 5", "내용 5");
        
        // 다른 Family의 공지사항
        createAnnouncement(2L, "다른 Family 공지", "다른 내용");

        // when
        List<Announcement> firstPage = sut.findAllByFamilyId(familyId, 0, 3);
        List<Announcement> secondPage = sut.findAllByFamilyId(familyId, 1, 3);

        // then
        assertThat(firstPage).hasSize(3);
        assertThat(secondPage).hasSize(2);
        
        // 모든 공지사항이 해당 Family에 속하는지 확인
        assertThat(firstPage)
            .extracting(Announcement::getFamilyId)
            .containsOnly(familyId);
        assertThat(secondPage)
            .extracting(Announcement::getFamilyId)
            .containsOnly(familyId);
        
        // 제목 확인
        List<String> allTitles = new ArrayList<>(firstPage.stream()
            .map(Announcement::getTitle)
            .toList());
        allTitles.addAll(secondPage.stream()
            .map(Announcement::getTitle)
            .toList());
        
        assertThat(allTitles).containsExactlyInAnyOrder("공지 1", "공지 2", "공지 3", "공지 4", "공지 5");
    }

    @Test
    @DisplayName("findAllByFamilyId 메서드는 null familyId로 호출 시 예외를 발생시킨다")
    void throw_exception_when_find_by_family_id_with_null() {
        // given
        Long nullFamilyId = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllByFamilyId(nullFamilyId, 0, 10))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }

    @Test
    @DisplayName("deleteById 메서드는 Announcement를 성공적으로 삭제한다")
    void delete_announcement_successfully() {
        // given
        Announcement announcement = Announcement.create(1L, "삭제할 공지", "삭제할 내용");
        AnnouncementJpaEntity entity = AnnouncementJpaEntity.from(announcement);
        AnnouncementJpaEntity savedEntity = announcementJpaRepository.save(entity);
        Long savedId = savedEntity.getId();
        
        // when
        sut.deleteById(savedId);
        
        // then
        Optional<AnnouncementJpaEntity> deletedEntity = announcementJpaRepository.findById(savedId);
        assertThat(deletedEntity).isEmpty();
    }

    @Test
    @DisplayName("deleteById 메서드는 null id로 호출 시 예외를 발생시킨다")
    void throw_exception_when_delete_with_null_id() {
        // given
        Long nullId = null;

        // when & then
        assertThatThrownBy(() -> sut.deleteById(nullId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("id must not be null");
    }

    /**
     * 테스트용 헬퍼 메서드: 공지사항을 생성합니다.
     */
    private void createAnnouncement(Long familyId, String title, String content) {
        Announcement announcement = Announcement.create(familyId, title, content);
        AnnouncementJpaEntity entity = AnnouncementJpaEntity.from(announcement);
        announcementJpaRepository.save(entity);
    }
}
