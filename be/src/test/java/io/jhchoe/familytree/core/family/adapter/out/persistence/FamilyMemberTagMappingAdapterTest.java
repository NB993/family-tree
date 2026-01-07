package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Adapter Test] FamilyMemberTagMappingAdapterTest")
class FamilyMemberTagMappingAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private FamilyMemberTagMappingJpaRepository familyMemberTagMappingJpaRepository;

    private FamilyMemberTagMappingAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyMemberTagMappingAdapter(familyMemberTagMappingJpaRepository);
    }

    @Nested
    @DisplayName("saveAll 메서드는")
    class SaveAllMethod {

        @Test
        @DisplayName("매핑 목록을 성공적으로 저장한다")
        void saveAll_mappings_successfully() {
            // given
            Long tagId1 = 1L;
            Long tagId2 = 2L;
            Long memberId = 10L;
            List<FamilyMemberTagMapping> mappings = List.of(
                FamilyMemberTagMapping.newMapping(tagId1, memberId),
                FamilyMemberTagMapping.newMapping(tagId2, memberId)
            );

            // when
            sut.saveAll(mappings);

            // then
            List<FamilyMemberTagMappingJpaEntity> savedEntities = familyMemberTagMappingJpaRepository.findAll();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities)
                .extracting(FamilyMemberTagMappingJpaEntity::getTagId)
                .containsExactlyInAnyOrder(tagId1, tagId2);
            assertThat(savedEntities)
                .extracting(FamilyMemberTagMappingJpaEntity::getMemberId)
                .containsOnly(memberId);
        }

        @Test
        @DisplayName("빈 목록이면 아무것도 저장하지 않는다")
        void saveAll_empty_list() {
            // given
            List<FamilyMemberTagMapping> emptyMappings = List.of();

            // when
            sut.saveAll(emptyMappings);

            // then
            List<FamilyMemberTagMappingJpaEntity> savedEntities = familyMemberTagMappingJpaRepository.findAll();
            assertThat(savedEntities).isEmpty();
        }

        @Test
        @DisplayName("null 목록으로 호출 시 예외를 발생시킨다")
        void throw_exception_when_saveAll_with_null_mappings() {
            // when & then
            assertThatThrownBy(() -> sut.saveAll(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("mappings must not be null");
        }
    }

    @Nested
    @DisplayName("deleteAllByMemberId 메서드는")
    class DeleteAllByMemberIdMethod {

        @Test
        @DisplayName("특정 멤버의 모든 태그 매핑을 삭제한다")
        void deleteAllByMemberId_successfully() {
            // given
            Long memberId = 10L;
            Long otherMemberId = 20L;
            createMapping(1L, memberId);
            createMapping(2L, memberId);
            createMapping(3L, otherMemberId); // 다른 멤버

            // when
            sut.deleteAllByMemberId(memberId);

            // then
            List<FamilyMemberTagMappingJpaEntity> remainingEntities = familyMemberTagMappingJpaRepository.findAll();
            assertThat(remainingEntities).hasSize(1);
            assertThat(remainingEntities.get(0).getMemberId()).isEqualTo(otherMemberId);
        }

        @Test
        @DisplayName("매핑이 없는 멤버에 대해서도 정상 동작한다")
        void deleteAllByMemberId_when_no_mappings() {
            // given
            Long nonExistentMemberId = 999L;

            // when & then (예외 없이 정상 동작)
            sut.deleteAllByMemberId(nonExistentMemberId);
        }

        @Test
        @DisplayName("null memberId로 호출 시 예외를 발생시킨다")
        void throw_exception_when_deleteAllByMemberId_with_null_memberId() {
            // when & then
            assertThatThrownBy(() -> sut.deleteAllByMemberId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("memberId must not be null");
        }
    }

    /**
     * 테스트용 헬퍼 메서드: 태그 매핑을 생성합니다.
     */
    private void createMapping(Long tagId, Long memberId) {
        FamilyMemberTagMapping mapping = FamilyMemberTagMapping.newMapping(tagId, memberId);
        FamilyMemberTagMappingJpaEntity entity = FamilyMemberTagMappingJpaEntity.from(mapping);
        familyMemberTagMappingJpaRepository.save(entity);
    }
}
