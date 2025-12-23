package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.application.port.out.ModifyFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] SaveInviteResponseWithKakaoServiceTest")
class SaveInviteResponseWithKakaoServiceTest {

    @InjectMocks
    private SaveInviteResponseWithKakaoService saveInviteResponseWithKakaoService;

    @Mock
    private FindFamilyInvitePort findFamilyInvitePort;

    @Mock
    private ModifyFamilyInvitePort modifyFamilyInvitePort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private SaveFamilyMemberPort saveFamilyMemberPort;

    @Mock
    private FindUserPort findUserPort;

    private SaveInviteResponseWithKakaoCommand command;
    private FamilyInvite activeInvite;
    private FamilyMember requesterMember;

    @BeforeEach
    void setUp() {
        command = new SaveInviteResponseWithKakaoCommand(
            "invite-code-123",
            "kakao_12345",              // kakaoId
            "kakao@example.com",        // email
            "카카오유저",                 // name
            "http://kakao.com/profile.jpg" // profileUrl
        );

        activeInvite = FamilyInvite.withId(
            1L,
            100L, // requesterId
            "invite-code-123",
            LocalDateTime.now().plusDays(1),
            10, // maxUses
            3,  // usedCount
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        requesterMember = FamilyMember.withId(
            1L,
            10L, // familyId
            100L, // userId (requesterId와 동일)
            null, // kakaoId
            "요청자",
            null,
            null, // relationship
            null,
            null,
            FamilyMemberStatus.ACTIVE,
            FamilyMemberRole.MEMBER,
            null,
            null,
            null,
            null
        );
    }

    @Test
    @DisplayName("카카오 OAuth를 통한 초대 수락에 성공한다")
    void save_success_when_valid_invite_and_kakao_auth() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // User 조회 모킹 - User 없음 (비회원)
        when(findUserPort.findByEmail("kakao@example.com"))
            .thenReturn(Optional.empty());

        // 초대 생성자의 FamilyMember 조회 모킹
        when(findFamilyMemberPort.findByUserId(100L))
            .thenReturn(List.of(requesterMember));

        // 가족 구성원 조회 모킹 - 기존 구성원 없음
        when(findFamilyMemberPort.findByFamilyId(10L))
            .thenReturn(Collections.emptyList());

        // FamilyMember 저장 모킹
        when(saveFamilyMemberPort.save(any(FamilyMember.class)))
            .thenReturn(2L); // 저장된 멤버의 ID 반환

        // FamilyInvite 수정 모킹
        when(modifyFamilyInvitePort.modify(any(FamilyInvite.class)))
            .thenReturn(activeInvite);

        // when
        Long result = saveInviteResponseWithKakaoService.save(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(2L);

        verify(saveFamilyMemberPort).save(any(FamilyMember.class));
        verify(modifyFamilyInvitePort).modify(any(FamilyInvite.class));
    }

    @Test
    @DisplayName("초대 코드가 존재하지 않으면 예외가 발생한다")
    void throw_exception_when_invite_not_found() {
        // given
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.INVITE_NOT_FOUND.getCode());
            });
    }

    @Test
    @DisplayName("초대가 만료되었으면 예외가 발생한다")
    void throw_exception_when_invite_expired() {
        // given
        FamilyInvite expiredInvite = FamilyInvite.withId(
            1L,
            100L,
            "invite-code-123",
            LocalDateTime.now().minusDays(1), // 만료됨
            10,
            3,
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(2)
        );

        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(expiredInvite));

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.INVITE_EXPIRED.getCode());
            });
    }

    @Test
    @DisplayName("초대 사용 횟수를 초과하면 예외가 발생한다")
    void throw_exception_when_max_uses_exceeded() {
        // given
        FamilyInvite fullInvite = FamilyInvite.withId(
            1L,
            100L,
            "invite-code-123",
            LocalDateTime.now().plusDays(1),
            5,  // maxUses
            5,  // usedCount (이미 최대치에 도달)
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(fullInvite));

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.INVITE_MAX_USES_EXCEEDED.getCode());
            });
    }

    @Test
    @DisplayName("초대 생성자가 가족에 속해있지 않으면 예외가 발생한다")
    void throw_exception_when_requester_has_no_family() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // User 조회 모킹 - User 없음 (비회원)
        when(findUserPort.findByEmail("kakao@example.com"))
            .thenReturn(Optional.empty());

        // 초대 생성자의 FamilyMember 조회 모킹 - 가족 없음
        when(findFamilyMemberPort.findByUserId(100L))
            .thenReturn(Collections.emptyList()); // 가족 없음

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.REQUESTER_HAS_NO_FAMILY.getCode());
            });
    }

    @Test
    @DisplayName("자기가 보낸 초대를 자기가 수락하면 예외가 발생한다")
    void throw_exception_when_accept_own_invite() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // User 조회 모킹 - User 없음 (비회원)
        when(findUserPort.findByEmail("kakao@example.com"))
            .thenReturn(Optional.empty());

        // 초대 생성자의 FamilyMember 조회 모킹 - 동일한 kakaoId
        FamilyMember requesterMember = FamilyMember.withId(
            1L,
            10L, // familyId
            100L, // userId (requesterId와 동일)
            "kakao_12345", // kakaoId - command의 kakaoId와 동일
            "소유자",
            null,
            null, // relationship
            null,
            null,
            FamilyMemberStatus.ACTIVE,
            FamilyMemberRole.OWNER,
            null,
            null,
            null,
            null
        );
        when(findFamilyMemberPort.findByUserId(100L))
            .thenReturn(List.of(requesterMember));

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.CANNOT_ACCEPT_OWN_INVITE.getCode());
            });
    }

    @Test
    @DisplayName("이미 같은 카카오 ID로 가입한 멤버가 있으면 예외가 발생한다")
    void throw_exception_when_already_family_member() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // User 조회 모킹 - User 없음 (비회원)
        when(findUserPort.findByEmail("kakao@example.com"))
            .thenReturn(Optional.empty());

        // 초대 생성자의 FamilyMember 조회 모킹
        when(findFamilyMemberPort.findByUserId(100L))
            .thenReturn(List.of(requesterMember));

        // 이미 같은 kakaoId를 가진 멤버가 존재
        FamilyMember existingKakaoMember = FamilyMember.withIdKakao(
            2L,
            10L,
            null,
            "kakao_12345",
            "기존유저",
            null, // relationship
            null,
            null,
            null,
            FamilyMemberStatus.ACTIVE,
            FamilyMemberRole.MEMBER,
            null,
            null,
            null,
            null
        );

        // 가족 구성원 조회 모킹 - 기존 구성원에 같은 kakaoId 존재
        when(findFamilyMemberPort.findByFamilyId(10L))
            .thenReturn(List.of(existingKakaoMember));

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.ALREADY_FAMILY_MEMBER.getCode());
            });
    }

    @Test
    @DisplayName("command가 null이면 예외가 발생한다")
    void throw_exception_when_command_is_null() {
        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
