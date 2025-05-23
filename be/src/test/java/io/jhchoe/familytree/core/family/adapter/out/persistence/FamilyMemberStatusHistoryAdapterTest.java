package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import io.jhchoe.familytree.helper.AdapterTestBase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Unit Test] FamilyMemberStatusHistoryAdapterTest")
class FamilyMemberStatusHistoryAdapterTest extends AdapterTestBase {

    @Autowired
    private FamilyMemberStatusHistoryJpaRepository familyMemberStatusHistoryJpaRepository;

    private FamilyMemberStatusHistoryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyMemberStatusHistoryAdapter(familyMemberStatusHistoryJpaRepository);
    }

    @Test
    @DisplayName("save 메서드는 FamilyMemberStatusHistory를 성공적으로 저장한다")
    void save_family_member_status_history_successfully() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        
        FamilyMemberStatusHistory history = FamilyMemberStatusHistory.create(
            familyId, memberId, status, reason
        );

        // when
        Long savedId = sut.save(history);

        // then
        assertThat(savedId).isNotNull();
        assertThat(savedId).isPositive();
        
        // 저장 확인
        FamilyMemberStatusHistoryJpaEntity savedEntity = familyMemberStatusHistoryJpaRepository.findById(savedId).orElseThrow();
        assertThat(savedEntity.getFamilyId()).isEqualTo(familyId);
        assertThat(savedEntity.getMemberId()).isEqualTo(memberId);
        assertThat(savedEntity.getStatus()).isEqualTo(status);
        assertThat(savedEntity.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("save 메서드는 null history로 호출 시 예외를 발생시킨다")
    void throw_exception_when_save_with_null_history() {
        // given
        FamilyMemberStatusHistory nullHistory = null;

        // when & then
        assertThatThrownBy(() -> sut.save(nullHistory))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("history must not be null");
    }

    @Test
    @DisplayName("findAllByMemberId 메서드는 특정 구성원의 모든 상태 변경 이력을 조회한다")
    void return_all_status_history_by_member_id() {
        // given
        Long familyId = 1L;
        Long memberId = 2L;
        
        // 여러 상태 변경 이력 생성
        createStatusHistory(familyId, memberId, FamilyMemberStatus.ACTIVE, "활성화");
        createStatusHistory(familyId, memberId, FamilyMemberStatus.SUSPENDED, "규칙 위반");
        createStatusHistory(familyId, memberId, FamilyMemberStatus.ACTIVE, "상태 복구");
        
        // 다른 구성원의 이력
        createStatusHistory(familyId, 3L, FamilyMemberStatus.BANNED, "스팸 행위");

        // when
        List<FamilyMemberStatusHistory> result = sut.findAllByMemberId(memberId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(FamilyMemberStatusHistory::getMemberId)
            .containsOnly(memberId);
        assertThat(result)
            .extracting(FamilyMemberStatusHistory::getReason)
            .containsExactlyInAnyOrder("활성화", "규칙 위반", "상태 복구");
    }

    @Test
    @DisplayName("findAllByFamilyId 메서드는 특정 Family의 모든 상태 변경 이력을 조회한다")
    void return_all_status_history_by_family_id() {
        // given
        Long familyId = 1L;
        
        // Family 1의 여러 구성원 상태 변경 이력
        createStatusHistory(familyId, 1L, FamilyMemberStatus.ACTIVE, "Owner 활성화");
        createStatusHistory(familyId, 2L, FamilyMemberStatus.SUSPENDED, "Member 정지");
        createStatusHistory(familyId, 3L, FamilyMemberStatus.BANNED, "Member 차단");
        
        // 다른 Family의 이력
        createStatusHistory(2L, 4L, FamilyMemberStatus.ACTIVE, "다른 Family");

        // when
        List<FamilyMemberStatusHistory> result = sut.findAllByFamilyId(familyId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(FamilyMemberStatusHistory::getFamilyId)
            .containsOnly(familyId);
        assertThat(result)
            .extracting(FamilyMemberStatusHistory::getReason)
            .containsExactlyInAnyOrder("Owner 활성화", "Member 정지", "Member 차단");
    }

    @Test
    @DisplayName("findAllByMemberId 메서드는 null memberId로 호출 시 예외를 발생시킨다")
    void throw_exception_when_find_by_member_id_with_null() {
        // given
        Long nullMemberId = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllByMemberId(nullMemberId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("memberId must not be null");
    }

    @Test
    @DisplayName("findAllByFamilyId 메서드는 null familyId로 호출 시 예외를 발생시킨다")
    void throw_exception_when_find_by_family_id_with_null() {
        // given
        Long nullFamilyId = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllByFamilyId(nullFamilyId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }

    /**
     * 테스트용 헬퍼 메서드: 상태 변경 이력을 생성합니다.
     */
    private void createStatusHistory(Long familyId, Long memberId, FamilyMemberStatus status, String reason) {
        FamilyMemberStatusHistory history = FamilyMemberStatusHistory.create(familyId, memberId, status, reason);
        FamilyMemberStatusHistoryJpaEntity entity = FamilyMemberStatusHistoryJpaEntity.from(history);
        familyMemberStatusHistoryJpaRepository.save(entity);
    }
}
