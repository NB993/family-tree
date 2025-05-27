package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyTreeQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyTreePort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyTreeServiceTest")
class FindFamilyTreeServiceTest {

    @InjectMocks
    private FindFamilyTreeService findFamilyTreeService;
    
    @Mock
    private FindFamilyTreePort findFamilyTreePort;

    @Test
    @DisplayName("유효한 가족 ID로 조회 시 가족트리를 성공적으로 반환합니다")
    void find_family_tree_success_with_valid_family_id() {
        // given
        Long familyId = 1L;
        FindFamilyTreeQuery query = FindFamilyTreeQuery.withDefaults(familyId);
        
        Family family = Family.withId(
            familyId, "테스트 가족", "설명", "profile.jpg", 
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
        
        FamilyMember member = FamilyMember.existingMember(
            1L, familyId, 100L, "김철수", "profile.jpg", LocalDateTime.of(1990, 5, 15, 0, 0), "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );

        // Mocking: 가족 정보 조회 성공
        when(findFamilyTreePort.findFamily(familyId)).thenReturn(Optional.of(family));
        // Mocking: 가족 구성원 조회 성공
        when(findFamilyTreePort.findActiveFamilyMembers(familyId)).thenReturn(List.of(member));
        // Mocking: 관계 조회 (빈 목록)
        when(findFamilyTreePort.findFamilyMemberRelationships(familyId)).thenReturn(List.of());

        // when
        FamilyTree result = findFamilyTreeService.findFamilyTree(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getCenterMember().memberId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("query가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_query_is_null() {
        assertThatThrownBy(() -> findFamilyTreeService.findFamilyTree(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
