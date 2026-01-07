package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Adapter Test] FamilyMemberTagAdapterTest")
class FamilyMemberTagAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private FamilyMemberTagJpaRepository familyMemberTagJpaRepository;

    private FamilyMemberTagAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyMemberTagAdapter(familyMemberTagJpaRepository);
    }

    @Nested
    @DisplayName("save 메서드는")
    class SaveMethod {

        @Test
        @DisplayName("태그를 성공적으로 저장하고 ID를 반환한다")
        void save_tag_successfully() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.newTag(1L, "친가", 100L);

            // when
            Long savedId = sut.save(tag);

            // then
            assertThat(savedId).isNotNull();
            assertThat(savedId).isPositive();

            // 저장 확인
            FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.findById(savedId).orElseThrow();
            assertThat(savedEntity.getFamilyId()).isEqualTo(1L);
            assertThat(savedEntity.getName()).isEqualTo("친가");
        }

        @Test
        @DisplayName("null 태그로 호출 시 예외를 발생시킨다")
        void throw_exception_when_save_with_null_tag() {
            // when & then
            assertThatThrownBy(() -> sut.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("tag must not be null");
        }
    }

    @Nested
    @DisplayName("findById 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("ID로 태그를 조회할 수 있다")
        void return_tag_when_exists_by_id() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.newTag(1L, "외가", 100L);
            FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
            FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);

            // when
            Optional<FamilyMemberTag> result = sut.findById(savedEntity.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
            assertThat(result.get().getName()).isEqualTo("외가");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
        void return_empty_optional_when_not_exists_by_id() {
            // given
            Long nonExistentId = 999L;

            // when
            Optional<FamilyMemberTag> result = sut.findById(nonExistentId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null ID로 호출 시 예외를 발생시킨다")
        void throw_exception_when_find_with_null_id() {
            // when & then
            assertThatThrownBy(() -> sut.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
        }
    }

    @Nested
    @DisplayName("findAllByFamilyId 메서드는")
    class FindAllByFamilyIdMethod {

        @Test
        @DisplayName("특정 Family의 모든 태그를 조회한다")
        void return_all_tags_by_family_id() {
            // given
            Long familyId = 1L;
            createTag(familyId, "친가", 100L);
            createTag(familyId, "외가", 100L);
            createTag(familyId, "조카들", 100L);
            createTag(2L, "다른가족태그", 200L); // 다른 Family

            // when
            List<FamilyMemberTag> result = sut.findAllByFamilyId(familyId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                .extracting(FamilyMemberTag::getName)
                .containsExactlyInAnyOrder("친가", "외가", "조카들");
        }

        @Test
        @DisplayName("태그가 없으면 빈 리스트를 반환한다")
        void return_empty_list_when_no_tags() {
            // given
            Long familyId = 999L;

            // when
            List<FamilyMemberTag> result = sut.findAllByFamilyId(familyId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null familyId로 호출 시 예외를 발생시킨다")
        void throw_exception_when_find_with_null_family_id() {
            // when & then
            assertThatThrownBy(() -> sut.findAllByFamilyId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("familyId must not be null");
        }
    }

    @Nested
    @DisplayName("findByFamilyIdAndName 메서드는")
    class FindByFamilyIdAndNameMethod {

        @Test
        @DisplayName("Family와 이름으로 태그를 조회한다")
        void return_tag_by_family_id_and_name() {
            // given
            Long familyId = 1L;
            createTag(familyId, "친가", 100L);

            // when
            Optional<FamilyMemberTag> result = sut.findByFamilyIdAndName(familyId, "친가");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("친가");
        }

        @Test
        @DisplayName("존재하지 않는 이름으로 조회 시 빈 Optional을 반환한다")
        void return_empty_optional_when_name_not_exists() {
            // given
            Long familyId = 1L;
            createTag(familyId, "친가", 100L);

            // when
            Optional<FamilyMemberTag> result = sut.findByFamilyIdAndName(familyId, "없는태그");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 Family의 동일 이름 태그는 조회되지 않는다")
        void not_return_tag_from_different_family() {
            // given
            createTag(1L, "공통태그", 100L);
            createTag(2L, "공통태그", 200L);

            // when
            Optional<FamilyMemberTag> result = sut.findByFamilyIdAndName(1L, "공통태그");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getFamilyId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("countByFamilyId 메서드는")
    class CountByFamilyIdMethod {

        @Test
        @DisplayName("특정 Family의 태그 수를 반환한다")
        void return_tag_count_by_family_id() {
            // given
            Long familyId = 1L;
            createTag(familyId, "태그1", 100L);
            createTag(familyId, "태그2", 100L);
            createTag(familyId, "태그3", 100L);
            createTag(2L, "다른가족", 200L); // 다른 Family

            // when
            int count = sut.countByFamilyId(familyId);

            // then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("태그가 없으면 0을 반환한다")
        void return_zero_when_no_tags() {
            // given
            Long familyId = 999L;

            // when
            int count = sut.countByFamilyId(familyId);

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("null familyId로 호출 시 예외를 발생시킨다")
        void throw_exception_when_count_with_null_family_id() {
            // when & then
            assertThatThrownBy(() -> sut.countByFamilyId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("familyId must not be null");
        }
    }

    @Nested
    @DisplayName("deleteById 메서드는")
    class DeleteByIdMethod {

        @Test
        @DisplayName("태그를 성공적으로 삭제한다")
        void delete_tag_successfully() {
            // given
            FamilyMemberTag tag = FamilyMemberTag.newTag(1L, "삭제할태그", 100L);
            FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
            FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);
            Long savedId = savedEntity.getId();

            // when
            sut.deleteById(savedId);

            // then
            Optional<FamilyMemberTagJpaEntity> deletedEntity = familyMemberTagJpaRepository.findById(savedId);
            assertThat(deletedEntity).isEmpty();
        }

        @Test
        @DisplayName("null id로 호출 시 예외를 발생시킨다")
        void throw_exception_when_delete_with_null_id() {
            // when & then
            assertThatThrownBy(() -> sut.deleteById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id must not be null");
        }
    }

    /**
     * 테스트용 헬퍼 메서드: 태그를 생성합니다.
     */
    private void createTag(Long familyId, String name, Long createdBy) {
        FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
        familyMemberTagJpaRepository.save(entity);
    }
}
