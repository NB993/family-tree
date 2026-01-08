package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.application.port.in.MemberTagsInfo;
import io.jhchoe.familytree.core.family.application.port.in.ModifyMemberTagsCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyMemberTagsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 멤버 태그 할당 Controller입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class ModifyMemberTagsController {

    private final ModifyMemberTagsUseCase modifyMemberTagsUseCase;

    /**
     * 멤버에게 태그를 할당합니다.
     *
     * @param familyId Family ID
     * @param memberId Member ID
     * @param request  태그 할당 요청
     * @param user     인증된 사용자 정보
     * @return 할당된 태그 정보
     */
    @PutMapping("/{familyId}/members/{memberId}/tags")
    public ResponseEntity<ModifyMemberTagsResponse> modify(
        @RequestBody @Valid final ModifyMemberTagsRequest request,
        @AuthFTUser final FTUser user,
        @PathVariable final Long familyId,
        @PathVariable final Long memberId
    ) {
        ModifyMemberTagsCommand command = new ModifyMemberTagsCommand(
            familyId,
            memberId,
            request.tagIds()
        );

        MemberTagsInfo result = modifyMemberTagsUseCase.modify(command, user.getId());
        ModifyMemberTagsResponse response = ModifyMemberTagsResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
