package io.jhchoe.familytree.core.relationship.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.relationship.application.port.in.DefineFamilyRelationshipCommand;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyRelationshipQuery;
import io.jhchoe.familytree.core.relationship.application.port.in.FindMemberRelationshipsQuery;
import io.jhchoe.familytree.core.relationship.application.port.out.FindFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.application.port.out.SaveFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationshipType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FamilyRelationshipService")
@ExtendWith(MockitoExtension.class)
class FamilyRelationshipServiceTest {

    @Mock
    private SaveFamilyRelationshipPort saveFamilyRelationshipPort;

    @Mock
    private FindFamilyRelationshipPort findFamilyRelationshipPort;

    @InjectMocks
    private FamilyRelationshipService sut;

    @Test
    @DisplayName("defineRelationship 메서드는 기존 관계가 없을 때 새 관계를 생성해야 한다")
    void given_no_existing_relationship_when_define_relationship_then_create_new_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyRelationshipType relationshipType = FamilyRelationshipType.PARENT;
        String customRelationship = null;
        String description = "새로운 관계";
        Long expectedId = 4L;
        
        DefineFamilyRelationshipCommand command = new DefineFamilyRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );
        
        when(findFamilyRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.empty());
        
        when(saveFamilyRelationshipPort.saveRelationship(any(FamilyRelationship.class)))
            .thenReturn(expectedId);

        // when
        Long result = sut.defineRelationship(command);

        // then
        assertThat(result).isEqualTo(expectedId);
        verify(findFamilyRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
        verify(saveFamilyRelationshipPort).saveRelationship(any(FamilyRelationship.class));
    }

    @Test
    @DisplayName("defineRelationship 메서드는 기존 관계가 있을 때 관계를 업데이트해야 한다")
    void given_existing_relationship_when_define_relationship_then_update_relationship() {
        // given
        Long relationshipId = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyRelationshipType originalType = FamilyRelationshipType.PARENT;
        FamilyRelationshipType newType = FamilyRelationshipType.GRANDPARENT;
        String originalCustom = null;
        String newCustom = null;
        String originalDesc = "원래 설명";
        String newDesc = "새로운 설명";
        
        FamilyRelationship existingRelationship = FamilyRelationship.withId(
            relationshipId,
            familyId,
            fromMemberId,
            toMemberId,
            originalType,
            originalCustom,
            originalDesc,
            null,
            null,
            null,
            null
        );
        
        DefineFamilyRelationshipCommand command = new DefineFamilyRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            newType,
            newCustom,
            newDesc
        );
        
        when(findFamilyRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(existingRelationship));
        
        when(saveFamilyRelationshipPort.saveRelationship(any(FamilyRelationship.class)))
            .thenReturn(relationshipId);

        // when
        Long result = sut.defineRelationship(command);

        // then
        assertThat(result).isEqualTo(relationshipId);
        verify(findFamilyRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
        verify(saveFamilyRelationshipPort).saveRelationship(any(FamilyRelationship.class));
    }

    @Test
    @DisplayName("defineRelationship 메서드는 command가 null이면 예외를 발생시켜야 한다")
    void given_null_command_when_define_relationship_then_throw_exception() {
        // given
        DefineFamilyRelationshipCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.defineRelationship(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("findRelationship 메서드는 query에 맞는 관계를 찾아 반환해야 한다")
    void given_valid_query_when_find_relationship_then_return_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FindFamilyRelationshipQuery query = new FindFamilyRelationshipQuery(
            familyId,
            fromMemberId,
            toMemberId
        );
        
        FamilyRelationship expectedRelationship = FamilyRelationship.withId(
            4L,
            familyId,
            fromMemberId,
            toMemberId,
            FamilyRelationshipType.PARENT,
            null,
            "설명",
            null,
            null,
            null,
            null
        );
        
        when(findFamilyRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(expectedRelationship));

        // when
        Optional<FamilyRelationship> result = sut.findRelationship(query);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedRelationship);
        verify(findFamilyRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
    }

    @Test
    @DisplayName("findRelationship 메서드는 query가 null이면 예외를 발생시켜야 한다")
    void given_null_query_when_find_relationship_then_throw_exception() {
        // given
        FindFamilyRelationshipQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findRelationship(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 query에 맞는 모든 관계를 찾아 반환해야 한다")
    void given_valid_query_when_find_all_relationships_by_member_then_return_relationships() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        FindMemberRelationshipsQuery query = new FindMemberRelationshipsQuery(
            familyId,
            fromMemberId
        );
        
        FamilyRelationship relationship1 = FamilyRelationship.withId(
            3L,
            familyId,
            fromMemberId,
            4L,
            FamilyRelationshipType.PARENT,
            null,
            "설명1",
            null,
            null,
            null,
            null
        );
        
        FamilyRelationship relationship2 = FamilyRelationship.withId(
            5L,
            familyId,
            fromMemberId,
            6L,
            FamilyRelationshipType.SIBLING,
            null,
            "설명2",
            null,
            null,
            null,
            null
        );
        
        List<FamilyRelationship> expectedRelationships = List.of(relationship1, relationship2);
        
        when(findFamilyRelationshipPort.findAllRelationshipsByMember(familyId, fromMemberId))
            .thenReturn(expectedRelationships);

        // when
        List<FamilyRelationship> result = sut.findAllRelationshipsByMember(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedRelationships);
        verify(findFamilyRelationshipPort).findAllRelationshipsByMember(familyId, fromMemberId);
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 query가 null이면 예외를 발생시켜야 한다")
    void given_null_query_when_find_all_relationships_by_member_then_throw_exception() {
        // given
        FindMemberRelationshipsQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllRelationshipsByMember(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
