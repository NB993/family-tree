package io.jhchoe.familytree.core.relationship.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.relationship.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.relationship.domain.FamilyMemberRelationshipType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FamilyRelationshipAdapter")
@ExtendWith(MockitoExtension.class)
class FamilyMemberRelationshipAdapterTest {

    @Mock
    private FamilyRelationshipJpaRepository familyRelationshipJpaRepository;

    @InjectMocks
    private FamilyRelationshipAdapter sut;

    @Test
    @DisplayName("saveRelationship 메서드는 유효한 관계 객체를 받으면 저장하고 ID를 반환해야 한다")
    void given_valid_relationship_when_save_relationship_then_return_id() {
        // given
        Long expectedId = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String description = "부모 관계";
        
        FamilyMemberRelationship relationship = FamilyMemberRelationship.createRelationship(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            null,
            description
        );
        
        FamilyRelationshipJpaEntity savedEntity = FamilyRelationshipJpaEntity.from(relationship);
        savedEntity.setId(expectedId);
        
        when(familyRelationshipJpaRepository.save(any(FamilyRelationshipJpaEntity.class)))
            .thenReturn(savedEntity);

        // when
        Long result = sut.saveRelationship(relationship);

        // then
        assertThat(result).isEqualTo(expectedId);
        verify(familyRelationshipJpaRepository).save(any(FamilyRelationshipJpaEntity.class));
    }

    @Test
    @DisplayName("saveRelationship 메서드는 null을 받으면 예외를 발생시켜야 한다")
    void given_null_relationship_when_save_relationship_then_throw_exception() {
        // given
        FamilyMemberRelationship relationship = null;

        // when & then
        assertThatThrownBy(() -> sut.saveRelationship(relationship))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("relationship must not be null");
    }

    @Test
    @DisplayName("findRelationship 메서드는 유효한 파라미터로 관계를 조회할 수 있어야 한다")
    void given_valid_parameters_when_find_relationship_then_return_relationship() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String description = "부모 관계";
        
        FamilyRelationshipJpaEntity entity = new FamilyRelationshipJpaEntity();
        entity.setId(id);
        entity.setFamilyId(familyId);
        entity.setFromMemberId(fromMemberId);
        entity.setToMemberId(toMemberId);
        entity.setRelationshipType(relationshipType);
        entity.setDescription(description);
        
        when(familyRelationshipJpaRepository.findByFamilyIdAndFromMemberIdAndToMemberId(
            familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(entity));

        // when
        Optional<FamilyMemberRelationship> result = sut.findRelationship(familyId, fromMemberId, toMemberId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getFamilyId()).isEqualTo(familyId);
        assertThat(result.get().getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(result.get().getToMemberId()).isEqualTo(toMemberId);
        assertThat(result.get().getRelationshipType()).isEqualTo(relationshipType);
        assertThat(result.get().getDescription()).isEqualTo(description);
        
        verify(familyRelationshipJpaRepository)
            .findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId);
    }

    @Test
    @DisplayName("findRelationship 메서드는 존재하지 않는 관계에 대해 빈 Optional을 반환해야 한다")
    void given_non_existent_relationship_when_find_relationship_then_return_empty_optional() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        
        when(familyRelationshipJpaRepository.findByFamilyIdAndFromMemberIdAndToMemberId(
            familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.empty());

        // when
        Optional<FamilyMemberRelationship> result = sut.findRelationship(familyId, fromMemberId, toMemberId);

        // then
        assertThat(result).isEmpty();
        verify(familyRelationshipJpaRepository)
            .findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId);
    }

    @Test
    @DisplayName("findRelationship 메서드는 familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_find_relationship_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> sut.findRelationship(familyId, fromMemberId, toMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("findRelationship 메서드는 fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_find_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;
        Long toMemberId = 3L;

        // when & then
        assertThatThrownBy(() -> sut.findRelationship(familyId, fromMemberId, toMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fromMemberId must not be null");
    }

    @Test
    @DisplayName("findRelationship 메서드는 toMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_to_member_id_when_find_relationship_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = null;

        // when & then
        assertThatThrownBy(() -> sut.findRelationship(familyId, fromMemberId, toMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("toMemberId must not be null");
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 구성원이 정의한 모든 관계를 조회해야 한다")
    void given_valid_parameters_when_find_all_relationships_by_member_then_return_relationships() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        
        FamilyRelationshipJpaEntity entity1 = new FamilyRelationshipJpaEntity();
        entity1.setId(3L);
        entity1.setFamilyId(familyId);
        entity1.setFromMemberId(fromMemberId);
        entity1.setToMemberId(4L);
        entity1.setRelationshipType(FamilyMemberRelationshipType.PARENT);
        entity1.setDescription("부모 관계");
        
        FamilyRelationshipJpaEntity entity2 = new FamilyRelationshipJpaEntity();
        entity2.setId(5L);
        entity2.setFamilyId(familyId);
        entity2.setFromMemberId(fromMemberId);
        entity2.setToMemberId(6L);
        entity2.setRelationshipType(FamilyMemberRelationshipType.SIBLING);
        entity2.setDescription("형제 관계");
        
        List<FamilyRelationshipJpaEntity> entities = List.of(entity1, entity2);
        
        when(familyRelationshipJpaRepository.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId))
            .thenReturn(entities);

        // when
        List<FamilyMemberRelationship> result = sut.findAllRelationshipsByMember(familyId, fromMemberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getFamilyId()).isEqualTo(familyId);
        assertThat(result.get(0).getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(result.get(0).getToMemberId()).isEqualTo(4L);
        assertThat(result.get(0).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.PARENT);
        
        assertThat(result.get(1).getId()).isEqualTo(5L);
        assertThat(result.get(1).getFamilyId()).isEqualTo(familyId);
        assertThat(result.get(1).getFromMemberId()).isEqualTo(fromMemberId);
        assertThat(result.get(1).getToMemberId()).isEqualTo(6L);
        assertThat(result.get(1).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.SIBLING);
        
        verify(familyRelationshipJpaRepository).findAllByFamilyIdAndFromMemberId(familyId, fromMemberId);
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 존재하지 않는 관계에 대해 빈 리스트를 반환해야 한다")
    void given_non_existent_relationships_when_find_all_relationships_by_member_then_return_empty_list() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        
        when(familyRelationshipJpaRepository.findAllByFamilyIdAndFromMemberId(familyId, fromMemberId))
            .thenReturn(List.of());

        // when
        List<FamilyMemberRelationship> result = sut.findAllRelationshipsByMember(familyId, fromMemberId);

        // then
        assertThat(result).isEmpty();
        verify(familyRelationshipJpaRepository).findAllByFamilyIdAndFromMemberId(familyId, fromMemberId);
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 familyId가 null이면 예외를 발생시켜야 한다")
    void given_null_family_id_when_find_all_relationships_by_member_then_throw_exception() {
        // given
        Long familyId = null;
        Long fromMemberId = 2L;

        // when & then
        assertThatThrownBy(() -> sut.findAllRelationshipsByMember(familyId, fromMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 fromMemberId가 null이면 예외를 발생시켜야 한다")
    void given_null_from_member_id_when_find_all_relationships_by_member_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllRelationshipsByMember(familyId, fromMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fromMemberId must not be null");
    }
}
