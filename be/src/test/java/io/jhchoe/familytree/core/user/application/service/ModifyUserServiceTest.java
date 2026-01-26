package io.jhchoe.familytree.core.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.user.application.port.in.ModifyUserCommand;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.application.port.out.ModifyUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
import io.jhchoe.familytree.test.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] ModifyUserServiceTest")
class ModifyUserServiceTest {

    @InjectMocks
    private ModifyUserService modifyUserService;

    @Mock
    private FindUserPort findUserPort;

    @Mock
    private ModifyUserPort modifyUserPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private ModifyFamilyMemberPort modifyFamilyMemberPort;

    @Nested
    @DisplayName("User 수정 테스트")
    class ModifyUserTest {

        @Test
        @DisplayName("유효한 명령으로 User 정보가 수정됩니다")
        void modify_user_successfully() {
            // given
            Long userId = 1L;
            User existingUser = UserFixture.withId(userId);
            ModifyUserCommand command = new ModifyUserCommand(
                userId,
                "새이름",
                LocalDateTime.of(1995, 5, 15, 0, 0),
                BirthdayType.LUNAR
            );

            // Mocking: 기존 User 조회
            when(findUserPort.findById(userId)).thenReturn(Optional.of(existingUser));
            // Mocking: User 수정 성공
            when(modifyUserPort.modify(any(User.class))).thenReturn(userId);
            // Mocking: FamilyMember 조회 (없음)
            when(findFamilyMemberPort.findByUserId(userId)).thenReturn(Collections.emptyList());

            // when
            User result = modifyUserService.modify(command);

            // then
            assertThat(result.getName()).isEqualTo("새이름");
            assertThat(result.getBirthday()).isEqualTo(LocalDateTime.of(1995, 5, 15, 0, 0));
            assertThat(result.getBirthdayType()).isEqualTo(BirthdayType.LUNAR);
            verify(modifyUserPort).modify(any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 User 수정 시 FTException이 발생합니다")
        void throw_when_user_not_found() {
            // given
            Long userId = 999L;
            ModifyUserCommand command = new ModifyUserCommand(userId, "새이름", null, null);

            // Mocking: User 없음
            when(findUserPort.findById(userId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> modifyUserService.modify(command))
                .isInstanceOf(FTException.class);
            verify(modifyUserPort, never()).modify(any());
        }
    }

    @Nested
    @DisplayName("FamilyMember 생일 동기화 테스트")
    class SyncFamilyMemberBirthdayTest {

        @Test
        @DisplayName("User 생일 수정 시 연관된 FamilyMember의 생일이 동기화됩니다")
        void sync_family_member_birthday_when_user_birthday_modified() {
            // given
            Long userId = 1L;
            User existingUser = UserFixture.withId(userId);
            LocalDateTime newBirthday = LocalDateTime.of(1995, 5, 15, 0, 0);
            BirthdayType newBirthdayType = BirthdayType.LUNAR;
            ModifyUserCommand command = new ModifyUserCommand(userId, existingUser.getName(), newBirthday, newBirthdayType);

            // FamilyMember 2개 (동기화 가능한 상태)
            FamilyMember member1 = FamilyMemberFixture.withIdFull(10L, 1L, userId, FamilyMemberRole.MEMBER, FamilyMemberStatus.ACTIVE);
            FamilyMember member2 = FamilyMemberFixture.withIdFull(20L, 2L, userId, FamilyMemberRole.OWNER, FamilyMemberStatus.ACTIVE);

            // Mocking
            when(findUserPort.findById(userId)).thenReturn(Optional.of(existingUser));
            when(modifyUserPort.modify(any(User.class))).thenReturn(userId);
            when(findFamilyMemberPort.findByUserId(userId)).thenReturn(List.of(member1, member2));
            when(modifyFamilyMemberPort.modify(any(FamilyMember.class))).thenReturn(10L, 20L);

            // when
            modifyUserService.modify(command);

            // then: FamilyMember 2개 모두 수정됨
            ArgumentCaptor<FamilyMember> captor = ArgumentCaptor.forClass(FamilyMember.class);
            verify(modifyFamilyMemberPort, times(2)).modify(captor.capture());

            List<FamilyMember> modifiedMembers = captor.getAllValues();
            assertThat(modifiedMembers).hasSize(2);
            assertThat(modifiedMembers).allSatisfy(member -> {
                assertThat(member.getBirthday()).isEqualTo(newBirthday);
                assertThat(member.getBirthdayType()).isEqualTo(newBirthdayType);
            });
        }

        @Test
        @DisplayName("동기화 불가능한 FamilyMember는 제외됩니다")
        void skip_not_syncable_family_members() {
            // given
            Long userId = 1L;
            User existingUser = UserFixture.withId(userId);
            LocalDateTime newBirthday = LocalDateTime.of(1995, 5, 15, 0, 0);
            ModifyUserCommand command = new ModifyUserCommand(userId, existingUser.getName(), newBirthday, BirthdayType.SOLAR);

            // 동기화 가능 1개, 불가능 1개
            FamilyMember syncableMember = FamilyMemberFixture.withIdFull(10L, 1L, userId, FamilyMemberRole.MEMBER, FamilyMemberStatus.ACTIVE);
            FamilyMember notSyncableMember = FamilyMemberFixture.withIdFull(20L, 2L, userId, FamilyMemberRole.MEMBER, FamilyMemberStatus.BANNED);

            // Mocking
            when(findUserPort.findById(userId)).thenReturn(Optional.of(existingUser));
            when(modifyUserPort.modify(any(User.class))).thenReturn(userId);
            when(findFamilyMemberPort.findByUserId(userId)).thenReturn(List.of(syncableMember, notSyncableMember));
            when(modifyFamilyMemberPort.modify(any(FamilyMember.class))).thenReturn(10L);

            // when
            modifyUserService.modify(command);

            // then: 동기화 가능한 멤버만 수정됨
            verify(modifyFamilyMemberPort, times(1)).modify(any(FamilyMember.class));
        }

        @Test
        @DisplayName("수동 등록 FamilyMember(userId=null)는 조회되지 않아 동기화에서 제외됩니다")
        void not_sync_manual_family_members() {
            // given
            Long userId = 1L;
            User existingUser = UserFixture.withId(userId);
            LocalDateTime newBirthday = LocalDateTime.of(1995, 5, 15, 0, 0);
            ModifyUserCommand command = new ModifyUserCommand(userId, existingUser.getName(), newBirthday, BirthdayType.SOLAR);

            // findByUserId는 userId가 있는 멤버만 반환하므로, 수동 등록 멤버는 포함되지 않음
            FamilyMember member = FamilyMemberFixture.withIdFull(10L, 1L, userId, FamilyMemberRole.MEMBER, FamilyMemberStatus.ACTIVE);

            // Mocking
            when(findUserPort.findById(userId)).thenReturn(Optional.of(existingUser));
            when(modifyUserPort.modify(any(User.class))).thenReturn(userId);
            when(findFamilyMemberPort.findByUserId(userId)).thenReturn(List.of(member));
            when(modifyFamilyMemberPort.modify(any(FamilyMember.class))).thenReturn(10L);

            // when
            modifyUserService.modify(command);

            // then: userId가 있는 멤버만 조회되어 수정됨
            verify(modifyFamilyMemberPort, times(1)).modify(any(FamilyMember.class));
        }

        @Test
        @DisplayName("User 이름 수정 시 FamilyMember 이름은 동기화되지 않습니다")
        void not_sync_family_member_name_when_user_name_modified() {
            // given
            Long userId = 1L;
            User existingUser = UserFixture.withId(userId);
            String newName = "완전히새로운이름";
            ModifyUserCommand command = new ModifyUserCommand(userId, newName, existingUser.getBirthday(), existingUser.getBirthdayType());

            FamilyMember member = FamilyMemberFixture.withIdFull(10L, 1L, userId, FamilyMemberRole.MEMBER, FamilyMemberStatus.ACTIVE);
            String originalMemberName = member.getName();

            // Mocking
            when(findUserPort.findById(userId)).thenReturn(Optional.of(existingUser));
            when(modifyUserPort.modify(any(User.class))).thenReturn(userId);
            when(findFamilyMemberPort.findByUserId(userId)).thenReturn(List.of(member));
            when(modifyFamilyMemberPort.modify(any(FamilyMember.class))).thenReturn(10L);

            // when
            User result = modifyUserService.modify(command);

            // then: User 이름은 변경됨
            assertThat(result.getName()).isEqualTo(newName);

            // then: FamilyMember 이름은 변경되지 않음 (생일만 동기화)
            ArgumentCaptor<FamilyMember> captor = ArgumentCaptor.forClass(FamilyMember.class);
            verify(modifyFamilyMemberPort).modify(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo(originalMemberName);
        }
    }
}