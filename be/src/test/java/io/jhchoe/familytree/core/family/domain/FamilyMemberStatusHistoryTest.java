package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberStatusHistoryTest")
class FamilyMemberStatusHistoryTest {

    @Test
    @DisplayName("create 메서드로 새로운 상태 변경 이력을 생성할 수 있다")
    void create_creates_new_status_history() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when
        FamilyMemberStatusHistory history = FamilyMemberStatusHistory.create(
            familyId, memberId, status, reason
        );
        
        // then
        assertThat(history.getId()).isNull();
        assertThat(history.getFamilyId()).isEqualTo(familyId);
        assertThat(history.getMemberId()).isEqualTo(memberId);
        assertThat(history.getStatus()).isEqualTo(status);
        assertThat(history.getReason()).isEqualTo(reason);
        assertThat(history.getCreatedBy()).isNull();
        assertThat(history.getCreatedAt()).isNull();
    }
    
    @Test
    @DisplayName("withId 메서드로 모든 필드가 채워진 상태 변경 이력을 생성할 수 있다")
    void with_id_creates_status_history_with_all_fields() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long memberId = 3L;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        Long createdBy = 4L;
        LocalDateTime createdAt = LocalDateTime.now();
        
        // when
        FamilyMemberStatusHistory history = FamilyMemberStatusHistory.withId(
            id, familyId, memberId, status, reason, createdBy, createdAt
        );
        
        // then
        assertThat(history.getId()).isEqualTo(id);
        assertThat(history.getFamilyId()).isEqualTo(familyId);
        assertThat(history.getMemberId()).isEqualTo(memberId);
        assertThat(history.getStatus()).isEqualTo(status);
        assertThat(history.getReason()).isEqualTo(reason);
        assertThat(history.getCreatedBy()).isEqualTo(createdBy);
        assertThat(history.getCreatedAt()).isEqualTo(createdAt);
    }
    
    @Test
    @DisplayName("Family ID가 null이면 예외가 발생한다")
    void when_family_id_is_null_then_throw_exception() {
        // given
        Long familyId = null;
        Long memberId = 2L;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            FamilyMemberStatusHistory.create(familyId, memberId, status, reason);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("familyId must not be null");
    }
    
    @Test
    @DisplayName("Member ID가 null이면 예외가 발생한다")
    void when_member_id_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = null;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            FamilyMemberStatusHistory.create(familyId, memberId, status, reason);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("memberId must not be null");
    }
    
    @Test
    @DisplayName("Status가 null이면 예외가 발생한다")
    void when_status_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        FamilyMemberStatus status = null;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            FamilyMemberStatusHistory.create(familyId, memberId, status, reason);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("status must not be null");
    }
    
    @Test
    @DisplayName("withId 메서드의 ID가 null이면 예외가 발생한다")
    void when_id_is_null_in_with_id_then_throw_exception() {
        // given
        Long id = null;
        Long familyId = 2L;
        Long memberId = 3L;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        Long createdBy = 4L;
        LocalDateTime createdAt = LocalDateTime.now();
        
        // when & then
        assertThatThrownBy(() -> {
            FamilyMemberStatusHistory.withId(id, familyId, memberId, status, reason, createdBy, createdAt);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("id must not be null");
    }
}
