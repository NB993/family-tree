package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.config.TestAuditConfig;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("[Unit Test] FamilyAdapter")
class FamilyAdapterTest {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;
    
    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;
    
    @Autowired
    private FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;

    private FamilyAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyAdapter(familyJpaRepository, familyMemberJpaRepository, familyMemberRelationshipJpaRepository);
    }


    @Test
    @DisplayName("modifyFamily 메서드는 유효한 Family 인스턴스를 통해 데이터를 수정하고 ID를 반환해야 한다.")
    void given_valid_family_when_modify_family_then_return_id() {
        // given
        Family family = Family.newFamily("Name", "Description", "http://example.com", true);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Family modifyFamily = Family.withId(savedEntity.getId(), "Updated Name", "Updated Description",
            "http://updated-url.com", true, savedEntity.getCreatedBy(), savedEntity.getCreatedAt(), 2L, LocalDateTime.now());

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
        Family family = Family.withId(999L, "Name", "Description", "http://example.com", true, 1L, LocalDateTime.now(), 2L,
            LocalDateTime.now());

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

        Family family = Family.newFamily("가족 이름", "Description", "http://example.com", true);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));

        // when
        List<Family> families = sut.findByNameContaining(null);

        // then
        // when & then
        assertThat(families).hasSize(1);
        assertThat(families).extracting("name").containsExactlyInAnyOrder("가족 이름");
        assertThat(families).extracting("description").containsExactlyInAnyOrder("Description");
        assertThat(families).extracting("profileUrl").containsExactlyInAnyOrder("http://example.com");

    }

    @Test
    @DisplayName("findByNameContaining 메서드는 name을 전달받으면 해당 name을 포함한 Family 목록을 응답해야 한다.")
    void given_name_when_find_by_name_containing_then_return_family_list() {
        // given
        Family family1 = Family.newFamily("가족 이름1", "Description", "http://example.com", true);
        Family family2 = Family.newFamily("가족 이름2", "Description", "http://example.com", true);
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
