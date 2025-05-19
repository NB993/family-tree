package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.helper.AdapterTestBase;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Unit Test] FamilyMemberAdapterTest")
class FamilyMemberAdapterTest extends AdapterTestBase {

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
