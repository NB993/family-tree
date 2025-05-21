package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] AnnouncementTest")
class AnnouncementTest {

    @Test
    @DisplayName("create 메서드로 새로운 공지사항을 생성할 수 있다")
    void create_creates_new_announcement() {
        // given
        Long familyId = 1L;
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        
        // when
        Announcement announcement = Announcement.create(
            familyId, title, content
        );
        
        // then
        assertThat(announcement.getId()).isNull();
        assertThat(announcement.getFamilyId()).isEqualTo(familyId);
        assertThat(announcement.getTitle()).isEqualTo(title);
        assertThat(announcement.getContent()).isEqualTo(content);
        assertThat(announcement.getCreatedBy()).isNull();
        assertThat(announcement.getCreatedAt()).isNull();
        assertThat(announcement.getModifiedBy()).isNull();
        assertThat(announcement.getModifiedAt()).isNull();
    }
    
    @Test
    @DisplayName("withId 메서드로 모든 필드가 채워진 공지사항을 생성할 수 있다")
    void with_id_creates_announcement_with_all_fields() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        Long createdBy = 3L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 4L;
        LocalDateTime modifiedAt = LocalDateTime.now();
        
        // when
        Announcement announcement = Announcement.withId(
            id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt
        );
        
        // then
        assertThat(announcement.getId()).isEqualTo(id);
        assertThat(announcement.getFamilyId()).isEqualTo(familyId);
        assertThat(announcement.getTitle()).isEqualTo(title);
        assertThat(announcement.getContent()).isEqualTo(content);
        assertThat(announcement.getCreatedBy()).isEqualTo(createdBy);
        assertThat(announcement.getCreatedAt()).isEqualTo(createdAt);
        assertThat(announcement.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(announcement.getModifiedAt()).isEqualTo(modifiedAt);
    }
    
    @Test
    @DisplayName("update 메서드로 공지사항의 제목과 내용을 수정할 수 있다")
    void update_updates_title_and_content() {
        // given
        Announcement announcement = Announcement.withId(
            1L, 2L, "원래 제목", "원래 내용", 3L, 
            LocalDateTime.now().minusDays(1), 4L, LocalDateTime.now()
        );
        String newTitle = "새 제목";
        String newContent = "새 내용";
        
        // when
        Announcement updatedAnnouncement = announcement.update(newTitle, newContent);
        
        // then
        assertThat(updatedAnnouncement.getTitle()).isEqualTo(newTitle);
        assertThat(updatedAnnouncement.getContent()).isEqualTo(newContent);
        // 다른 필드는 변경되지 않아야 함
        assertThat(updatedAnnouncement.getId()).isEqualTo(announcement.getId());
        assertThat(updatedAnnouncement.getFamilyId()).isEqualTo(announcement.getFamilyId());
        assertThat(updatedAnnouncement.getCreatedBy()).isEqualTo(announcement.getCreatedBy());
        assertThat(updatedAnnouncement.getCreatedAt()).isEqualTo(announcement.getCreatedAt());
        assertThat(updatedAnnouncement.getModifiedBy()).isEqualTo(announcement.getModifiedBy());
        assertThat(updatedAnnouncement.getModifiedAt()).isEqualTo(announcement.getModifiedAt());
    }
    
    @Test
    @DisplayName("Family ID가 null이면 예외가 발생한다")
    void when_family_id_is_null_then_throw_exception() {
        // given
        Long familyId = null;
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        
        // when & then
        assertThatThrownBy(() -> {
            Announcement.create(familyId, title, content);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("familyId must not be null");
    }
    
    @Test
    @DisplayName("제목이 null이면 예외가 발생한다")
    void when_title_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        String title = null;
        String content = "공지사항 내용";
        
        // when & then
        assertThatThrownBy(() -> {
            Announcement.create(familyId, title, content);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("title must not be null");
    }
    
    @Test
    @DisplayName("withId 메서드의 ID가 null이면 예외가 발생한다")
    void when_id_is_null_in_with_id_then_throw_exception() {
        // given
        Long id = null;
        Long familyId = 2L;
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        Long createdBy = 3L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 4L;
        LocalDateTime modifiedAt = LocalDateTime.now();
        
        // when & then
        assertThatThrownBy(() -> {
            Announcement.withId(id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("id must not be null");
    }
    
    @Test
    @DisplayName("withId 메서드의 Family ID가 null이면 예외가 발생한다")
    void when_family_id_is_null_in_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        Long familyId = null;
        String title = "공지사항 제목";
        String content = "공지사항 내용";
        Long createdBy = 3L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 4L;
        LocalDateTime modifiedAt = LocalDateTime.now();
        
        // when & then
        assertThatThrownBy(() -> {
            Announcement.withId(id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("familyId must not be null");
    }
    
    @Test
    @DisplayName("withId 메서드의 제목이 null이면 예외가 발생한다")
    void when_title_is_null_in_with_id_then_throw_exception() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        String title = null;
        String content = "공지사항 내용";
        Long createdBy = 3L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 4L;
        LocalDateTime modifiedAt = LocalDateTime.now();
        
        // when & then
        assertThatThrownBy(() -> {
            Announcement.withId(id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("title must not be null");
    }
    
    @Test
    @DisplayName("update 메서드의 제목이 null이면 예외가 발생한다")
    void when_title_is_null_in_update_then_throw_exception() {
        // given
        Announcement announcement = Announcement.withId(
            1L, 2L, "원래 제목", "원래 내용", 3L, 
            LocalDateTime.now().minusDays(1), 4L, LocalDateTime.now()
        );
        String newTitle = null;
        String newContent = "새 내용";
        
        // when & then
        assertThatThrownBy(() -> {
            announcement.update(newTitle, newContent);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("title must not be null");
    }
}
