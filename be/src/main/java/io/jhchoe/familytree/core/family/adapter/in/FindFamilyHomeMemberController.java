package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberWithRelationshipResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberWithRelationshipResponse.TagInfo;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberWithTagsInfo;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersWithTagsQuery;
import io.jhchoe.familytree.core.family.application.port.in.MemberTagsInfo.TagSimpleInfo;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 홈 구성원 목록 조회를 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class FindFamilyHomeMemberController {

    private final FindFamilyMemberUseCase findFamilyMemberUseCase;

    /**
     * Family 홈용 구성원 목록을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param ftUser   인증된 사용자 정보
     * @return Family 홈 구성원 목록
     */
    @GetMapping("/{familyId}/home/members")
    public ResponseEntity<List<FamilyMemberWithRelationshipResponse>> findFamilyHomeMembers(
        @PathVariable Long familyId,
        @AuthFTUser FTUser ftUser
    ) {
        // 1. Query 생성
        FindFamilyMembersWithTagsQuery query =
            new FindFamilyMembersWithTagsQuery(familyId, ftUser.getId());

        // 2. UseCase 호출 (태그 포함 조회)
        List<FamilyMemberWithTagsInfo> membersWithTags = findFamilyMemberUseCase.findAll(query);

        // 3. Response DTO 변환
        List<FamilyMemberWithRelationshipResponse> response = membersWithTags.stream()
            .map(info -> new FamilyMemberWithRelationshipResponse(
                info.member(),
                Optional.empty(),
                info.tags().stream()
                    .map(tag -> new TagInfo(tag.id(), tag.name(), tag.color()))
                    .toList()
            ))
            .toList();

        return ResponseEntity.ok(response);
    }
}
