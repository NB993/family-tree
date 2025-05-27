package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyTreeNodeTest")
class FamilyTreeNodeTest {

    @Test
    @DisplayName("유효한 파라미터로 생성 시 정상적으로 객체가 생성됩니다")
    void create_success_with_valid_parameters() {
        // given
        Long memberId = 1L;
        String name = "김철수";
        Integer age = 30;
        String nationality = "KR";
        String profileUrl = "profile.jpg";
        Long userId = 10L;
        FamilyMemberRole role = FamilyMemberRole.MEMBER;
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        boolean isMe = true;
        int generation = 1;
        List<FamilyTreeRelation> relations = List.of();

        // when
        FamilyTreeNode node = new FamilyTreeNode(
            memberId, name, age, nationality, profileUrl, 
            userId, role, status, isMe, generation, relations
        );

        // then
        assertThat(node.memberId()).isEqualTo(memberId);
        assertThat(node.name()).isEqualTo(name);
        assertThat(node.age()).isEqualTo(age);
        assertThat(node.nationality()).isEqualTo(nationality);
        assertThat(node.profileUrl()).isEqualTo(profileUrl);
        assertThat(node.userId()).isEqualTo(userId);
        assertThat(node.role()).isEqualTo(role);
        assertThat(node.status()).isEqualTo(status);
        assertThat(node.isMe()).isEqualTo(isMe);
        assertThat(node.generation()).isEqualTo(generation);
        assertThat(node.relations()).isEqualTo(relations);
    }

    @Test
    @DisplayName("memberId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_member_id_is_null() {
        assertThatThrownBy(() -> new FamilyTreeNode(
            null, "김철수", 30, "KR", "profile.jpg", 
            10L, FamilyMemberRole.MEMBER, FamilyMemberStatus.ACTIVE, 
            true, 1, List.of()
        ))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("memberId must not be null");
    }

    @Test
    @DisplayName("활성 상태인 경우 isActive는 true를 반환합니다")
    void return_true_when_status_is_active() {
        // given
        FamilyTreeNode node = new FamilyTreeNode(
            1L, "김철수", 30, "KR", "profile.jpg", 
            10L, FamilyMemberRole.MEMBER, FamilyMemberStatus.ACTIVE, 
            true, 1, List.of()
        );

        // when & then
        assertThat(node.isActive()).isTrue();
    }
}
