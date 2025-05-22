package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.support.ApiResponse;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyMemberStatusRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.ModifyFamilyMemberStatusResponse;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberStatusCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberStatusUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 구성원 상태 변경 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/members")
public class ModifyFamilyMemberStatusController {

    private final ModifyFamilyMemberStatusUseCase modifyFamilyMemberStatusUseCase;
    
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
    public ApiResponse<ModifyFamilyMemberStatusResponse> modifyMemberStatus(
        @PathVariable Long familyId,
        @PathVariable Long memberId,
        @Valid @RequestBody ModifyFamilyMemberStatusRequest request,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        Long updatedMemberId = modifyFamilyMemberStatusUseCase.modifyStatus(
            new ModifyFamilyMemberStatusCommand(
                familyId,
                memberId,
                userId,
                request.getStatus(),
                request.getReason()
            )
        );
        
        return ApiResponse.ok(new ModifyFamilyMemberStatusResponse(updatedMemberId));
    }
}
