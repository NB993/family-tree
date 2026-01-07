package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FamilyMemberTag 삭제 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/tags")
public class DeleteFamilyMemberTagController {

    private final DeleteFamilyMemberTagUseCase deleteFamilyMemberTagUseCase;

    /**
     * 태그를 삭제합니다.
     *
     * @param familyId Family ID
     * @param tagId    태그 ID
     * @param user     인증된 사용자 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(
        @PathVariable final Long familyId,
        @PathVariable final Long tagId,
        @AuthFTUser final FTUser user
    ) {
        Long userId = user.getId();

        DeleteFamilyMemberTagCommand command = new DeleteFamilyMemberTagCommand(
            familyId,
            tagId
        );

        deleteFamilyMemberTagUseCase.delete(command, userId);

        return ResponseEntity.noContent().build();
    }
}
