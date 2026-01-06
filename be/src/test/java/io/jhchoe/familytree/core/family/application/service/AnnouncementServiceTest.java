package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.*;
import io.jhchoe.familytree.core.family.application.port.out.FindAnnouncementPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveAnnouncementPort;
import io.jhchoe.familytree.core.family.domain.Announcement;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] AnnouncementService")
class AnnouncementServiceTest {

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private SaveAnnouncementPort saveAnnouncementPort;

    @Mock
    private FindAnnouncementPort findAnnouncementPort;

    @InjectMocks
    private AnnouncementService sut;

    @Test
    @DisplayName("OWNER가 공지사항을 저장할 수 있다")
    void save_announcement_by_owner_should_succeed() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        String title = "중요 공지사항";
        String content = "공지사항 내용입니다.";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "소유자", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(saveAnnouncementPort.save(any(Announcement.class)))
            .thenReturn(1L);

        // when
        SaveAnnouncementCommand command = new SaveAnnouncementCommand(
            familyId, currentUserId, title, content
        );
        Long savedId = sut.save(command);

        // then
        assertThat(savedId).isEqualTo(1L);
        verify(saveAnnouncementPort, times(1)).save(any(Announcement.class));
    }

    @Test
    @DisplayName("ADMIN이 공지사항을 저장할 수 있다")
    void save_announcement_by_admin_should_succeed() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        String title = "중요 공지사항";
        String content = "공지사항 내용입니다.";

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "관리자", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(saveAnnouncementPort.save(any(Announcement.class)))
            .thenReturn(1L);

        // when
        SaveAnnouncementCommand command = new SaveAnnouncementCommand(
            familyId, currentUserId, title, content
        );
        Long savedId = sut.save(command);

        // then
        assertThat(savedId).isEqualTo(1L);
        verify(saveAnnouncementPort, times(1)).save(any(Announcement.class));
    }

    @Test
    @DisplayName("일반 구성원은 공지사항을 저장할 수 없다")
    void save_announcement_by_member_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        String title = "중요 공지사항";
        String content = "공지사항 내용입니다.";

        // 일반 구성원 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "일반 구성원", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // when & then
        SaveAnnouncementCommand command = new SaveAnnouncementCommand(
            familyId, currentUserId, title, content
        );
        
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_AUTHORIZED);
        
        verify(saveAnnouncementPort, never()).save(any(Announcement.class));
    }

    @Test
    @DisplayName("모든 구성원이 공지사항 목록을 조회할 수 있다")
    void find_all_announcements_should_succeed() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        int page = 0;
        int size = 10;

        // 일반 구성원
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "일반 구성원", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // 공지사항 목록
        List<Announcement> announcements = Arrays.asList(
            Announcement.withId(
                1L, familyId, "공지1", "내용1", 3L, LocalDateTime.now(), null, null
            ),
            Announcement.withId(
                2L, familyId, "공지2", "내용2", 3L, LocalDateTime.now(), null, null
            )
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findAnnouncementPort.findAllByFamilyId(familyId, page, size))
            .thenReturn(announcements);

        // when
        FindAnnouncementQuery query = new FindAnnouncementQuery(familyId, currentUserId, page, size);
        List<Announcement> result = sut.findAll(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("공지1");
        assertThat(result.get(1).getTitle()).isEqualTo("공지2");
    }

    @Test
    @DisplayName("구성원이 아닌 사용자는 공지사항을 조회할 수 없다")
    void find_all_by_non_member_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        int page = 0;
        int size = 10;

        // 구성원이 아닌 경우를 시뮬레이션
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.empty());

        // when & then
        FindAnnouncementQuery query = new FindAnnouncementQuery(familyId, currentUserId, page, size);
        
        assertThatThrownBy(() -> sut.findAll(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_FAMILY_MEMBER);
    }

    @Test
    @DisplayName("OWNER가 공지사항을 삭제할 수 있다")
    void delete_announcement_by_owner_should_succeed() {
        // given
        Long announcementId = 1L;
        Long familyId = 1L;
        Long currentUserId = 2L;

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "소유자", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 삭제할 공지사항 (다른 사용자가 작성)
        Announcement announcement = Announcement.withId(
            announcementId, familyId, "공지1", "내용1", 3L, LocalDateTime.now(), null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findAnnouncementPort.findById(announcementId))
            .thenReturn(Optional.of(announcement));
        doNothing().when(saveAnnouncementPort).deleteById(announcementId);

        // when
        DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
            announcementId, familyId, currentUserId
        );
        sut.delete(command);

        // then
        verify(saveAnnouncementPort, times(1)).deleteById(announcementId);
    }

    @Test
    @DisplayName("ADMIN은 자신이 작성한 공지사항만 삭제할 수 있다")
    void delete_own_announcement_by_admin_should_succeed() {
        // given
        Long announcementId = 1L;
        Long familyId = 1L;
        Long currentUserId = 2L;

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "관리자", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // 삭제할 공지사항 (자신이 작성)
        Announcement announcement = Announcement.withId(
            announcementId, familyId, "공지1", "내용1", currentUserId, LocalDateTime.now(), null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findAnnouncementPort.findById(announcementId))
            .thenReturn(Optional.of(announcement));
        doNothing().when(saveAnnouncementPort).deleteById(announcementId);

        // when
        DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
            announcementId, familyId, currentUserId
        );
        sut.delete(command);

        // then
        verify(saveAnnouncementPort, times(1)).deleteById(announcementId);
    }

    @Test
    @DisplayName("ADMIN은 다른 사용자가 작성한 공지사항을 삭제할 수 없다")
    void delete_others_announcement_by_admin_should_throw_exception() {
        // given
        Long announcementId = 1L;
        Long familyId = 1L;
        Long currentUserId = 2L;

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "관리자", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // 삭제할 공지사항 (다른 사용자가 작성)
        Announcement announcement = Announcement.withId(
            announcementId, familyId, "공지1", "내용1", 3L, LocalDateTime.now(), null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findAnnouncementPort.findById(announcementId))
            .thenReturn(Optional.of(announcement));

        // when & then
        DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
            announcementId, familyId, currentUserId
        );

        assertThatThrownBy(() -> sut.delete(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_AUTHORIZED);

        verify(saveAnnouncementPort, never()).deleteById(announcementId);
    }

    @Test
    @DisplayName("일반 구성원은 공지사항을 삭제할 수 없다")
    void delete_announcement_by_member_should_throw_exception() {
        // given
        Long announcementId = 1L;
        Long familyId = 1L;
        Long currentUserId = 2L;

        // 일반 구성원 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "일반 구성원", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // when & then
        DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
            announcementId, familyId, currentUserId
        );
        
        assertThatThrownBy(() -> sut.delete(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_AUTHORIZED);
        
        verify(saveAnnouncementPort, never()).deleteById(announcementId);
    }

    @Test
    @DisplayName("존재하지 않는 공지사항 삭제 시도 시 예외가 발생한다")
    void delete_non_existing_announcement_should_throw_exception() {
        // given
        Long announcementId = 1L;
        Long familyId = 1L;
        Long currentUserId = 2L;

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withId(
            2L, familyId, currentUserId, "소유자", null, null, "profile.jpg",
            LocalDateTime.now(), null, FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 존재하지 않는 공지사항
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findAnnouncementPort.findById(announcementId))
            .thenReturn(Optional.empty());

        // when & then
        DeleteAnnouncementCommand command = new DeleteAnnouncementCommand(
            announcementId, familyId, currentUserId
        );
        
        assertThatThrownBy(() -> sut.delete(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.ANNOUNCEMENT_NOT_FOUND);
        
        verify(saveAnnouncementPort, never()).deleteById(announcementId);
    }
}