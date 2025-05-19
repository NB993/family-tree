package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyJoinRequestCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] SaveFamilyJoinRequestServiceTest")
@ExtendWith(MockitoExtension.class)
class SaveFamilyJoinRequestServiceTest {

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private FindFamilyJoinRequestPort findFamilyJoinRequestPort;

    @Mock
    private SaveFamilyJoinRequestPort saveFamilyJoinRequestPort;

    @InjectMocks
    private SaveFamilyJoinRequestService sut;

    @Test
    @DisplayName("Family 가입 신청을 성공적으로 처리할 수 있다")
    void save_family_join_request_successfully() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        Long savedRequestId = 3L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        when(findFamilyPort.existsById(familyId)).thenReturn(true);
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, requesterId)).thenReturn(false);
        when(findFamilyJoinRequestPort.findLatestByFamilyIdAndRequesterId(familyId, requesterId))
            .thenReturn(Optional.empty());
        when(findFamilyMemberPort.countActiveByUserId(requesterId)).thenReturn(2);
        when(saveFamilyJoinRequestPort.save(any(FamilyJoinRequest.class))).thenReturn(savedRequestId);

        // when
        Long result = sut.save(command);

        // then
        assertThat(result).isEqualTo(savedRequestId);
        verify(saveFamilyJoinRequestPort).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("존재하지 않는 Family에 가입 신청 시 예외가 발생한다")
    void throw_exception_when_family_not_exists() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        when(findFamilyPort.existsById(familyId)).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.FAMILY_NOT_FOUND);

        verify(saveFamilyJoinRequestPort, never()).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("이미 가입된 Family에 가입 신청 시 예외가 발생한다")
    void throw_exception_when_already_joined_family() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        when(findFamilyPort.existsById(familyId)).thenReturn(true);
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, requesterId)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.ALREADY_JOINED_FAMILY);

        verify(saveFamilyJoinRequestPort, never()).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("대기 상태의 가입 신청이 있는 경우 예외가 발생한다")
    void throw_exception_when_pending_request_exists() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        FamilyJoinRequest pendingRequest = FamilyJoinRequest.withId(
            3L, familyId, requesterId, FamilyJoinRequestStatus.PENDING,
            LocalDateTime.now(), requesterId, LocalDateTime.now(), requesterId
        );

        when(findFamilyPort.existsById(familyId)).thenReturn(true);
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, requesterId)).thenReturn(false);
        when(findFamilyJoinRequestPort.findLatestByFamilyIdAndRequesterId(familyId, requesterId))
            .thenReturn(Optional.of(pendingRequest));

        // when, then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.JOIN_REQUEST_ALREADY_PENDING);

        verify(saveFamilyJoinRequestPort, never()).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("승인 상태의 가입 신청이 있는 경우 예외가 발생한다")
    void throw_exception_when_approved_request_exists() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        FamilyJoinRequest approvedRequest = FamilyJoinRequest.withId(
            3L, familyId, requesterId, FamilyJoinRequestStatus.APPROVED,
            LocalDateTime.now(), requesterId, LocalDateTime.now(), requesterId
        );

        when(findFamilyPort.existsById(familyId)).thenReturn(true);
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, requesterId)).thenReturn(false);
        when(findFamilyJoinRequestPort.findLatestByFamilyIdAndRequesterId(familyId, requesterId))
            .thenReturn(Optional.of(approvedRequest));

        // when, then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.ALREADY_JOINED_FAMILY);

        verify(saveFamilyJoinRequestPort, never()).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("거절된 가입 신청이 있는 경우 새로운 가입 신청을 할 수 있다")
    void can_request_again_when_rejected_request_exists() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        Long savedRequestId = 3L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);

        FamilyJoinRequest rejectedRequest = FamilyJoinRequest.withId(
            4L, familyId, requesterId, FamilyJoinRequestStatus.REJECTED,
            LocalDateTime.now(), requesterId, LocalDateTime.now(), requesterId
        );

        when(findFamilyPort.existsById(familyId)).thenReturn(true);
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, requesterId)).thenReturn(false);
        when(findFamilyJoinRequestPort.findLatestByFamilyIdAndRequesterId(familyId, requesterId))
            .thenReturn(Optional.of(rejectedRequest));
        when(findFamilyMemberPort.countActiveByUserId(requesterId)).thenReturn(2);
        when(saveFamilyJoinRequestPort.save(any(FamilyJoinRequest.class))).thenReturn(savedRequestId);

        // when
        Long result = sut.save(command);

        // then
        assertThat(result).isEqualTo(savedRequestId);
        verify(saveFamilyJoinRequestPort).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("최대 가입 가능 수를 초과하면 예외가 발생한다")
    void throw_exception_when_exceed_max_join_limit() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        SaveFamilyJoinRequestCommand command = new SaveFamilyJoinRequestCommand(familyId, requesterId);
        
        when(findFamilyPort.existsById(familyId)).thenReturn(true);
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, requesterId)).thenReturn(false);
        when(findFamilyJoinRequestPort.findLatestByFamilyIdAndRequesterId(familyId, requesterId))
            .thenReturn(Optional.empty());
        when(findFamilyMemberPort.countActiveByUserId(requesterId)).thenReturn(6);

        // when, then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.EXCEEDED_FAMILY_JOIN_LIMIT);

        verify(saveFamilyJoinRequestPort, never()).save(any(FamilyJoinRequest.class));
    }

    @Test
    @DisplayName("커맨드가 null이면 예외가 발생한다")
    void throw_exception_when_command_is_null() {
        // given
        SaveFamilyJoinRequestCommand nullCommand = null;

        // when, then
        assertThatThrownBy(() -> sut.save(nullCommand))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("command must not be null");

        verify(saveFamilyJoinRequestPort, never()).save(any(FamilyJoinRequest.class));
    }
}
