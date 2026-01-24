package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Adapter Test] FamilyAdapter")
class FamilyAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    private FamilyAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyAdapter(familyJpaRepository, familyMemberJpaRepository);
    }


    @Test
    @DisplayName("modifyFamily 메서드는 유효한 Family 인스턴스를 통해 데이터를 수정하고 ID를 반환해야 한다.")
    void given_valid_family_when_modify_family_then_return_id() {
        // given
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Family modifyFamily = FamilyFixture.withId(savedEntity.getId(), "Updated Name", "Updated Description",
            "http://updated-url.com", true);

        // when
        Long updatedId = sut.modifyFamily(modifyFamily);

        // then
        assertThat(updatedId).isEqualTo(savedEntity.getId());

        Optional<FamilyJpaEntity> updatedEntity = familyJpaRepository.findById(updatedId);
        assertThat(updatedEntity).isPresent().get()
            .satisfies(entity -> {
                assertThat(entity.getName()).isEqualTo("Updated Name");
                assertThat(entity.getDescription()).isEqualTo("Updated Description");
                assertThat(entity.getProfileUrl()).isEqualTo("http://updated-url.com");
                assertThat(entity.getModifiedBy()).isEqualTo(99L);
            });
    }

    @Test
    @DisplayName("modifyFamily 메서드는 존재하지 않는 Family ID로 요청 시 FTException을 발생시켜야 한다.")
    void given_non_existent_family_id_when_modify_family_then_throw_ft_exception() {
        // given
        Family family = FamilyFixture.withId(999L);

        // when & then
        assertThatThrownBy(() -> sut.modifyFamily(family))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("family");
    }

    @Test
    @DisplayName("modifyFamily 메서드는 null 값이 전달되면 NullPointerException을 발생시켜야 한다.")
    void given_null_family_when_modify_family_then_throw_null_pointer_exception() {
        // given
        Family family = null;

        // when & then
        assertThatThrownBy(() -> sut.modifyFamily(family))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("family must not be null");
    }

    @Test
    @DisplayName("findByNameContaining 메서드는 null 값이 전달되면 \"\"로 치환하여 조회한다.")
    void given_null_name_when_find_by_name_containing_then_throw_exception() {
        // given
        String name = null;

        Family family = FamilyFixture.newFamily("가족 이름");
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));

        // when
        List<Family> families = sut.findByNameContaining(null);

        // then
        assertThat(families).hasSize(1);
        assertThat(families).extracting("name").containsExactlyInAnyOrder("가족 이름");

    }

    @Test
    @DisplayName("findByNameContaining 메서드는 name을 전달받으면 해당 name을 포함한 Family 목록을 응답해야 한다.")
    void given_name_when_find_by_name_containing_then_return_family_list() {
        // given
        Family family1 = FamilyFixture.newFamily("가족 이름1");
        Family family2 = FamilyFixture.newFamily("가족 이름2");
        FamilyJpaEntity savedEntity1 = familyJpaRepository.save(FamilyJpaEntity.from(family1));
        FamilyJpaEntity savedEntity2 = familyJpaRepository.save(FamilyJpaEntity.from(family2));

        // when
        List<Family> families = sut.findByNameContaining("가족");

        // when & then
        assertThat(families).hasSize(2);
        assertThat(families).extracting("name")
            .containsExactlyInAnyOrder("가족 이름1", "가족 이름2");
    }
}
