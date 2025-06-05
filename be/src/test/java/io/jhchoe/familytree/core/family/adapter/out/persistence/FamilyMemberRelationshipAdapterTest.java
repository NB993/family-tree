package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.config.TestAuditConfig;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.util.List;
import java.util.Optional;
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
@DisplayName("[Unit Test] FamilyRelationshipAdapter")
class FamilyMemberRelationshipAdapterTest {

    @Autowired
    private FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;

    private FamilyMemberRelationshipAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyMemberRelationshipAdapter(familyMemberRelationshipJpaRepository);
    }

    @Test
    @DisplayName("save 메서드는 null을 받으면 예외를 발생시켜야 한다")
    void given_valid_relationship_when_save_then_return_id() {
        // given
        FamilyMemberRelationship familyMemberRelationship = null;

        // when & then
        assertThatThrownBy(() -> sut.save(familyMemberRelationship))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("relationship must not be null");
    }

    @Test
    @DisplayName("save 메서드는 유효한 관계 객체를 받으면 저장하고 ID를 반환해야 한다")
    void given_null_relationship_when_save_then_throw_exception() {
        // given
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.FATHER;
        String customRelationship = null;
        String description = "부모 관계";

        FamilyMemberRelationship familyMemberRelationship = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        // when
        Long savedId = sut.save(familyMemberRelationship);

        //then
        assertThat(savedId).isGreaterThan(0);

        FamilyMemberRelationship savedRelationship = sut.findByFamilyIdAndFromMemberIdAndToMemberId(
            familyId, fromMemberId, toMemberId).get();
        assertThat(savedId).isEqualTo(savedRelationship.getId());
        assertThat(savedRelationship.getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(savedRelationship.getToMemberId()).isEqualTo(toMemberId);
        assertThat(savedRelationship.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(savedRelationship.getCustomRelationship()).isEqualTo(customRelationship);
        assertThat(savedRelationship.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("findByFamilyIdAndFromMemberIdAndToMemberId 메서드는 유효한 파라미터로 관계를 조회할 수 있어야 한다")
    void given_valid_parameters_when_find_by_family_id_and_from_member_id_and_to_member_id_then_return_relationship() {
        // given
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.FATHER;
        String customRelationship = null;
        String description = "부모 관계";

        FamilyMemberRelationship familyMemberRelationship = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(familyMemberRelationship));

        // when
        FamilyMemberRelationship result = sut.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId).get();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(result.getToMemberId()).isEqualTo(toMemberId);
        assertThat(result.getRelationshipType()).isEqualTo(relationshipType);
        assertThat(result.getCustomRelationship()).isEqualTo(customRelationship);
        assertThat(result.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("findByFamilyIdAndFromMemberIdAndToMemberId 메서드는 존재하지 않는 관계에 대해 빈 Optional을 반환해야 한다")
    void given_non_existent_relationship_when_find_by_family_id_and_from_member_id_and_to_member_id_then_return_empty_optional() {
        // given
        Long familyId = 999L;
        Long fromMemberId = 999L;
        Long toMemberId = 999L;

        // when
        Optional<FamilyMemberRelationship> result = sut.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByFamilyIdAndFromMemberIdAndToMemberId 메서드는 familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_find_by_family_id_and_from_member_id_and_to_member_id_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> sut.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("findRelationship 메서드는 fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_find_by_family_id_and_from_member_id_and_to_member_id_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> sut.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fromMemberId must not be null");
    }

    @Test
    @DisplayName("findByFamilyIdAndFromMemberIdAndToMemberId 메서드는 toMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_to_member_id_when_find_by_family_id_and_from_member_id_and_to_member_id_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = null;

        // when & then
        assertThatThrownBy(() -> sut.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("toMemberId must not be null");
    }

    @Test
    @DisplayName("findAllByFamilyIdAndFromMemberId 메서드는 구성원이 정의한 모든 관계를 조회해야 한다")
    void given_valid_parameters_when_find_all_by_family_id_and_from_member_id_then_return_relationships() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;

        Long toMemberId1 = 3L;
        FamilyMemberRelationshipType relationshipType1 = FamilyMemberRelationshipType.FATHER;
        String customRelationship1 = null;
        String description1 = "부모 관계";

        Long toMemberId2 = 4L;
        FamilyMemberRelationshipType relationshipType2 = FamilyMemberRelationshipType.ELDER_BROTHER;
        String customRelationship2 = null;
        String description2 = "형제 관계";

        FamilyMemberRelationship familyMemberRelationship1 = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId1,
            relationshipType1,
            customRelationship1,
            description1
        );
        FamilyMemberRelationship familyMemberRelationship2 = FamilyMemberRelationship.newRelationship(
            familyId,
            fromMemberId,
            toMemberId2,
            relationshipType2,
            customRelationship2,
            description2
        );

        sut.save(familyMemberRelationship1);
        sut.save(familyMemberRelationship2);

        // when
        List<FamilyMemberRelationship> results = sut.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId);


        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isGreaterThan(0);
        assertThat(results.get(0).getFamilyId()).isEqualTo(familyId);
        assertThat(results.get(0).getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(results.get(0).getToMemberId()).isEqualTo(toMemberId1);
        assertThat(results.get(0).getRelationshipType()).isEqualTo(relationshipType1);
        assertThat(results.get(0).getDescription()).isEqualTo(description1);

        assertThat(results.get(1).getId()).isGreaterThan(0);
        assertThat(results.get(1).getFamilyId()).isEqualTo(familyId);
        assertThat(results.get(1).getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(results.get(1).getToMemberId()).isEqualTo(toMemberId2);
        assertThat(results.get(1).getRelationshipType()).isEqualTo(relationshipType2);
        assertThat(results.get(1).getDescription()).isEqualTo(description2);
    }

    @Test
    @DisplayName("findAllByFamilyIdAndFromMemberId 메서드는 존재하지 않는 관계에 대해 빈 리스트를 반환해야 한다")
    void given_non_existent_relationships_when_find_all_by_family_id_and_from_member_id_then_return_empty_list() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;

        // when
        List<FamilyMemberRelationship> result = sut.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllByFamilyIdAndFromMemberId 메서드는 familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_find_all_by_family_id_and_from_member_id_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;

        // when & then
        assertThatThrownBy(() -> sut.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("findAllByFamilyIdAndFromMemberId 메서드는 fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_find_all_by_family_id_and_from_member_id_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fromMemberId must not be null");
    }
}
