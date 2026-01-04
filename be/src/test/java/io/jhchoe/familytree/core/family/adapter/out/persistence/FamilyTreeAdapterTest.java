package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Adapter Test] FamilyTreeAdapterTest")
class FamilyTreeAdapterTest extends TestcontainersDataJpaTestBase {

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
        Family family = FamilyFixture.newFamily("테스트가족", "설명", "프로필URL", true);
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
        Family family = FamilyFixture.newFamily("테스트가족", "설명", "프로필URL", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        // 활성 구성원
        FamilyMember activeMember = FamilyMemberFixture.newMember(savedFamily.getId(), 1L);
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(activeMember));

        // 비활성 구성원 - 먼저 활성으로 생성 후 상태 변경
        FamilyMember memberForInactive = FamilyMemberFixture.newMember(savedFamily.getId(), 2L);
        FamilyMember inactiveMember = memberForInactive.updateStatus(FamilyMemberStatus.BANNED);
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(inactiveMember));
        
        // when
        List<FamilyMember> result = sut.findActiveFamilyMembers(savedFamily.getId());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("구성원 ID로 조회 시 FamilyMember 객체를 반환합니다")
    void find_family_member_returns_member_when_exists() {
        // given
        Family family = FamilyFixture.newFamily("테스트가족", "설명", "프로필URL", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));

        FamilyMember member = FamilyMemberFixture.newMember(savedFamily.getId(), 1L);
        FamilyMemberJpaEntity savedMember = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        
        // when
        Optional<FamilyMember> result = sut.findFamilyMember(savedMember.getId());
        
        // then
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("가족의 모든 구성원 관계를 조회합니다")
    void find_family_member_relationships_returns_all_relationships() {
        // given
        Family family = FamilyFixture.newFamily("테스트가족", "설명", "프로필URL", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));

        // 구성원 생성
        FamilyMember member1 = FamilyMemberFixture.newMember(savedFamily.getId(), 1L);
        FamilyMemberJpaEntity savedMember1 = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member1));

        FamilyMember member2 = FamilyMemberFixture.newMember(savedFamily.getId(), 2L);
        FamilyMemberJpaEntity savedMember2 = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member2));
        
        // 관계 생성
        FamilyMemberRelationship relationship = FamilyMemberRelationship.newRelationship(
            savedFamily.getId(), savedMember1.getId(), savedMember2.getId(),
            FamilyMemberRelationshipType.FATHER, "부모", "설명"
        );
        familyMemberRelationshipJpaRepository.save(FamilyMemberRelationshipJpaEntity.from(relationship));
        
        // when
        List<FamilyMemberRelationship> result = sut.findFamilyMemberRelationships(savedFamily.getId());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRelationshipType()).isEqualTo(FamilyMemberRelationshipType.FATHER);
    }
    
    @Test
    @DisplayName("사용자 ID로 가족 구성원을 조회합니다")
    void find_family_member_by_user_id_returns_member_when_exists() {
        // given
        Family family = FamilyFixture.newFamily("테스트가족", "설명", "프로필URL", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));

        FamilyMember member = FamilyMemberFixture.newMember(savedFamily.getId(), 1L);
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        
        // when
        Optional<FamilyMember> result = sut.findFamilyMemberByUserId(savedFamily.getId(), 1L);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 Optional을 반환합니다")
    void find_family_member_by_user_id_returns_empty_when_not_exists() {
        // given
        Family family = FamilyFixture.newFamily("테스트가족", "설명", "프로필URL", true);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        // when
        Optional<FamilyMember> result = sut.findFamilyMemberByUserId(savedFamily.getId(), 999L);
        
        // then
        assertThat(result).isEmpty();
    }
}
