package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.helper.AdapterTestBase;
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
        createFamilyMember(1L, userId, FamilyMemberStatus.ACTIVE);
        createFamilyMember(2L, userId, FamilyMemberStatus.ACTIVE);
        createFamilyMember(3L, userId, FamilyMemberStatus.ACTIVE);
        
        // 비활성 상태 FamilyMember 생성
        createFamilyMember(4L, userId, FamilyMemberStatus.BANNED);
        createFamilyMember(5L, userId, FamilyMemberStatus.SUSPENDED);
        
        // 다른 사용자의 활성 상태 FamilyMember 생성
        createFamilyMember(6L, 2L, FamilyMemberStatus.ACTIVE);

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
    
    private void createFamilyMember(Long familyId, Long userId, FamilyMemberStatus status) {
        FamilyMemberJpaEntity entity = new FamilyMemberJpaEntity();
        entity.setFamilyId(familyId);
        entity.setUserId(userId);
        entity.setName("Member " + familyId);
        entity.setStatus(status);
        familyMemberJpaRepository.save(entity);
    }
}
