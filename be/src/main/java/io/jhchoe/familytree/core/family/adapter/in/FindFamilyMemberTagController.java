package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberTagResponse;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagInfo;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagsQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FamilyMemberTag 조회 API 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/families/{familyId}/tags")
public class FindFamilyMemberTagController {

    private final FindFamilyMemberTagUseCase findFamilyMemberTagUseCase;

    /**
     * Family의 모든 태그를 조회합니다.
     *
     * @param familyId Family ID
     * @param user     인증된 사용자 정보
     * @return 태그 목록
     */
    @GetMapping
    public ResponseEntity<List<FamilyMemberTagResponse>> findTags(
        @PathVariable final Long familyId,
        @AuthFTUser final FTUser user
    ) {
        Long userId = user.getId();

        FindFamilyMemberTagsQuery query = new FindFamilyMemberTagsQuery(familyId);

        List<FamilyMemberTagInfo> tags = findFamilyMemberTagUseCase.findAll(query, userId);

        List<FamilyMemberTagResponse> responses = tags.stream()
            .map(FamilyMemberTagResponse::from)
            .toList();

        return ResponseEntity.ok(responses);
    }
}
