package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyMemberInfoRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.ModifyFamilyMemberInfoResponse;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberInfoCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberInfoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 구성원 기본 정보 변경 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/members")
public class ModifyFamilyMemberInfoController {

    private final ModifyFamilyMemberInfoUseCase modifyFamilyMemberInfoUseCase;

    /**
     * 구성원의 기본 정보(이름, 생일, 생일타입)를 변경합니다.
     *
     * @param familyId  가족 ID
     * @param memberId  구성원 ID
     * @param request   변경 요청 정보
     * @param user      인증된 사용자 정보
     * @return 변경 결과 응답
     */
    @PutMapping("/{memberId}/info")
    public ResponseEntity<ModifyFamilyMemberInfoResponse> modifyMemberInfo(
        @PathVariable Long familyId,
        @PathVariable Long memberId,
        @Valid @RequestBody ModifyFamilyMemberInfoRequest request,
        @AuthFTUser FTUser user
    ) {
        Long userId = user.getId();

        Long updatedMemberId = modifyFamilyMemberInfoUseCase.modifyInfo(
            new ModifyFamilyMemberInfoCommand(
                familyId,
                memberId,
                userId,
                request.name(),
                request.birthday(),
                request.birthdayType()
            )
        );

        return ResponseEntity.ok(new ModifyFamilyMemberInfoResponse(updatedMemberId));
    }
}
