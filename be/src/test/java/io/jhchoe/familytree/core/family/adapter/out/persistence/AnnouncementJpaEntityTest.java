package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.Announcement;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] AnnouncementJpaEntityTest")
class AnnouncementJpaEntityTest {

    @Test
    @DisplayName("from 메서드는 유효한 Announcement 객체를 입력받아 JpaEntity 객체를 정상적으로 생성해야 한다")
    void given_valid_announcement_when_from_then_return_jpa_entity() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        String title = "중요 공지";
        String content = "가족 모임 안내입니다.";
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        Announcement announcement = Announcement.withId(
            id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt
        );

        // when
        AnnouncementJpaEntity result = AnnouncementJpaEntity.from(announcement);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("from 메서드는 null Announcement를 입력받는 경우 NullPointerException을 발생시켜야 한다")
    void given_null_announcement_when_from_then_throw_exception() {
        // given
        Announcement announcement = null;

        // when & then
        assertThatThrownBy(() -> AnnouncementJpaEntity.from(announcement))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("announcement must not be null");
    }

    @Test
    @DisplayName("toDomainEntity 메서드는 JpaEntity를 도메인 객체로 올바르게 변환해야 한다")
    void given_jpa_entity_when_to_domain_entity_then_return_domain_object() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        String title = "긴급 공지";
        String content = "회의 일정이 변경되었습니다.";
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        AnnouncementJpaEntity entity = new AnnouncementJpaEntity(
            id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt
        );

        // when
        Announcement result = entity.toDomainEntity();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("새로운 공지사항 생성을 위한 from 메서드는 ID가 null인 객체를 올바르게 처리해야 한다")
    void given_new_announcement_when_from_then_return_entity_without_id() {
        // given
        Long familyId = 101L;
        String title = "새 공지사항";
        String content = "새로운 공지사항입니다.";

        Announcement announcement = Announcement.create(familyId, title, content);

        // when
        AnnouncementJpaEntity result = AnnouncementJpaEntity.from(announcement);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // 새로운 엔티티이므로 ID는 null
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getCreatedBy()).isNull(); // 새로운 엔티티이므로 audit 필드는 null
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getModifiedBy()).isNull();
        assertThat(result.getModifiedAt()).isNull();
    }
}
