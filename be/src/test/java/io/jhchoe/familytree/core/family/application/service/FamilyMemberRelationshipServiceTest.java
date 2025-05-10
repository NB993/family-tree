package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
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
    private FindFamilyMemberRelationshipPort findFamilyMemberRelationshipPort;

    @InjectMocks
    private FindFamilyMemberRelationshipService sut;

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
        
        when(findFamilyMemberRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(expectedRelationship));

        // when
        Optional<FamilyMemberRelationship> result = sut.findRelationship(query);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedRelationship);
        verify(findFamilyMemberRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
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
        
        when(findFamilyMemberRelationshipPort.findAllRelationshipsByMember(familyId, fromMemberId))
            .thenReturn(expectedRelationships);

        // when
        List<FamilyMemberRelationship> result = sut.findAllRelationshipsByMember(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedRelationships);
        verify(findFamilyMemberRelationshipPort).findAllRelationshipsByMember(familyId, fromMemberId);
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
