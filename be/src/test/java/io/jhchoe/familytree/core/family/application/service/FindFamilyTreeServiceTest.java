package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyTreeQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyTreePort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyTree;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyTreeServiceTest")
class FindFamilyTreeServiceTest {

    @InjectMocks
    private FindFamilyTreeService findFamilyTreeService;
    
    @Mock
    private FindFamilyTreePort findFamilyTreePort;
    
    @Test
    @DisplayName("유효한 조건으로 가족트리 조회 시 FamilyTree 객체를 반환합니다")
    void find_family_tree_returns_tree_when_valid_query() {
        // given
        Long familyId = 1L;
        Long centerMemberId = 1L;
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(familyId, centerMemberId, 3);
        
        Family family = Family.withId(familyId, "테스트가족", "설명", "프로필URL", true, 1L, LocalDateTime.now(), 1L, LocalDateTime.now());
        
        FamilyMember centerMember = FamilyMember.withId(
            centerMemberId, familyId, 1L, "중심구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN, 1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        FamilyMember childMember = FamilyMember.withId(
            2L, familyId, 2L, "자녀", "child.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, 1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        List<FamilyMember> familyMembers = List.of(centerMember, childMember);
        
        FamilyMemberRelationship relationship = FamilyMemberRelationship.withId(
            1L, familyId, centerMemberId, 2L, FamilyMemberRelationshipType.SON,
            null, "자녀", 1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        List<FamilyMemberRelationship> relationships = List.of(relationship);
        
        // Mocking: 가족 존재 확인
        when(findFamilyTreePort.findFamily(familyId)).thenReturn(Optional.of(family));
        
        // Mocking: 가족의 활성 구성원 조회
        when(findFamilyTreePort.findActiveFamilyMembers(familyId)).thenReturn(familyMembers);
        
        // Mocking: 가족 구성원 관계 조회
        when(findFamilyTreePort.findFamilyMemberRelationships(familyId)).thenReturn(relationships);
        
        // when
        FamilyTree result = findFamilyTreeService.findFamilyTree(query);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getCenterMember().memberId()).isEqualTo(centerMemberId);
        assertThat(result.getAllMembers()).hasSize(2);
    }
    
    @Test
    @DisplayName("존재하지 않는 가족 ID로 조회 시 예외가 발생합니다")
    void find_family_tree_throws_exception_when_family_not_found() {
        // given
        Long familyId = 999L;
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(familyId, null, 3);
        
        // Mocking: 가족이 존재하지 않음
        when(findFamilyTreePort.findFamily(familyId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> findFamilyTreeService.findFamilyTree(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.FAMILY_NOT_FOUND);
    }
    
    @Test
    @DisplayName("구성원이 없는 가족의 경우 빈 트리를 반환합니다")
    void find_family_tree_returns_empty_tree_when_no_members() {
        // given
        Long familyId = 1L;
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(familyId, null, 3);
        
        Family family = Family.withId(familyId, "빈가족", "설명", "프로필URL", true, 1L, LocalDateTime.now(), 1L, LocalDateTime.now());
        
        // Mocking: 가족 존재하지만 구성원 없음
        when(findFamilyTreePort.findFamily(familyId)).thenReturn(Optional.of(family));
        when(findFamilyTreePort.findActiveFamilyMembers(familyId)).thenReturn(List.of());
        
        // when
        FamilyTree result = findFamilyTreeService.findFamilyTree(query);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("지정된 중심 구성원이 가족에 속하지 않는 경우 예외가 발생합니다")
    void find_family_tree_throws_exception_when_center_member_not_in_family() {
        // given
        Long familyId = 1L;
        Long invalidCenterMemberId = 999L;
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(familyId, invalidCenterMemberId, 3);
        
        Family family = Family.withId(familyId, "테스트가족", "설명", "프로필URL", true, 1L, LocalDateTime.now(), 1L, LocalDateTime.now());
        
        FamilyMember member = FamilyMember.withId(
            1L, familyId, 1L, "구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, 1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        // Mocking: 가족 존재하지만 지정된 중심 구성원이 없음
        when(findFamilyTreePort.findFamily(familyId)).thenReturn(Optional.of(family));
        when(findFamilyTreePort.findActiveFamilyMembers(familyId)).thenReturn(List.of(member));
        // findFamilyMemberRelationships는 예외가 발생하기 전에 호출되지 않으므로 제거
        
        // when & then
        assertThatThrownBy(() -> findFamilyTreeService.findFamilyTree(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.MEMBER_NOT_FOUND);
    }
    
    @Test
    @DisplayName("중심 구성원을 지정하지 않은 경우 첫 번째 구성원이 중심이 됩니다")
    void find_family_tree_uses_first_member_as_center_when_center_not_specified() {
        // given
        Long familyId = 1L;
        FindFamilyTreeQuery query = new FindFamilyTreeQuery(familyId, null, 3); // 중심 구성원 지정 안함
        
        Family family = Family.withId(familyId, "테스트가족", "설명", "프로필URL", true, 1L, LocalDateTime.now(), 1L, LocalDateTime.now());
        
        FamilyMember firstMember = FamilyMember.withId(
            1L, familyId, 1L, "첫번째구성원", "profile1.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, 1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        FamilyMember secondMember = FamilyMember.withId(
            2L, familyId, 2L, "두번째구성원", "profile2.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER, 1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        List<FamilyMember> familyMembers = List.of(firstMember, secondMember);
        
        // Mocking: 가족과 구성원들 존재
        when(findFamilyTreePort.findFamily(familyId)).thenReturn(Optional.of(family));
        when(findFamilyTreePort.findActiveFamilyMembers(familyId)).thenReturn(familyMembers);
        when(findFamilyTreePort.findFamilyMemberRelationships(familyId)).thenReturn(List.of());
        
        // when
        FamilyTree result = findFamilyTreeService.findFamilyTree(query);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getCenterMember().memberId()).isEqualTo(1L); // 첫 번째 구성원이 중심
        assertThat(result.getCenterMember().name()).isEqualTo("첫번째구성원");
    }
    
    @Test
    @DisplayName("쿼리 객체가 null인 경우 예외가 발생합니다")
    void find_family_tree_throws_exception_when_query_is_null() {
        // when & then
        assertThatThrownBy(() -> findFamilyTreeService.findFamilyTree(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
