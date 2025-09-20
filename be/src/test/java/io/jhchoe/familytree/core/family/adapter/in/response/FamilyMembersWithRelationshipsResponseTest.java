package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FamilyMembersWithRelationshipsResponse")
class FamilyMembersWithRelationshipsResponseTest {
    
    @Test
    @DisplayName("생성자에서 null 파라미터 전달시 예외가 발생한다")
    void should_throw_exception_when_null_parameters() {
        // given
        List<FamilyMember> members = List.of();
        List<FamilyMemberRelationship> relationships = List.of();
        
        // when & then
        assertThatThrownBy(() -> new FamilyMembersWithRelationshipsResponse(null, relationships))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("members must not be null");
    }
    
    @Test
    @DisplayName("relationships가 null인 경우 빈 리스트로 처리된다")
    void should_handle_null_relationships_as_empty_list() {
        // given
        FamilyMember member = createFamilyMember(1L, "김구성원", LocalDateTime.of(1990, 1, 1, 0, 0));
        List<FamilyMember> members = List.of(member);
        
        // when
        FamilyMembersWithRelationshipsResponse target = 
            new FamilyMembersWithRelationshipsResponse(members, null);
        
        // then
        Long currentUserId = 2L;
        List<FamilyMemberWithRelationshipResponse> result = 
            target.toMemberWithRelationships(currentUserId);
        
        assertThat(result).hasSize(1);
            assertThat(result.get(0).hasRelationship()).isFalse();
        assertThat(result.get(0).getRelationshipType()).isNull();
    }
    
    @Test
    @DisplayName("특정 구성원 간 관계 조회가 정상 동작한다")
    void should_find_relationship_between_members() {
        // given
        FamilyMember member1 = createFamilyMember(1L, "김아버지", LocalDateTime.of(1969, 12, 1, 0, 0));
        FamilyMember member2 = createFamilyMember(2L, "김아들", LocalDateTime.of(1995, 5, 15, 0, 0));
        FamilyMemberRelationship relationship = createRelationship(1L, 2L, FamilyMemberRelationshipType.FATHER);
        
        FamilyMembersWithRelationshipsResponse target = 
            new FamilyMembersWithRelationshipsResponse(List.of(member1, member2), List.of(relationship));
        
        // when
        Optional<FamilyMemberRelationship> result = target.findRelationship(1L, 2L);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.FATHER);
        assertThat(result.get().getFromMemberId()).isEqualTo(1L);
        assertThat(result.get().getToMemberId()).isEqualTo(2L);
    }
    
    @Test
    @DisplayName("Family 홈 응답 변환이 올바르게 동작한다")
    void should_convert_to_family_home_response_correctly() {
        // given
        Long currentUserId = 1L;
        
        FamilyMember currentUser = createFamilyMember(1L, "나", LocalDateTime.of(1990, 1, 1, 0, 0));
        FamilyMember father = createFamilyMember(2L, "김아버지", LocalDateTime.of(1965, 12, 1, 0, 0));
        FamilyMember mother = createFamilyMember(3L, "김어머니", LocalDateTime.of(1967, 5, 18, 0, 0));
        FamilyMember brother = createFamilyMember(4L, "김형", LocalDateTime.of(1985, 3, 10, 0, 0));
        
        List<FamilyMember> members = List.of(currentUser, father, mother, brother);
        
        FamilyMemberRelationship fatherRel = createRelationship(1L, 2L, FamilyMemberRelationshipType.FATHER);
        FamilyMemberRelationship motherRel = createRelationship(1L, 3L, FamilyMemberRelationshipType.MOTHER);
        FamilyMemberRelationship brotherRel = createRelationship(1L, 4L, FamilyMemberRelationshipType.ELDER_BROTHER);
        
        List<FamilyMemberRelationship> relationships = List.of(fatherRel, motherRel, brotherRel);
        
        FamilyMembersWithRelationshipsResponse target = 
            new FamilyMembersWithRelationshipsResponse(members, relationships);
        
        // when
        List<FamilyMemberWithRelationshipResponse> result = 
            target.toMemberWithRelationships(currentUserId);
        
        // then
        assertThat(result).hasSize(3); // 본인 제외
        
        // 나이순 정렬 확인 (어린 순서: 형 -> 어머니 -> 아버지)
        assertThat(result.get(0).getMemberName()).isEqualTo("김형");
        assertThat(result.get(1).getMemberName()).isEqualTo("김어머니");
        assertThat(result.get(2).getMemberName()).isEqualTo("김아버지");
        
        // 관계 정보 확인
        assertThat(result.get(0).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.ELDER_BROTHER);
        assertThat(result.get(1).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.MOTHER);
        assertThat(result.get(2).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.FATHER);
    }
    
    // Helper methods
    
    private FamilyMember createFamilyMember(Long id, String name, LocalDateTime birthday) {
        return FamilyMember.withId(
            id, 1L, 1L, null, name, null, "https://example.com/profile.jpg", birthday, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
    
    private FamilyMember createSuspendedMember(Long id, String name, LocalDateTime birthday) {
        return FamilyMember.withId(
            id, 1L, 1L, null, name, null, "https://example.com/profile.jpg", birthday, "KR",
            FamilyMemberStatus.SUSPENDED, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
    
    private FamilyMemberRelationship createRelationship(Long fromId, Long toId, FamilyMemberRelationshipType type) {
        return FamilyMemberRelationship.withId(
            1L, 1L, fromId, toId, type, null, null,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
}
