package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.helper.AdapterTestBase;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Unit Test] FamilyTreeAdapterTest")
class FamilyTreeAdapterTest extends AdapterTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;
    
    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;
    
    @Autowired
    private FamilyMemberRelationshipJpaRepository familyMemberRelationshipJpaRepository;
    
    private FamilyAdapter sut;
    
    @BeforeEach
    void setUp() {
        sut = new FamilyAdapter(familyJpaRepository, familyMemberJpaRepository, familyMemberRelationshipJpaRepository);
    }
    
    @Test
    @DisplayName("가족 ID로 조회 시 Family 객체를 반환합니다")
    void find_family_returns_family_when_exists() {
        // given
        Family family = Family.newFamily("테스트가족", "설명", "프로필URL");
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        // when
        Optional<Family> result = sut.findFamily(savedEntity.getId());
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트가족");
    }
    
    @Test
    @DisplayName("존재하지 않는 가족 ID로 조회 시 빈 Optional을 반환합니다")
    void find_family_returns_empty_when_not_exists() {
        // given
        Long nonExistentFamilyId = 999L;
        
        // when
        Optional<Family> result = sut.findFamily(nonExistentFamilyId);
        
        // then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("가족의 활성 구성원들을 조회합니다")
    void find_active_family_members_returns_active_members_only() {
        // given
        Family family = Family.newFamily("테스트가족", "설명", "프로필URL");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        // 활성 구성원
        FamilyMember activeMember = FamilyMember.withRole(
            savedFamily.getId(), 1L, "활성구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberRole.MEMBER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(activeMember));
        
        // 비활성 구성원 - 먼저 활성으로 생성 후 상태 변경
        FamilyMember memberForInactive = FamilyMember.withRole(
            savedFamily.getId(), 2L, "비활성구성원", "profile2.jpg", LocalDateTime.now(),
            "KR", FamilyMemberRole.MEMBER
        );
        FamilyMember inactiveMember = memberForInactive.updateStatus(FamilyMemberStatus.BANNED);
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(inactiveMember));
        
        // when
        List<FamilyMember> result = sut.findActiveFamilyMembers(savedFamily.getId());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("활성구성원");
        assertThat(result.get(0).getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("구성원 ID로 조회 시 FamilyMember 객체를 반환합니다")
    void find_family_member_returns_member_when_exists() {
        // given
        Family family = Family.newFamily("테스트가족", "설명", "프로필URL");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        FamilyMember member = FamilyMember.withRole(
            savedFamily.getId(), 1L, "구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberRole.MEMBER
        );
        FamilyMemberJpaEntity savedMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        
        // when
        Optional<FamilyMember> result = sut.findFamilyMember(savedMember.getId());
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("구성원");
    }
    
    @Test
    @DisplayName("가족의 모든 구성원 관계를 조회합니다")
    void find_family_member_relationships_returns_all_relationships() {
        // given
        Family family = Family.newFamily("테스트가족", "설명", "프로필URL");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        // 구성원 생성
        FamilyMember member1 = FamilyMember.withRole(
            savedFamily.getId(), 1L, "구성원1", "profile1.jpg", LocalDateTime.now(),
            "KR", FamilyMemberRole.MEMBER
        );
        FamilyMemberJpaEntity savedMember1 = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member1));
        
        FamilyMember member2 = FamilyMember.withRole(
            savedFamily.getId(), 2L, "구성원2", "profile2.jpg", LocalDateTime.now(),
            "KR", FamilyMemberRole.MEMBER
        );
        FamilyMemberJpaEntity savedMember2 = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member2));
        
        // 관계 생성
        FamilyMemberRelationship relationship = FamilyMemberRelationship.newRelationship(
            savedFamily.getId(), savedMember1.getId(), savedMember2.getId(),
            FamilyMemberRelationshipType.PARENT, "부모", "설명"
        );
        familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(relationship));
        
        // when
        List<FamilyMemberRelationship> result = sut.findFamilyMemberRelationships(savedFamily.getId());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.PARENT);
    }
    
    @Test
    @DisplayName("사용자 ID로 가족 구성원을 조회합니다")
    void find_family_member_by_user_id_returns_member_when_exists() {
        // given
        Family family = Family.newFamily("테스트가족", "설명", "프로필URL");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        FamilyMember member = FamilyMember.withRole(
            savedFamily.getId(), 1L, "구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberRole.MEMBER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        
        // when
        Optional<FamilyMember> result = sut.findFamilyMemberByUserId(savedFamily.getId(), 1L);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("구성원");
        assertThat(result.get().getUserId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 Optional을 반환합니다")
    void find_family_member_by_user_id_returns_empty_when_not_exists() {
        // given
        Family family = Family.newFamily("테스트가족", "설명", "프로필URL");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        // when
        Optional<FamilyMember> result = sut.findFamilyMemberByUserId(savedFamily.getId(), 999L);
        
        // then
        assertThat(result).isEmpty();
    }
}
