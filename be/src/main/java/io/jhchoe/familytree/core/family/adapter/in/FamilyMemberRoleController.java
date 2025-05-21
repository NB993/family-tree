package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.support.ApiResponse;
import io.jhchoe.familytree.core.family.adapter.in.request.UpdateFamilyMemberRoleRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.UpdateFamilyMemberRoleResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleUseCase;
import io.jhchoe.familytree.core.family.application.port.in.UpdateFamilyMemberRoleCommand;
import io.jhchoe.familytree.core.family.application.port.in.UpdateFamilyMemberRoleUseCase;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 구성원 역할 관리 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/members")
public class FamilyMemberRoleController {

    private final UpdateFamilyMemberRoleUseCase updateFamilyMemberRoleUseCase;
    private final FindFamilyMembersRoleUseCase findFamilyMembersRoleUseCase;
    
    /**
     * 구성원 역할 목록을 조회합니다.
     *
     * @param familyId 가족 ID
     * @param user     인증된 사용자 정보
     * @return 구성원 역할 목록 응답
     */
    @GetMapping("/roles")
    public ApiResponse<List<FamilyMemberResponse>> getFamilyMembersRoles(
        @PathVariable Long familyId,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        List<FamilyMember> members = findFamilyMembersRoleUseCase.findAllByFamilyId(
            new FindFamilyMembersRoleQuery(familyId, userId)
        );
        
        return ApiResponse.ok(
            members.stream()
                .map(FamilyMemberResponse::from)
                .collect(Collectors.toList())
        );
    }
    
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
    public ApiResponse<UpdateFamilyMemberRoleResponse> updateMemberRole(
        @PathVariable Long familyId,
        @PathVariable Long memberId,
        @Valid @RequestBody UpdateFamilyMemberRoleRequest request,
        @AuthenticationPrincipal OAuth2User user
    ) {
        Long userId = Long.valueOf(user.getName());
        
        Long updatedMemberId = updateFamilyMemberRoleUseCase.updateRole(
            new UpdateFamilyMemberRoleCommand(
                familyId,
                memberId,
                userId,
                request.getRole()
            )
        );
        
        return ApiResponse.ok(new UpdateFamilyMemberRoleResponse(updatedMemberId));
    }
}
