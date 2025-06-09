package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] SaveFamilyService")
@ExtendWith(MockitoExtension.class)
class SaveFamilyServiceTest {

    @Mock
    private SaveFamilyPort saveFamilyPort;

    @Mock
    private SaveFamilyMemberPort saveFamilyMemberPort;

    @Mock
    private FindUserPort findUserPort;

    @InjectMocks
    private SaveFamilyService sut;

    @Test
    @DisplayName("Family 생성 시 정상적으로 저장되고 생성자에게 OWNER 권한이 부여됩니다")
    void save_success_and_create_owner_when_valid_command() {
        // given
        Long userId = 1L;
        Long expectedFamilyId = 10L;
        Long expectedMemberId = 20L;
        
        SaveFamilyCommand command = new SaveFamilyCommand(
            userId, "테스트 가족", "http://example.com/profile", "가족 설명", true
        );
        
        User user = User.withId(
            userId, "test@example.com", "홍길동", "http://example.com/user-profile",
            AuthenticationType.OAUTH2, OAuth2Provider.GOOGLE, UserRole.USER, false,
            userId, LocalDateTime.now(), userId, LocalDateTime.now()
        );

        // Mocking: 사용자 조회 성공
        when(findUserPort.findById(userId)).thenReturn(Optional.of(user));
        // Mocking: Family 저장 성공
        when(saveFamilyPort.save(any(Family.class))).thenReturn(expectedFamilyId);
        // Mocking: FamilyMember 저장 성공
        when(saveFamilyMemberPort.save(any(FamilyMember.class))).thenReturn(expectedMemberId);

        // when
        Long result = sut.save(command);

        // then
        assertThat(result).isEqualTo(expectedFamilyId);
        verify(findUserPort).findById(userId);
        verify(saveFamilyPort).save(any(Family.class));
        verify(saveFamilyMemberPort).save(any(FamilyMember.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 Family 생성 시 예외가 발생합니다")
    void throw_exception_when_user_not_found() {
        // given
        Long nonExistentUserId = 999L;
        SaveFamilyCommand command = new SaveFamilyCommand(
            nonExistentUserId, "테스트 가족", null, null, false
        );

        // Mocking: 사용자 조회 실패
        when(findUserPort.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(e -> {
                FTException ftException = (FTException) e;
                assertThat(ftException.getCode()).isEqualTo(AuthExceptionCode.USER_NOT_FOUND.getCode());
            });
    }

    @Test
    @DisplayName("Command가 null일 때 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        // given
        SaveFamilyCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 사용자 ID가 null이면 IllegalArgumentException이 발생합니다")
    void throw_exception_when_user_id_is_null() {
        // given & when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(null, "Family Name", null, null, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 사용자 ID가 필요합니다.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 사용자 ID가 0 이하이면 IllegalArgumentException이 발생합니다")
    void throw_exception_when_user_id_is_zero_or_negative() {
        // given & when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(0L, "Family Name", null, null, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효한 사용자 ID가 필요합니다.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 이름이 없는 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_null_name_when_create_command_then_throw_exception() {
        // given & when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(1L, null, "http://example.com", "Description", true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명을 입력해주세요.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 이름이 빈 문자열일 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_blank_name_when_create_command_then_throw_exception() {
        // given & when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(1L, "   ", "http://example.com", "Description", true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명을 입력해주세요.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 이름이 20자를 초과하는 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_name_over_20_chars_when_create_command_then_throw_exception() {
        // given
        String longName = "a".repeat(21);

        // when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(1L, longName, null, "Description", true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명은 20자 이하로 입력해주세요.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 유효하지 않은 profileUrl이 주어진 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_invalid_profile_url_when_create_command_then_throw_exception() {
        // given & when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(1L, "Family Name", "invalid-url", "Description", true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("프로필 URL 형식이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 설명이 200자를 초과할 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_description_over_200_chars_when_create_command_then_throw_exception() {
        // given
        String longDescription = "a".repeat(201);

        // when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(1L, "Family Name", "http://example.com", longDescription, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 설명은 200자 이내로 작성해주세요.");
    }

    @Test
    @DisplayName("SaveFamilyCommand 생성 시 isPublic이 null인 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_null_is_public_when_create_command_then_throw_exception() {
        // given & when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(1L, "Family Name", "http://example.com", "Description", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("공개 여부를 선택해주세요.");
    }
}
