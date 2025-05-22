package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.support.ApiResponse;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyMemberRoleRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.ModifyFamilyMemberRoleResponse;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRoleCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRoleUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 구성원 역할 변경 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/members")
public class ModifyFamilyMemberRoleController {

    private final ModifyFamilyMemberRoleUseCase modifyFamilyMemberRoleUseCase;
    
    /**
     * 구성원의 역할을 변경합니다.
     *
     * @param familyId  가족 ID
     * @param memberId  구성원 ID
     * @param request   변경 요청 정보
     * @param user      인증된 사용자 정보
     * @return 변경 결과 응답
     */
    @PutMapping("/{memberId}/role")
    public ApiResponse<ModifyFamilyMemberRoleResponse> modifyMemberRole(
        @PathVariable Long familyId,
        @PathVariable Long memberId,
        @Valid @RequestBody ModifyFamilyMemberRoleRequest request,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        Long updatedMemberId = modifyFamilyMemberRoleUseCase.modifyRole(
            new ModifyFamilyMemberRoleCommand(
                familyId,
                memberId,
                userId,
                request.getRole()
            )
        );
        
        return ApiResponse.ok(new ModifyFamilyMemberRoleResponse(updatedMemberId));
    }
}
