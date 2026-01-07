package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * FamilyMemberTagMappingJpaEntity 단위 테스트입니다.
 */
@DisplayName("FamilyMemberTagMappingJpaEntity")
class FamilyMemberTagMappingJpaEntityTest {

    @Nested
    @DisplayName("from 메서드는")
    class Describe_from {

        @Test
        @DisplayName("도메인 객체를 JPA 엔티티로 변환한다")
        void should_convert_domain_to_entity() {
            // given
            Long tagId = 1L;
            Long memberId = 2L;
            FamilyMemberTagMapping domain = FamilyMemberTagMapping.newMapping(tagId, memberId);

            // when
            FamilyMemberTagMappingJpaEntity sut = FamilyMemberTagMappingJpaEntity.from(domain);

            // then
            assertThat(sut.getId()).isNull();
            assertThat(sut.getTagId()).isEqualTo(tagId);
            assertThat(sut.getMemberId()).isEqualTo(memberId);
            assertThat(sut.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("ID가 있는 도메인 객체를 JPA 엔티티로 변환한다")
        void should_convert_domain_with_id_to_entity() {
            // given
            Long id = 10L;
            Long tagId = 1L;
            Long memberId = 2L;
            LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
            FamilyMemberTagMapping domain = FamilyMemberTagMapping.withId(id, tagId, memberId, createdAt);

            // when
            FamilyMemberTagMappingJpaEntity sut = FamilyMemberTagMappingJpaEntity.from(domain);

            // then
            assertThat(sut.getId()).isEqualTo(id);
            assertThat(sut.getTagId()).isEqualTo(tagId);
            assertThat(sut.getMemberId()).isEqualTo(memberId);
            assertThat(sut.getCreatedAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("null 도메인 객체가 전달되면 NullPointerException을 던진다")
        void should_throw_exception_when_domain_is_null() {
            // when & then
            assertThatThrownBy(() -> FamilyMemberTagMappingJpaEntity.from(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("domain");
        }
    }

    @Nested
    @DisplayName("toFamilyMemberTagMapping 메서드는")
    class Describe_toFamilyMemberTagMapping {

        @Test
        @DisplayName("JPA 엔티티를 도메인 객체로 변환한다")
        void should_convert_entity_to_domain() {
            // given
            Long id = 10L;
            Long tagId = 1L;
            Long memberId = 2L;
            LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30);
            FamilyMemberTagMapping originalDomain = FamilyMemberTagMapping.withId(id, tagId, memberId, createdAt);
            FamilyMemberTagMappingJpaEntity sut = FamilyMemberTagMappingJpaEntity.from(originalDomain);

            // when
            FamilyMemberTagMapping result = sut.toFamilyMemberTagMapping();

            // then
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getTagId()).isEqualTo(tagId);
            assertThat(result.getMemberId()).isEqualTo(memberId);
            assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        }
    }
}
