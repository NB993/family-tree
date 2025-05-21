package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FindFamilyMembersRoleQueryTest")
class FindFamilyMembersRoleQueryTest {

    @Test
    @DisplayName("유효한 입력값으로 쿼리 객체를 생성할 수 있다")
    void when_valid_input_then_create_query() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        
        // when
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(
            familyId, currentUserId
        );
        
        // then
        assertThat(query.getFamilyId()).isEqualTo(familyId);
        assertThat(query.getCurrentUserId()).isEqualTo(currentUserId);
    }
    
    @Test
    @DisplayName("Family ID가 null이면 예외가 발생한다")
    void when_family_id_is_null_then_throw_exception() {
        // given
        Long familyId = null;
        Long currentUserId = 2L;
        
        // when & then
        assertThatThrownBy(() -> {
            new FindFamilyMembersRoleQuery(familyId, currentUserId);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Family ID가 필요합니다");
    }
    
    @Test
    @DisplayName("Family ID가 0 이하이면 예외가 발생한다")
    void when_family_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 0L;
        Long currentUserId = 2L;
        
        // when & then
        assertThatThrownBy(() -> {
            new FindFamilyMembersRoleQuery(familyId, currentUserId);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 Family ID가 필요합니다");
    }
    
    @Test
    @DisplayName("현재 사용자 ID가 null이면 예외가 발생한다")
    void when_current_user_id_is_null_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long currentUserId = null;
        
        // when & then
        assertThatThrownBy(() -> {
            new FindFamilyMembersRoleQuery(familyId, currentUserId);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 현재 사용자 ID가 필요합니다");
    }
    
    @Test
    @DisplayName("현재 사용자 ID가 0 이하이면 예외가 발생한다")
    void when_current_user_id_is_less_than_or_equal_to_zero_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long currentUserId = 0L;
        
        // when & then
        assertThatThrownBy(() -> {
            new FindFamilyMembersRoleQuery(familyId, currentUserId);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("유효한 현재 사용자 ID가 필요합니다");
    }
}
