package io.jhchoe.familytree.core.family.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyJoinRequestTest")
class FamilyJoinRequestTest {

    @Test
    @DisplayName("newRequest 메서드로 새로운 가입 신청을 생성할 수 있다")
    void create_new_request() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;

        // when
        FamilyJoinRequest request = FamilyJoinRequest.newRequest(familyId, requesterId);

        // then
        assertThat(request.getId()).isNull();
        assertThat(request.getFamilyId()).isEqualTo(familyId);
        assertThat(request.getRequesterId()).isEqualTo(requesterId);
        assertThat(request.getStatus()).isEqualTo(FamilyJoinRequestStatus.PENDING);
        assertThat(request.isPending()).isTrue();
    }

    @Test
    @DisplayName("withId 메서드로 기존 가입 신청 정보로 객체를 생성할 수 있다")
    void create_request_with_id() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long requesterId = 3L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.APPROVED;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        Long createdBy = 3L;
        LocalDateTime modifiedAt = LocalDateTime.of(2023, 1, 2, 0, 0);
        Long modifiedBy = 3L;

        // when
        FamilyJoinRequest request = FamilyJoinRequest.withId(
            id, familyId, requesterId, status, createdAt, createdBy, modifiedAt, modifiedBy
        );

        // then
        assertThat(request.getId()).isEqualTo(id);
        assertThat(request.getFamilyId()).isEqualTo(familyId);
        assertThat(request.getRequesterId()).isEqualTo(requesterId);
        assertThat(request.getStatus()).isEqualTo(status);
        assertThat(request.getCreatedAt()).isEqualTo(createdAt);
        assertThat(request.getCreatedBy()).isEqualTo(createdBy);
        assertThat(request.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(request.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(request.isApproved()).isTrue();
    }

    @Test
    @DisplayName("familyId가 null이면 예외가 발생한다")
    void throw_exception_when_family_id_is_null() {
        // given
        Long familyId = null;
        Long requesterId = 1L;

        // when, then
        assertThatThrownBy(() -> FamilyJoinRequest.newRequest(familyId, requesterId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("familyId must not be null");
    }

    @Test
    @DisplayName("requesterId가 null이면 예외가 발생한다")
    void throw_exception_when_requester_id_is_null() {
        // given
        Long familyId = 1L;
        Long requesterId = null;

        // when, then
        assertThatThrownBy(() -> FamilyJoinRequest.newRequest(familyId, requesterId))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("requesterId must not be null");
    }

    @Test
    @DisplayName("withId 메서드에서 id가 null이면 예외가 발생한다")
    void throw_exception_when_id_is_null_in_withId() {
        // given
        Long id = null;
        Long familyId = 1L;
        Long requesterId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.PENDING;

        // when & then
        assertThatThrownBy(() -> FamilyJoinRequest.withId(
            id, familyId, requesterId, status, null, null, null, null
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("withId 메서드에서 familyId가 null이면 예외가 발생한다")
    void throw_exception_when_family_id_is_null_in_withId() {
        // given
        Long id = 1L;
        Long familyId = null;
        Long requesterId = 2L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.PENDING;

        // when & then
        assertThatThrownBy(() -> FamilyJoinRequest.withId(
            id, familyId, requesterId, status, null, null, null, null
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("withId 메서드에서 requesterId가 null이면 예외가 발생한다")
    void throw_exception_when_requester_id_is_null_in_withId() {
        // given
        Long id = 1L;
        Long familyId = 1L;
        Long requesterId = null;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.PENDING;

        // when & then
        assertThatThrownBy(() -> FamilyJoinRequest.withId(
            id, familyId, requesterId, status, null, null, null, null
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("requesterId must not be null");
    }

    @Test
    @DisplayName("withId 메서드에서 status가 null이면 예외가 발생한다")
    void throw_exception_when_status_is_null_in_withId() {
        // given
        Long id = 1L;
        Long familyId = 1L;
        Long requesterId = 2L;
        FamilyJoinRequestStatus status = null;

        // when & then
        assertThatThrownBy(() -> FamilyJoinRequest.withId(
            id, familyId, requesterId, status, null, null, null, null
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("status must not be null");
    }

    @Test
    @DisplayName("approve 메서드로 가입 신청을 승인 상태로 변경할 수 있다")
    void approve_request() {
        // given
        FamilyJoinRequest request = FamilyJoinRequest.newRequest(1L, 2L);

        // when
        FamilyJoinRequest approvedRequest = request.approve();

        // then
        assertThat(approvedRequest.getStatus()).isEqualTo(FamilyJoinRequestStatus.APPROVED);
        assertThat(approvedRequest.isApproved()).isTrue();
        // modifiedAt과 modifiedBy는 JPA Audit을 통해 자동 설정되므로 null로 전달
        assertThat(approvedRequest.getModifiedBy()).isNull();
        assertThat(approvedRequest.getModifiedAt()).isNull();
        // 원본 객체는 변경되지 않음 (불변성)
        assertThat(request.getStatus()).isEqualTo(FamilyJoinRequestStatus.PENDING);
    }

    @Test
    @DisplayName("reject 메서드로 가입 신청을 거절 상태로 변경할 수 있다")
    void reject_request() {
        // given
        FamilyJoinRequest request = FamilyJoinRequest.newRequest(1L, 2L);

        // when
        FamilyJoinRequest rejectedRequest = request.reject();

        // then
        assertThat(rejectedRequest.getStatus()).isEqualTo(FamilyJoinRequestStatus.REJECTED);
        assertThat(rejectedRequest.isRejected()).isTrue();
        // modifiedAt과 modifiedBy는 JPA Audit을 통해 자동 설정되므로 null로 전달
        assertThat(rejectedRequest.getModifiedBy()).isNull();
        assertThat(rejectedRequest.getModifiedAt()).isNull();
        // 원본 객체는 변경되지 않음 (불변성)
        assertThat(request.getStatus()).isEqualTo(FamilyJoinRequestStatus.PENDING);
    }

    @Test
    @DisplayName("canBeProcessed 메서드가 올바르게 동작한다")
    void can_be_processed_works_correctly() {
        // given
        FamilyJoinRequest pendingRequest = FamilyJoinRequest.withId(
            1L, 1L, 2L, FamilyJoinRequestStatus.PENDING, null, null, null, null
        );
        FamilyJoinRequest approvedRequest = FamilyJoinRequest.withId(
            2L, 1L, 2L, FamilyJoinRequestStatus.APPROVED, null, null, null, null
        );
        FamilyJoinRequest rejectedRequest = FamilyJoinRequest.withId(
            3L, 1L, 2L, FamilyJoinRequestStatus.REJECTED, null, null, null, null
        );

        // then
        assertThat(pendingRequest.canBeProcessed()).isTrue();
        assertThat(approvedRequest.canBeProcessed()).isFalse();
        assertThat(rejectedRequest.canBeProcessed()).isFalse();
    }

    @Test
    @DisplayName("상태 확인 메서드가 올바르게 동작한다")
    void status_check_methods_work_correctly() {
        // given
        FamilyJoinRequest pendingRequest = FamilyJoinRequest.withId(
            1L, 1L, 2L, FamilyJoinRequestStatus.PENDING, null, null, null, null
        );
        FamilyJoinRequest approvedRequest = FamilyJoinRequest.withId(
            2L, 1L, 2L, FamilyJoinRequestStatus.APPROVED, null, null, null, null
        );
        FamilyJoinRequest rejectedRequest = FamilyJoinRequest.withId(
            3L, 1L, 2L, FamilyJoinRequestStatus.REJECTED, null, null, null, null
        );

        // then
        assertThat(pendingRequest.isPending()).isTrue();
        assertThat(pendingRequest.isApproved()).isFalse();
        assertThat(pendingRequest.isRejected()).isFalse();

        assertThat(approvedRequest.isPending()).isFalse();
        assertThat(approvedRequest.isApproved()).isTrue();
        assertThat(approvedRequest.isRejected()).isFalse();

        assertThat(rejectedRequest.isPending()).isFalse();
        assertThat(rejectedRequest.isApproved()).isFalse();
        assertThat(rejectedRequest.isRejected()).isTrue();
    }
}
