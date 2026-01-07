package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.request.ModifyFamilyMemberTagRequest;
import io.jhchoe.familytree.core.family.adapter.in.request.SaveFamilyMemberTagRequest;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberTagResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.SaveFamilyMemberTagResponse;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagInfo;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagsQuery;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagUseCase;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FamilyMember 태그 관리 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/tags")
public class FamilyMemberTagController {

    private final SaveFamilyMemberTagUseCase saveFamilyMemberTagUseCase;
    private final FindFamilyMemberTagUseCase findFamilyMemberTagUseCase;
    private final ModifyFamilyMemberTagUseCase modifyFamilyMemberTagUseCase;
    private final DeleteFamilyMemberTagUseCase deleteFamilyMemberTagUseCase;

    /**
     * 태그를 생성합니다.
     *
     * @param familyId Family ID
     * @param request  태그 생성 요청 정보
     * @param user     인증된 사용자 정보
     * @return 생성된 태그의 ID
     */
    @PostMapping
    public ResponseEntity<SaveFamilyMemberTagResponse> saveTag(
        @PathVariable Long familyId,
        @Valid @RequestBody SaveFamilyMemberTagRequest request,
        @AuthFTUser FTUser user
    ) {
        Long userId = user.getId();

        SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(
            familyId,
            request.getName()
        );

        Long savedId = saveFamilyMemberTagUseCase.save(command, userId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new SaveFamilyMemberTagResponse(savedId));
    }

    /**
     * Family의 모든 태그를 조회합니다.
     *
     * @param familyId Family ID
     * @param user     인증된 사용자 정보
     * @return 태그 목록
     */
    @GetMapping
    public ResponseEntity<List<FamilyMemberTagResponse>> findTags(
        @PathVariable Long familyId,
        @AuthFTUser FTUser user
    ) {
        Long userId = user.getId();

        FindFamilyMemberTagsQuery query = new FindFamilyMemberTagsQuery(familyId);

        List<FamilyMemberTagInfo> tags = findFamilyMemberTagUseCase.findAll(query, userId);

        List<FamilyMemberTagResponse> responses = tags.stream()
            .map(FamilyMemberTagResponse::from)
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

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
        @PathVariable Long familyId,
        @PathVariable Long tagId,
        @Valid @RequestBody ModifyFamilyMemberTagRequest request,
        @AuthFTUser FTUser user
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
        @PathVariable Long familyId,
        @PathVariable Long tagId,
        @AuthFTUser FTUser user
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
