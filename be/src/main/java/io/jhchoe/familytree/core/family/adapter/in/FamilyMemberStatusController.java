package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.support.ApiResponse;
import io.jhchoe.familytree.core.family.adapter.in.request.UpdateFamilyMemberStatusRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.UpdateFamilyMemberStatusResponse;
import io.jhchoe.familytree.core.family.application.port.in.UpdateFamilyMemberStatusCommand;
import io.jhchoe.familytree.core.family.application.port.in.UpdateFamilyMemberStatusUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 구성원 상태 관리 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/members")
public class FamilyMemberStatusController {

    private final UpdateFamilyMemberStatusUseCase updateFamilyMemberStatusUseCase;
    
    /**
     * 구성원의 상태를 변경합니다.
     *
     * @param familyId  가족 ID
     * @param memberId  구성원 ID
     * @param request   변경 요청 정보
     * @param user      인증된 사용자 정보
     * @return 변경 결과 응답
     */
    @PutMapping("/{memberId}/status")
    public ApiResponse<UpdateFamilyMemberStatusResponse> updateMemberStatus(
        @PathVariable Long familyId,
        @PathVariable Long memberId,
        @Valid @RequestBody UpdateFamilyMemberStatusRequest request,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        Long updatedMemberId = updateFamilyMemberStatusUseCase.updateStatus(
            new UpdateFamilyMemberStatusCommand(
                familyId,
                memberId,
                userId,
                request.getStatus(),
                request.getReason()
            )
        );
        
        return ApiResponse.ok(new UpdateFamilyMemberStatusResponse(updatedMemberId));
    }
}
