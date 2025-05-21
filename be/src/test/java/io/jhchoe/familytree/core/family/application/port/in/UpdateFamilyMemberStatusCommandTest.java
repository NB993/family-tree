package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] UpdateFamilyMemberStatusCommandTest")
class UpdateFamilyMemberStatusCommandTest {

    @Test
    @DisplayName("유효한 입력값으로 커맨드 객체를 생성할 수 있다")
    void when_valid_input_then_create_command() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when
        UpdateFamilyMemberStatusCommand command = new UpdateFamilyMemberStatusCommand(
            familyId, memberId, currentUserId, newStatus, reason
        );
        
        // then
        assertThat(command.getFamilyId()).isEqualTo(familyId);
        assertThat(command.getMemberId()).isEqualTo(memberId);
        assertThat(command.getCurrentUserId()).isEqualTo(currentUserId);
        assertThat(command.getNewStatus()).isEqualTo(newStatus);
        assertThat(command.getReason()).isEqualTo(reason);
    }
    
    @Test
    @DisplayName("Family ID가 null이면 예외가 발생한다")
    void when_family_id_is_null_then_throw_exception() {
        // given
        Long familyId = null;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Family ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Family ID가 0 이하이면 예외가 발생한다")
    void when_family_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 0L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Family ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Member ID가 null이면 예외가 발생한다")
    void when_member_id_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = null;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Member ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Member ID가 0 이하이면 예외가 발생한다")
    void when_member_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 0L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Member ID가 필요합니다");
    }
    
    @Test
    @DisplayName("현재 사용자 ID가 null이면 예외가 발생한다")
    void when_current_user_id_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = null;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 현재 사용자 ID가 필요합니다");
    }
    
    @Test
    @DisplayName("현재 사용자 ID가 0 이하이면 예외가 발생한다")
    void when_current_user_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 0L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 현재 사용자 ID가 필요합니다");
    }
    
    @Test
    @DisplayName("새 상태가 null이면 예외가 발생한다")
    void when_new_status_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = null;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("변경할 상태를 지정해야 합니다");
    }
    
    @Test
    @DisplayName("자신의 상태는 변경할 수 없다")
    void when_changing_self_status_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        Long currentUserId = 2L; // 동일한 ID
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        // when & then
        assertThatThrownBy(() -> {
            new UpdateFamilyMemberStatusCommand(familyId, memberId, currentUserId, newStatus, reason);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("자신의 상태는 변경할 수 없습니다");
    }
}
