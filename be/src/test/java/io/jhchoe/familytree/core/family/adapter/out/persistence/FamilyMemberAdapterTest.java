package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Adapter Test] FamilyMemberAdapterTest")
class FamilyMemberAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    private FamilyMemberAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyMemberAdapter(familyMemberJpaRepository);
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 familyId가 null이면 예외를 발생시켜야 한다")
    void throw_exception_when_family_id_is_null_for_exists_by_family_id_and_user_id() {
        Long familyId = null;
        Long userId = 1L;

        assertThatThrownBy(() -> sut.existsByFamilyIdAndUserId(familyId, userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 userId가 null이면 예외를 발생시켜야 한다")
    void throw_exception_when_user_id_is_null_for_exists_by_family_id_and_user_id() {
        Long familyId = 1L;
        Long userId = null;

        assertThatThrownBy(() -> sut.existsByFamilyIdAndUserId(familyId, userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("userId must not be null");
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 familyId, userId로 FamilyMember 조회에 성공하면 true를 응답해야 한다")
    void return_true_when_family_member_exists() {
        // given
        Long familyId = 1L;
        Long userId = 1L;
        String kakaoId = "kakaoId";
        String name = "name";
        String profileUrl = "profileUrl";
        LocalDateTime birthday = LocalDateTime.now();
        String nationality = "nationality";

        FamilyMember familyMember = FamilyMember.newMember(
            familyId,
            userId,
            name,
            profileUrl,
            birthday,
            nationality
        );
        FamilyMemberJpaEntity familyMemberJpaEntity = FamilyMemberJpaEntity.from(familyMember);
        familyMemberJpaRepository.save(familyMemberJpaEntity);

        // when & then
        assertThat(sut.existsByFamilyIdAndUserId(familyId, userId))
            .isTrue();
    }

    @Test
    @DisplayName("existsByFamilyIdAndUserId 메서드는 familyId, userId로 FamilyMember 조회에 실패면 false를 응답해야 한다")
    void return_false_when_family_member_does_not_exist() {
        // given
        Long invalidFamilyId = 1L;
        Long invalidUserId = 1L;

        // when & then
        assertThat(sut.existsByFamilyIdAndUserId(invalidFamilyId, invalidUserId))
            .isFalse();
    }

    @Test
    @DisplayName("countActiveByUserId 메서드는 활성 상태의 FamilyMember 수를 반환해야 한다")
    void return_count_of_active_family_members() {
        // given
        Long userId = 1L;
        
        // 활성 상태 FamilyMember 생성
        createFamilyMemberWithStatus(1L, userId, "Member 1", FamilyMemberStatus.ACTIVE);
        createFamilyMemberWithStatus(2L, userId, "Member 2", FamilyMemberStatus.ACTIVE);
        createFamilyMemberWithStatus(3L, userId, "Member 3", FamilyMemberStatus.ACTIVE);
        
        // 비활성 상태 FamilyMember 생성
        createFamilyMemberWithStatus(4L, userId, "Member 4", FamilyMemberStatus.BANNED);
        createFamilyMemberWithStatus(5L, userId, "Member 5", FamilyMemberStatus.SUSPENDED);
        
        // 다른 사용자의 활성 상태 FamilyMember 생성
        createFamilyMemberWithStatus(6L, 2L, "Member 6", FamilyMemberStatus.ACTIVE);

        // when
        int count = sut.countActiveByUserId(userId);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("countActiveByUserId 메서드는 활성 상태의 FamilyMember가 없으면 0을 반환해야 한다")
    void return_zero_when_no_active_family_members() {
        // given
        Long userId = 99L;

        // when
        int count = sut.countActiveByUserId(userId);

        // then
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("countActiveByUserId 메서드는 userId가 null이면 예외를 발생시켜야 한다")
    void throw_exception_when_user_id_is_null_for_count_active_by_user_id() {
        // given
        Long nullUserId = null;

        // when & then
        assertThatThrownBy(() -> sut.countActiveByUserId(nullUserId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("userId must not be null");
    }
    
    @Test
    @DisplayName("findById 메서드는 ID로 FamilyMember를 조회할 수 있다")
    void return_family_member_when_exists_by_id() {
        // given
        FamilyMember member = FamilyMember.newOwner(1L, 1L, null, "Owner", null, null, "KR");
        FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(member);
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(entity);
        
        // when
        Optional<FamilyMember> result = sut.findById(savedEntity.getId());
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getName()).isEqualTo("Owner");
        assertThat(result.get().getRole()).isEqualTo(FamilyMemberRole.OWNER);
    }
    
    @Test
    @DisplayName("findById 메서드는 존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
    void return_empty_optional_when_not_exists_by_id() {
        // given
        Long nonExistentId = 999L;
        
        // when
        Optional<FamilyMember> result = sut.findById(nonExistentId);
        
        // then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("findByFamilyIdAndUserId 메서드는 Family ID와 User ID로 구성원을 조회할 수 있다")
    void return_family_member_when_exists_by_family_id_and_user_id() {
        // given
        Long familyId = 1L;
        Long userId = 1L;
        FamilyMember member = FamilyMember.newMember(familyId, userId, "Member", null, null, "KR");
        FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(member);
        familyMemberJpaRepository.save(entity);
        
        // when
        Optional<FamilyMember> result = sut.findByFamilyIdAndUserId(familyId, userId);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getFamilyId()).isEqualTo(familyId);
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getRole()).isEqualTo(FamilyMemberRole.MEMBER);
    }
    
    @Test
    @DisplayName("findAllByFamilyId 메서드는 Family ID로 모든 구성원을 조회할 수 있다")
    void return_all_family_members_by_family_id() {
        // given
        Long familyId = 1L;
        
        // OWNER 생성
        FamilyMember owner = FamilyMember.newOwner(familyId, 1L, null, "Owner", null, null, "KR");
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(owner));
        
        // ADMIN 생성  
        FamilyMember admin = FamilyMember.withRole(familyId, 2L, "Admin", null, null, "KR", FamilyMemberRole.ADMIN);
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(admin));
        
        // MEMBER 생성
        FamilyMember member = FamilyMember.newMember(familyId, 3L, "Member", null, null, "KR");
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        
        // 다른 Family의 구성원
        FamilyMember otherFamilyMember = FamilyMember.newMember(2L, 4L, "Other", null, null, "KR");
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(otherFamilyMember));
        
        // when
        List<FamilyMember> result = sut.findAllByFamilyId(familyId);
        
        // then
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(FamilyMember::getName)
            .containsExactlyInAnyOrder("Owner", "Admin", "Member");
        assertThat(result)
            .extracting(FamilyMember::getRole)
            .containsExactlyInAnyOrder(FamilyMemberRole.OWNER, FamilyMemberRole.ADMIN, FamilyMemberRole.MEMBER);
    }
    
    @Test
    @DisplayName("modify 메서드는 FamilyMember의 역할을 성공적으로 변경한다")
    void modify_family_member_role_successfully() {
        // given
        FamilyMember member = FamilyMember.newMember(1L, 1L, "Member", null, null, "KR");
        FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(member);
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(entity);
        
        // 역할을 ADMIN으로 변경
        FamilyMember memberWithRole = FamilyMember.withId(
            savedEntity.getId(), savedEntity.getFamilyId(), savedEntity.getUserId(), null, 
            savedEntity.getName(), null, savedEntity.getProfileUrl(), savedEntity.getBirthday(),
            savedEntity.getNationality(), savedEntity.getStatus(), FamilyMemberRole.ADMIN,
            savedEntity.getCreatedBy(), savedEntity.getCreatedAt(), savedEntity.getModifiedBy(), savedEntity.getModifiedAt()
        );
        
        // when
        Long modifiedId = sut.modify(memberWithRole);
        
        // then
        assertThat(modifiedId).isEqualTo(savedEntity.getId());
        
        // 변경 확인
        FamilyMemberJpaEntity modifiedEntity = familyMemberJpaRepository.findById(modifiedId).orElseThrow();
        assertThat(modifiedEntity.getRole()).isEqualTo(FamilyMemberRole.ADMIN);
        assertThat(modifiedEntity.getName()).isEqualTo("Member"); // 다른 필드는 변경되지 않아야 함
    }
    
    @Test
    @DisplayName("modify 메서드는 FamilyMember의 상태를 성공적으로 변경한다")
    void modify_family_member_status_successfully() {
        // given
        FamilyMember member = FamilyMember.newMember(1L, 1L, "Member", null, null, "KR");
        FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(member);
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(entity);
        
        // 상태를 SUSPENDED로 변경
        FamilyMember memberWithStatus = FamilyMember.withId(
            savedEntity.getId(), savedEntity.getFamilyId(), savedEntity.getUserId(), null,
            savedEntity.getName(), null, savedEntity.getProfileUrl(), savedEntity.getBirthday(),
            savedEntity.getNationality(), FamilyMemberStatus.SUSPENDED, savedEntity.getRole(),
            savedEntity.getCreatedBy(), savedEntity.getCreatedAt(), savedEntity.getModifiedBy(), savedEntity.getModifiedAt()
        );
        
        // when
        Long modifiedId = sut.modify(memberWithStatus);
        
        // then
        assertThat(modifiedId).isEqualTo(savedEntity.getId());
        
        // 변경 확인
        FamilyMemberJpaEntity modifiedEntity = familyMemberJpaRepository.findById(modifiedId).orElseThrow();
        assertThat(modifiedEntity.getStatus()).isEqualTo(FamilyMemberStatus.SUSPENDED);
        assertThat(modifiedEntity.getRole()).isEqualTo(FamilyMemberRole.MEMBER); // 다른 필드는 변경되지 않아야 함
    }
    
    @Test
    @DisplayName("modify 메서드는 ID가 null인 FamilyMember로 호출 시 예외를 발생시킨다")
    void throw_exception_when_modify_with_null_id() {
        // given
        FamilyMember memberWithoutId = FamilyMember.newMember(1L, 1L, "Member", null, null, "KR");
        
        // when & then
        assertThatThrownBy(() -> sut.modify(memberWithoutId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyMember.id must not be null");
    }
    
    @Test
    @DisplayName("modify 메서드는 존재하지 않는 ID로 호출 시 예외를 발생시킨다")
    void throw_exception_when_modify_with_non_existent_id() {
        // given
        FamilyMember memberWithNonExistentId = FamilyMember.withId(
            999L, 1L, 1L, null, "Member", null, null, null, "KR",
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );
        
        // when & then
        assertThatThrownBy(() -> sut.modify(memberWithNonExistentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Member not found: 999");
    }

    @Test
    @DisplayName("save 메서드는 새로운 FamilyMember를 저장하고 ID를 반환한다")
    void save_family_member_successfully() {
        // given
        FamilyMember newMember = FamilyMember.newMember(1L, 1L, "New Member", "profile.jpg", null, "KR");
        
        // when
        Long savedId = sut.save(newMember);
        
        // then
        assertThat(savedId).isNotNull();
        
        // 저장 확인
        Optional<FamilyMemberJpaEntity> savedEntity = familyMemberJpaRepository.findById(savedId);
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getName()).isEqualTo("New Member");
        assertThat(savedEntity.get().getProfileUrl()).isEqualTo("profile.jpg");
        assertThat(savedEntity.get().getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        assertThat(savedEntity.get().getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("save 메서드는 OWNER 역할의 FamilyMember를 저장할 수 있다")
    void save_owner_family_member_successfully() {
        // given
        FamilyMember owner = FamilyMember.newOwner(1L, 1L, null, "Owner", null, null, "KR");
        
        // when
        Long savedId = sut.save(owner);
        
        // then
        assertThat(savedId).isNotNull();
        
        // 저장 확인
        Optional<FamilyMemberJpaEntity> savedEntity = familyMemberJpaRepository.findById(savedId);
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getName()).isEqualTo("Owner");
        assertThat(savedEntity.get().getRole()).isEqualTo(FamilyMemberRole.OWNER);
        assertThat(savedEntity.get().getStatus()).isEqualTo(FamilyMemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("save 메서드는 null FamilyMember로 호출 시 예외를 발생시킨다")
    void throw_exception_when_save_with_null_member() {
        // given
        FamilyMember nullMember = null;
        
        // when & then
        assertThatThrownBy(() -> sut.save(nullMember))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyMember must not be null");
    }

    /**
     * 테스트용 헬퍼 메서드: 특정 상태를 가진 FamilyMember를 생성합니다.
     * 참고: 이 방식은 테스트에서만 사용해야 합니다. 도메인 엔티티를 우선 생성 후 저장하는 것이 
     * setter 사용을 피하는 정상적인 방법이지만, 테스트 코드에서는 현재 도메인 클래스에 상태를 설정하는
     * 메서드가 없어 불가피하게 리플렉션을 사용했습니다.
     */
    private void createFamilyMemberWithStatus(Long familyId, Long userId, String name, FamilyMemberStatus status) {
        try {
            // 일단 기본 FamilyMember 도메인 객체 생성
            FamilyMember member = FamilyMember.newMember(
                familyId,
                userId,
                name,
                null, // profileUrl
                null, // birthday
                null  // nationality
            );
            
            // 도메인 객체로부터 JPA 엔티티 생성
            FamilyMemberJpaEntity entity = FamilyMemberJpaEntity.from(member);
            
            // 저장
            FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(entity);
            
            // 리플렉션을 사용해 status 필드에 접근하여 설정
            // 참고: 테스트 코드에서만 사용되어야 하는 방식입니다
            Field statusField = FamilyMemberJpaEntity.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(savedEntity, status);
            
            // 변경된 엔티티 저장
            familyMemberJpaRepository.save(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
