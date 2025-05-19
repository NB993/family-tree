package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.helper.AdapterTestBase;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[Unit Test] FamilyJoinRequestAdapterTest")
class FamilyJoinRequestAdapterTest extends AdapterTestBase {

    @Autowired
    private FamilyJoinRequestJpaRepository familyJoinRequestJpaRepository;

    private FamilyJoinRequestAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new FamilyJoinRequestAdapter(familyJoinRequestJpaRepository);
    }

    @Test
    @DisplayName("Family 가입 신청을 저장할 수 있다")
    void save_family_join_request() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        FamilyJoinRequest request = FamilyJoinRequest.newRequest(familyId, requesterId);

        // when
        Long savedId = sut.save(request);

        // then
        assertThat(savedId).isNotNull();
        Optional<FamilyJoinRequestJpaEntity> savedEntity = familyJoinRequestJpaRepository.findById(savedId);
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getFamilyId()).isEqualTo(familyId);
        assertThat(savedEntity.get().getRequesterId()).isEqualTo(requesterId);
        assertThat(savedEntity.get().getStatus()).isEqualTo(FamilyJoinRequestStatus.PENDING);
    }

    @Test
    @DisplayName("familyJoinRequest가 null이면 예외가 발생한다")
    void throw_exception_when_family_join_request_is_null() {
        // given
        FamilyJoinRequest nullRequest = null;

        // when, then
        assertThatThrownBy(() -> sut.save(nullRequest))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyJoinRequest must not be null");
    }

    @Test
    @DisplayName("특정 Family와 신청자에 대한 가장 최근의 가입 신청을 조회할 수 있다")
    void find_latest_request_by_family_id_and_requester_id() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        
        // 첫 번째 가입 신청(거절됨)
        FamilyJoinRequest rejectedRequest = FamilyJoinRequest.withId(
            null, familyId, requesterId, FamilyJoinRequestStatus.REJECTED, null, null, null, null
        );
        FamilyJoinRequestJpaEntity entity1 = FamilyJoinRequestJpaEntity.from(rejectedRequest);
        familyJoinRequestJpaRepository.save(entity1);
        
        // 두 번째 가입 신청(대기 중)
        FamilyJoinRequest pendingRequest = FamilyJoinRequest.withId(
            null, familyId, requesterId, FamilyJoinRequestStatus.PENDING, null, null, null, null
        );
        FamilyJoinRequestJpaEntity entity2 = FamilyJoinRequestJpaEntity.from(pendingRequest);
        familyJoinRequestJpaRepository.save(entity2);

        // when
        Optional<FamilyJoinRequest> result = sut.findLatestByFamilyIdAndRequesterId(familyId, requesterId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(FamilyJoinRequestStatus.PENDING);
    }

    @Test
    @DisplayName("가입 신청이 없으면 빈 Optional을 반환한다")
    void return_empty_optional_when_no_request_exists() {
        // given
        Long familyId = 99L;
        Long requesterId = 99L;

        // when
        Optional<FamilyJoinRequest> result = sut.findLatestByFamilyIdAndRequesterId(familyId, requesterId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("familyId가 null이면 예외가 발생한다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long nullFamilyId = null;
        Long requesterId = 2L;

        // when, then
        assertThatThrownBy(() -> sut.findLatestByFamilyIdAndRequesterId(nullFamilyId, requesterId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }

    @Test
    @DisplayName("requesterId가 null이면 예외가 발생한다")
    void throw_exception_when_requester_id_is_null() {
        // given
        Long familyId = 1L;
        Long nullRequesterId = null;

        // when, then
        assertThatThrownBy(() -> sut.findLatestByFamilyIdAndRequesterId(familyId, nullRequesterId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("requesterId must not be null");
    }
}
