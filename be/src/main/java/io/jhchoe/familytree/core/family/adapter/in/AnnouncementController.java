package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.support.ApiResponse;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveAnnouncementRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.AnnouncementResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.SaveAnnouncementResponse;
import io.jhchoe.familytree.core.family.application.port.in.*;
import io.jhchoe.familytree.core.family.domain.Announcement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항 관리 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/announcements")
public class AnnouncementController {

    private final SaveAnnouncementUseCase saveAnnouncementUseCase;
    private final FindAnnouncementUseCase findAnnouncementUseCase;
    private final DeleteAnnouncementUseCase deleteAnnouncementUseCase;
    
    /**
     * 공지사항 목록을 조회합니다.
     *
     * @param familyId 가족 ID
     * @param page     페이지 번호 (0부터 시작)
     * @param size     페이지 크기
     * @param user     인증된 사용자 정보
     * @return 공지사항 목록 응답
     */
    @GetMapping
    public ApiResponse<List<AnnouncementResponse>> getAnnouncements(
        @PathVariable Long familyId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        List<Announcement> announcements = findAnnouncementUseCase.findAll(
            new FindAnnouncementQuery(familyId, userId, page, size)
        );
        
        return ApiResponse.ok(
            announcements.stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList())
        );
    }
    
    /**
     * 특정 공지사항을 조회합니다.
     *
     * @param familyId        가족 ID
     * @param announcementId  공지사항 ID
     * @param user            인증된 사용자 정보
     * @return 공지사항 응답
     */
    @GetMapping("/{announcementId}")
    public ApiResponse<AnnouncementResponse> getAnnouncement(
        @PathVariable Long familyId,
        @PathVariable Long announcementId,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        Announcement announcement = findAnnouncementUseCase.findById(
            new FindAnnouncementByIdQuery(announcementId, familyId, userId)
        );
        
        return ApiResponse.ok(AnnouncementResponse.from(announcement));
    }
    
    /**
     * 공지사항을 저장합니다.
     *
     * @param familyId  가족 ID
     * @param request   저장 요청 정보
     * @param user      인증된 사용자 정보
     * @return 저장 결과 응답
     */
    @PostMapping
    public ApiResponse<SaveAnnouncementResponse> saveAnnouncement(
        @PathVariable Long familyId,
        @Valid @RequestBody SaveAnnouncementRequest request,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        Long savedId = saveAnnouncementUseCase.save(
            new SaveAnnouncementCommand(
                familyId,
                userId,
                request.getTitle(),
                request.getContent()
            )
        );
        
        return ApiResponse.created(new SaveAnnouncementResponse(savedId));
    }
    
    /**
     * 공지사항을 삭제합니다.
     *
     * @param familyId        가족 ID
     * @param announcementId  공지사항 ID
     * @param user            인증된 사용자 정보
     * @return 삭제 결과 응답
     */
    @DeleteMapping("/{announcementId}")
    public ApiResponse<Void> deleteAnnouncement(
        @PathVariable Long familyId,
        @PathVariable Long announcementId,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        deleteAnnouncementUseCase.delete(
            new DeleteAnnouncementCommand(announcementId, familyId, userId)
        );
        
        return ApiResponse.ok();
    }
}
