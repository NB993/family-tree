package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.*;
import io.jhchoe.familytree.core.family.application.port.out.FindAnnouncementPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveAnnouncementPort;
import io.jhchoe.familytree.core.family.domain.Announcement;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공지사항 관리를 위한 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class AnnouncementService implements SaveAnnouncementUseCase, FindAnnouncementUseCase, DeleteAnnouncementUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final SaveAnnouncementPort saveAnnouncementPort;
    private final FindAnnouncementPort findAnnouncementPort;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveAnnouncementCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        // 1. 현재 사용자가 해당 Family의 구성원인지 확인하고 역할 검증
        FamilyMember currentMember = findFamilyMemberPort.findByFamilyIdAndUserId(
                command.getFamilyId(), command.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 2. OWNER 또는 ADMIN만 공지사항 작성 가능
        if (!currentMember.hasRoleAtLeast(FamilyMemberRole.ADMIN)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
        
        // 3. 공지사항 생성
        Announcement announcement = Announcement.create(
            command.getFamilyId(),
            command.getTitle(),
            command.getContent()
        );
        
        // 4. 저장
        return saveAnnouncementPort.save(announcement);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Announcement> findAll(FindAnnouncementQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        // 1. 현재 사용자가 해당 Family의 구성원인지 확인
        findFamilyMemberPort.findByFamilyIdAndUserId(
                query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 2. 공지사항 목록 조회
        return findAnnouncementPort.findAllByFamilyId(
            query.getFamilyId(), 
            query.getPage(), 
            query.getSize()
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Announcement findById(FindAnnouncementByIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        // 1. 현재 사용자가 해당 Family의 구성원인지 확인
        findFamilyMemberPort.findByFamilyIdAndUserId(
                query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 2. 공지사항 조회
        Announcement announcement = findAnnouncementPort.findById(query.getId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.ANNOUNCEMENT_NOT_FOUND));
        
        // 3. 해당 Family의 공지사항인지 확인
        if (!announcement.getFamilyId().equals(query.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.ANNOUNCEMENT_NOT_FOUND);
        }
        
        return announcement;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(DeleteAnnouncementCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        // 1. 현재 사용자가 해당 Family의 구성원인지 확인하고 역할 검증
        FamilyMember currentMember = findFamilyMemberPort.findByFamilyIdAndUserId(
                command.getFamilyId(), command.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
        
        // 2. OWNER 또는 ADMIN만 공지사항 삭제 가능
        if (!currentMember.hasRoleAtLeast(FamilyMemberRole.ADMIN)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
        
        // 3. 공지사항 조회
        Announcement announcement = findAnnouncementPort.findById(command.getId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.ANNOUNCEMENT_NOT_FOUND));
        
        // 4. 해당 Family의 공지사항인지 확인
        if (!announcement.getFamilyId().equals(command.getFamilyId())) {
            throw new FTException(FamilyExceptionCode.ANNOUNCEMENT_NOT_FOUND);
        }
        
        // 5. ADMIN은 자신이 작성한 공지사항만 삭제 가능 (OWNER는 모든 공지사항 삭제 가능)
        if (currentMember.getRole() == FamilyMemberRole.ADMIN 
                && !Objects.equals(announcement.getCreatedBy(), currentMember.getUserId())) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }
        
        // 6. 삭제
        saveAnnouncementPort.deleteById(command.getId());
    }
}
