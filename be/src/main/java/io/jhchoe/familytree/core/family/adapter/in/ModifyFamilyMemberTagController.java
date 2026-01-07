package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyMemberTagRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberTagResponse;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagInfo;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FamilyMemberTag 수정 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/tags")
public class ModifyFamilyMemberTagController {

    private final ModifyFamilyMemberTagUseCase modifyFamilyMemberTagUseCase;

    /**
     * 태그를 수정합니다.
     *
     * @param familyId Family ID
     * @param tagId    태그 ID
     * @param request  태그 수정 요청 정보
     * @param user     인증된 사용자 정보
     * @return 수정된 태그 정보
     */
    @PutMapping("/{tagId}")
    public ResponseEntity<FamilyMemberTagResponse> modifyTag(
        @PathVariable final Long familyId,
        @PathVariable final Long tagId,
        @Valid @RequestBody final ModifyFamilyMemberTagRequest request,
        @AuthFTUser final FTUser user
    ) {
        Long userId = user.getId();

        ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
            familyId,
            tagId,
            request.getName(),
            request.getColor()
        );

        FamilyMemberTagInfo modifiedTag = modifyFamilyMemberTagUseCase.modify(command, userId);

        return ResponseEntity.ok(FamilyMemberTagResponse.from(modifiedTag));
    }
}
