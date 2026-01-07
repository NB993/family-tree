package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberTagJpaEntityTest")
class FamilyMemberTagJpaEntityTest {

    @Test
    @DisplayName("from 메서드로 도메인 객체에서 JPA 엔티티를 생성합니다")
    void from_creates_entity_from_domain() {
        // given
        FamilyMemberTag tag = FamilyMemberTag.newTag(1L, "친가", 100L);

        // when
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);

        // then
        assertThat(entity.getFamilyId()).isEqualTo(tag.getFamilyId());
        assertThat(entity.getName()).isEqualTo(tag.getName());
        assertThat(entity.getColor()).isEqualTo(tag.getColor());
    }

    @Test
    @DisplayName("from 메서드로 ID가 있는 도메인 객체에서 JPA 엔티티를 생성합니다")
    void from_creates_entity_from_domain_with_id() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FamilyMemberTag tag = FamilyMemberTag.withId(
            1L, 10L, "외가", "#E3E2E0", 100L, now, 100L, now
        );

        // when
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);

        // then
        assertThat(entity.getId()).isEqualTo(tag.getId());
        assertThat(entity.getFamilyId()).isEqualTo(tag.getFamilyId());
        assertThat(entity.getName()).isEqualTo(tag.getName());
        assertThat(entity.getColor()).isEqualTo(tag.getColor());
    }

    @Test
    @DisplayName("from 메서드에 null 전달 시 NullPointerException이 발생합니다")
    void from_throws_exception_when_null() {
        // when & then
        assertThatThrownBy(() -> FamilyMemberTagJpaEntity.from(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("tag");
    }

    @Test
    @DisplayName("toFamilyMemberTag 메서드로 JPA 엔티티를 도메인 객체로 변환합니다")
    void toFamilyMemberTag_creates_domain_from_entity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FamilyMemberTag originalTag = FamilyMemberTag.withId(
            1L, 10L, "조카들", "#DBEDDB", 100L, now, 100L, now
        );
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(originalTag);

        // when
        FamilyMemberTag result = entity.toFamilyMemberTag();

        // then
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getFamilyId()).isEqualTo(entity.getFamilyId());
        assertThat(result.getName()).isEqualTo(entity.getName());
        assertThat(result.getColor()).isEqualTo(entity.getColor());
    }
}
