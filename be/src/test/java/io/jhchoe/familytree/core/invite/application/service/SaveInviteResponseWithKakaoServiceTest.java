package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.application.port.out.ModifyFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.test.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
            10L, // familyId
            100L, // requesterId
            "invite-code-123",
            LocalDateTime.now().plusDays(1),
            10, // maxUses
            3,  // usedCount
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        requesterMember = FamilyMemberFixture.withIdAndRole(1L, 10L, 100L, FamilyMemberRole.MEMBER);
    }

    @Test
    @DisplayName("카카오 OAuth를 통한 초대 수락에 성공한다")
    void save_success_when_valid_invite_and_kakao_auth() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // kakaoId로 User 조회 모킹
        User user = UserFixture.withIdAndKakaoId(200L, "kakao_12345");
        when(findUserPort.findByKakaoId("kakao_12345"))
            .thenReturn(Optional.of(user));

        // 초대 생성자의 해당 Family 멤버십 조회 모킹
        when(findFamilyMemberPort.findByFamilyIdAndUserId(10L, 100L))
            .thenReturn(Optional.of(requesterMember));

        // 중복 가입 확인 모킹 - 기존 구성원 없음
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(10L, 200L))
            .thenReturn(false);

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
            10L, // familyId
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
            10L, // familyId
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
    @DisplayName("초대 생성자가 해당 가족에 속해있지 않으면 예외가 발생한다")
    void throw_exception_when_requester_not_family_member() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // kakaoId로 User 조회 모킹
        User user = UserFixture.withIdAndKakaoId(200L, "kakao_12345");
        when(findUserPort.findByKakaoId("kakao_12345"))
            .thenReturn(Optional.of(user));

        // 초대 생성자의 해당 Family 멤버십 조회 모킹 - 해당 가족의 멤버가 아님
        when(findFamilyMemberPort.findByFamilyIdAndUserId(10L, 100L))
            .thenReturn(Optional.empty()); // 해당 가족의 멤버가 아님

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.REQUESTER_NOT_FAMILY_MEMBER.getCode());
            });
    }

    @Test
    @DisplayName("자기가 보낸 초대를 자기가 수락하면 예외가 발생한다")
    void throw_exception_when_accept_own_invite() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // kakaoId로 User 조회 모킹 - 초대 생성자(userId=100)와 동일한 userId를 가진 User 반환
        User user = UserFixture.withIdAndKakaoId(100L, "kakao_12345");
        when(findUserPort.findByKakaoId("kakao_12345"))
            .thenReturn(Optional.of(user));

        // 초대 생성자의 FamilyMember 조회 모킹 - 동일한 userId(100)
        FamilyMember requesterMemberWithSameUserId = FamilyMemberFixture.withIdAndRole(
            1L, 10L, 100L, FamilyMemberRole.OWNER
        );
        when(findFamilyMemberPort.findByFamilyIdAndUserId(10L, 100L))
            .thenReturn(Optional.of(requesterMemberWithSameUserId));

        // when & then
        assertThatThrownBy(() -> saveInviteResponseWithKakaoService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.CANNOT_ACCEPT_OWN_INVITE.getCode());
            });
    }

    @Test
    @DisplayName("이미 같은 userId로 가입한 멤버가 있으면 예외가 발생한다")
    void throw_exception_when_already_family_member() {
        // given
        // 초대 링크 조회 모킹
        when(findFamilyInvitePort.findByCode("invite-code-123"))
            .thenReturn(Optional.of(activeInvite));

        // kakaoId로 User 조회 모킹
        User user = UserFixture.withIdAndKakaoId(200L, "kakao_12345");
        when(findUserPort.findByKakaoId("kakao_12345"))
            .thenReturn(Optional.of(user));

        // 초대 생성자의 해당 Family 멤버십 조회 모킹
        when(findFamilyMemberPort.findByFamilyIdAndUserId(10L, 100L))
            .thenReturn(Optional.of(requesterMember));

        // 이미 해당 userId로 가입한 멤버가 존재
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(10L, 200L))
            .thenReturn(true);

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
