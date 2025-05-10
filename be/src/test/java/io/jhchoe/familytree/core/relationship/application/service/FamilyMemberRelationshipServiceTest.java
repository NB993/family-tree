package io.jhchoe.familytree.core.relationship.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.relationship.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.relationship.application.port.out.FindFamilyRelationshipPort;
import io.jhchoe.familytree.core.relationship.application.port.out.SaveFamilyRelationshipPort;
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

@DisplayName("[Unit Test] FamilyRelationshipService")
@ExtendWith(MockitoExtension.class)
class FamilyMemberRelationshipServiceTest {

    @Mock
    private SaveFamilyRelationshipPort saveFamilyRelationshipPort;

    @Mock
    private FindFamilyRelationshipPort findFamilyRelationshipPort;

    @InjectMocks
    private FindFamilyMemberRelationshipService sut;

    @Test
    @DisplayName("saveRelationship 메서드는 기존 관계가 없을 때 새 관계를 생성해야 한다")
    void given_no_existing_relationship_when_save_relationship_then_create_new_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "새로운 관계";
        Long expectedId = 4L;
        
        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );
        
        when(findFamilyRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.empty());
        
        when(saveFamilyRelationshipPort.saveRelationship(any(FamilyMemberRelationship.class)))
            .thenReturn(expectedId);

        // when
        Long result = sut.saveRelationship(command);

        // then
        assertThat(result).isEqualTo(expectedId);
        verify(findFamilyRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
        verify(saveFamilyRelationshipPort).saveRelationship(any(FamilyMemberRelationship.class));
    }

    @Test
    @DisplayName("saveRelationship 메서드는 기존 관계가 있을 때 관계를 업데이트해야 한다")
    void given_existing_relationship_when_save_relationship_then_update_relationship() {
        // given
        Long relationshipId = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType originalType = FamilyMemberRelationshipType.PARENT;
        FamilyMemberRelationshipType newType = FamilyMemberRelationshipType.GRANDPARENT;
        String originalCustom = null;
        String newCustom = null;
        String originalDesc = "원래 설명";
        String newDesc = "새로운 설명";
        
        FamilyMemberRelationship existingRelationship = FamilyMemberRelationship.withId(
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
        
        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            newType,
            newCustom,
            newDesc
        );
        
        when(findFamilyRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(existingRelationship));
        
        when(saveFamilyRelationshipPort.saveRelationship(any(FamilyMemberRelationship.class)))
            .thenReturn(relationshipId);

        // when
        Long result = sut.saveRelationship(command);

        // then
        assertThat(result).isEqualTo(relationshipId);
        verify(findFamilyRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
        verify(saveFamilyRelationshipPort).saveRelationship(any(FamilyMemberRelationship.class));
    }

    @Test
    @DisplayName("saveRelationship 메서드는 command가 null이면 예외를 발생시켜야 한다")
    void given_null_command_when_save_relationship_then_throw_exception() {
        // given
        SaveFamilyMemberRelationshipCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.saveRelationship(command))
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
        FindFamilyMemberRelationshipQuery query = new FindFamilyMemberRelationshipQuery(
            familyId,
            fromMemberId,
            toMemberId
        );
        
        FamilyMemberRelationship expectedRelationship = FamilyMemberRelationship.withId(
            4L,
            familyId,
            fromMemberId,
            toMemberId,
            FamilyMemberRelationshipType.PARENT,
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
        Optional<FamilyMemberRelationship> result = sut.findRelationship(query);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedRelationship);
        verify(findFamilyRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
    }

    @Test
    @DisplayName("findRelationship 메서드는 query가 null이면 예외를 발생시켜야 한다")
    void given_null_query_when_find_relationship_then_throw_exception() {
        // given
        FindFamilyMemberRelationshipQuery query = null;

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
        FindFamilyMemberRelationshipsQuery query = new FindFamilyMemberRelationshipsQuery(
            familyId,
            fromMemberId
        );
        
        FamilyMemberRelationship relationship1 = FamilyMemberRelationship.withId(
            3L,
            familyId,
            fromMemberId,
            4L,
            FamilyMemberRelationshipType.PARENT,
            null,
            "설명1",
            null,
            null,
            null,
            null
        );
        
        FamilyMemberRelationship relationship2 = FamilyMemberRelationship.withId(
            5L,
            familyId,
            fromMemberId,
            6L,
            FamilyMemberRelationshipType.SIBLING,
            null,
            "설명2",
            null,
            null,
            null,
            null
        );
        
        List<FamilyMemberRelationship> expectedRelationships = List.of(relationship1, relationship2);
        
        when(findFamilyRelationshipPort.findAllRelationshipsByMember(familyId, fromMemberId))
            .thenReturn(expectedRelationships);

        // when
        List<FamilyMemberRelationship> result = sut.findAllRelationshipsByMember(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedRelationships);
        verify(findFamilyRelationshipPort).findAllRelationshipsByMember(familyId, fromMemberId);
    }

    @Test
    @DisplayName("findAllRelationshipsByMember 메서드는 query가 null이면 예외를 발생시켜야 한다")
    void given_null_query_when_find_all_relationships_by_member_then_throw_exception() {
        // given
        FindFamilyMemberRelationshipsQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllRelationshipsByMember(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
