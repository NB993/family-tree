package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FamilyMembersWithRelationshipsResponse")
class FamilyMembersWithRelationshipsResponseTest {

    @Test
    @DisplayName("생성자에서 null 파라미터 전달시 예외가 발생한다")
    void should_throw_exception_when_null_parameters() {
        // when & then
        assertThatThrownBy(() -> new FamilyMembersWithRelationshipsResponse(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("members must not be null");
    }

    @Test
    @DisplayName("관계가 없는 구성원도 올바르게 처리된다")
    void should_handle_members_without_relationship() {
        // given
        FamilyMember member = createFamilyMember(1L, "김구성원", LocalDateTime.of(1990, 1, 1, 0, 0), null, null);
        List<FamilyMember> members = List.of(member);

        // when
        FamilyMembersWithRelationshipsResponse target =
            new FamilyMembersWithRelationshipsResponse(members);

        // then
        List<FamilyMemberWithRelationshipResponse> result = target.toMemberWithRelationships();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).hasRelationship()).isFalse();
        assertThat(result.get(0).getRelationshipType()).isNull();
    }

    @Test
    @DisplayName("Family 홈 응답 변환이 올바르게 동작한다")
    void should_convert_to_family_home_response_correctly() {
        // given
        FamilyMember currentUser = createFamilyMember(1L, "나", LocalDateTime.of(1990, 1, 1, 0, 0), null, null);
        FamilyMember father = createFamilyMember(2L, "김아버지", LocalDateTime.of(1965, 12, 1, 0, 0), FamilyMemberRelationshipType.FATHER, null);
        FamilyMember mother = createFamilyMember(3L, "김어머니", LocalDateTime.of(1967, 5, 18, 0, 0), FamilyMemberRelationshipType.MOTHER, null);
        FamilyMember brother = createFamilyMember(4L, "김형", LocalDateTime.of(1985, 3, 10, 0, 0), FamilyMemberRelationshipType.ELDER_BROTHER, null);

        List<FamilyMember> members = List.of(currentUser, father, mother, brother);

        FamilyMembersWithRelationshipsResponse target =
            new FamilyMembersWithRelationshipsResponse(members);

        // when
        List<FamilyMemberWithRelationshipResponse> result = target.toMemberWithRelationships();

        // then
        assertThat(result).hasSize(4);

        // 나이순 정렬 확인 (어린 순서: 나 -> 형 -> 어머니 -> 아버지)
        assertThat(result.get(0).getMemberName()).isEqualTo("나");
        assertThat(result.get(1).getMemberName()).isEqualTo("김형");
        assertThat(result.get(2).getMemberName()).isEqualTo("김어머니");
        assertThat(result.get(3).getMemberName()).isEqualTo("김아버지");

        // 관계 정보 확인 (FamilyMember의 relationshipType 기반)
        assertThat(result.get(0).hasRelationship()).isFalse();
        assertThat(result.get(1).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.ELDER_BROTHER);
        assertThat(result.get(2).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.MOTHER);
        assertThat(result.get(3).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.FATHER);
    }

    @Test
    @DisplayName("ACTIVE 상태의 구성원만 반환된다")
    void should_return_only_active_members() {
        // given
        FamilyMember activeMember = createFamilyMember(1L, "활성멤버", LocalDateTime.of(1990, 1, 1, 0, 0), null, null);
        FamilyMember suspendedMember = createSuspendedMember(2L, "정지멤버", LocalDateTime.of(1990, 1, 1, 0, 0));

        FamilyMembersWithRelationshipsResponse target =
            new FamilyMembersWithRelationshipsResponse(List.of(activeMember, suspendedMember));

        // when
        List<FamilyMemberWithRelationshipResponse> result = target.toMemberWithRelationships();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMemberName()).isEqualTo("활성멤버");
    }

    // Helper methods

    private FamilyMember createFamilyMember(Long id, String name, LocalDateTime birthday,
                                             FamilyMemberRelationshipType relationshipType, String customRelationship) {
        return FamilyMember.withId(
            id, 1L, 1L, name, relationshipType, customRelationship, "https://example.com/profile.jpg", birthday,
            null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }

    private FamilyMember createSuspendedMember(Long id, String name, LocalDateTime birthday) {
        return FamilyMember.withId(
            id, 1L, 1L, name, null, null, "https://example.com/profile.jpg", birthday,
            null, FamilyMemberStatus.SUSPENDED, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
}
