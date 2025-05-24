package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.helper.AdapterTestBase;
import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    @DisplayName("특정 Family의 모든 가입 신청 목록을 생성일시 내림차순으로 조회할 수 있다")
    void find_all_join_requests_by_family_id_ordered_by_created_at_desc() {
        // given
        Long familyId = 1L;
        
        // 가입 신청들을 순차적으로 저장 (시간 간격을 두어 저장)
        FamilyJoinRequest firstRequest = FamilyJoinRequest.withId(
            null, familyId, 2L, FamilyJoinRequestStatus.PENDING, 
            null, null, null, null
        );
        FamilyJoinRequestJpaEntity entity1 = FamilyJoinRequestJpaEntity.from(firstRequest);
        familyJoinRequestJpaRepository.save(entity1);
        
        try {
            Thread.sleep(10); // 시간 차이를 만들기 위해 잠시 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        FamilyJoinRequest secondRequest = FamilyJoinRequest.withId(
            null, familyId, 3L, FamilyJoinRequestStatus.PENDING, 
            null, null, null, null
        );
        FamilyJoinRequestJpaEntity entity2 = FamilyJoinRequestJpaEntity.from(secondRequest);
        familyJoinRequestJpaRepository.save(entity2);
        
        try {
            Thread.sleep(10); // 시간 차이를 만들기 위해 잠시 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        FamilyJoinRequest thirdRequest = FamilyJoinRequest.withId(
            null, familyId, 4L, FamilyJoinRequestStatus.REJECTED, 
            null, null, null, null
        );
        FamilyJoinRequestJpaEntity entity3 = FamilyJoinRequestJpaEntity.from(thirdRequest);
        familyJoinRequestJpaRepository.save(entity3);

        // when
        List<FamilyJoinRequest> result = sut.findAllByFamilyId(familyId);

        // then
        assertThat(result).hasSize(3);
        // 최신 순으로 정렬되어야 함 (가장 최근에 저장된 것부터)
        assertThat(result.get(0).getRequesterId()).isEqualTo(4L); // 마지막에 저장된 것
        assertThat(result.get(1).getRequesterId()).isEqualTo(3L); // 두 번째로 저장된 것
        assertThat(result.get(2).getRequesterId()).isEqualTo(2L); // 첫 번째로 저장된 것
    }

    @Test
    @DisplayName("다른 Family의 가입 신청은 조회되지 않는다")
    void find_all_join_requests_should_filter_by_family_id() {
        // given
        Long targetFamilyId = 1L;
        Long otherFamilyId = 2L;
        
        // 타겟 Family의 가입 신청
        FamilyJoinRequest targetRequest = FamilyJoinRequest.withId(
            null, targetFamilyId, 2L, FamilyJoinRequestStatus.PENDING, null, null, null, null
        );
        familyJoinRequestJpaRepository.save(FamilyJoinRequestJpaEntity.from(targetRequest));
        
        // 다른 Family의 가입 신청
        FamilyJoinRequest otherRequest = FamilyJoinRequest.withId(
            null, otherFamilyId, 3L, FamilyJoinRequestStatus.PENDING, null, null, null, null
        );
        familyJoinRequestJpaRepository.save(FamilyJoinRequestJpaEntity.from(otherRequest));

        // when
        List<FamilyJoinRequest> result = sut.findAllByFamilyId(targetFamilyId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFamilyId()).isEqualTo(targetFamilyId);
        assertThat(result.get(0).getRequesterId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("가입 신청이 없는 Family의 경우 빈 목록을 반환한다")
    void return_empty_list_when_no_join_requests_exist_for_family() {
        // given
        Long familyId = 99L;

        // when
        List<FamilyJoinRequest> result = sut.findAllByFamilyId(familyId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllByFamilyId에서 familyId가 null이면 예외가 발생한다")
    void throw_exception_when_family_id_is_null_in_find_all() {
        // given
        Long nullFamilyId = null;

        // when, then
        assertThatThrownBy(() -> sut.findAllByFamilyId(nullFamilyId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }
}
