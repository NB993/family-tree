package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberWithRelationshipResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberWithRelationshipResponse.TagInfo;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMembersWithRelationshipsResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final FindFamilyMemberTagMappingPort findFamilyMemberTagMappingPort;

    /**
     * Family 홈용 구성원 목록을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @param ftUser 인증된 사용자 정보
     * @return Family 홈 구성원 목록
     */
    @GetMapping("/{familyId}/home/members")
    public ResponseEntity<List<FamilyMemberWithRelationshipResponse>> findFamilyHomeMembers(
        @PathVariable Long familyId,
        @AuthFTUser FTUser ftUser
    ) {
        Long currentUserId = ftUser.getId();

        // 1. 전체 구성원 조회 (내부에서 권한 검증 포함)
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery memberQuery =
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);
        List<FamilyMember> members = findFamilyMemberUseCase.findAll(memberQuery);

        // 2. 현재 사용자의 memberId 찾기
        Long currentMemberId = members.stream()
            .filter(m -> m.getUserId().equals(currentUserId))
            .findFirst()
            .map(FamilyMember::getId)
            .orElseThrow(() -> new IllegalStateException("현재 사용자의 구성원 정보를 찾을 수 없습니다."));

        // 3. 태그 정보 조회 및 매핑
        Map<Long, List<TagInfo>> memberTagsMap = buildMemberTagsMap(familyId, members);

        // 4. 응답 변환 (관계 정보 없이, 태그 정보 포함)
        FamilyMembersWithRelationshipsResponse responseDTO =
            new FamilyMembersWithRelationshipsResponse(members, Collections.emptyList());

        List<FamilyMemberWithRelationshipResponse> response =
            responseDTO.toMemberWithRelationships(currentMemberId, memberTagsMap);

        return ResponseEntity.ok(response);
    }

    /**
     * 멤버별 태그 정보 맵을 생성합니다.
     */
    private Map<Long, List<TagInfo>> buildMemberTagsMap(Long familyId, List<FamilyMember> members) {
        // 1. Family의 모든 태그 조회
        List<FamilyMemberTag> allTags = findFamilyMemberTagPort.findAllByFamilyId(familyId);
        Map<Long, FamilyMemberTag> tagMap = allTags.stream()
            .collect(Collectors.toMap(FamilyMemberTag::getId, Function.identity()));

        // 2. 각 멤버의 태그 매핑 조회 및 변환
        Map<Long, List<TagInfo>> memberTagsMap = new HashMap<>();
        for (FamilyMember member : members) {
            List<FamilyMemberTagMapping> mappings =
                findFamilyMemberTagMappingPort.findAllByMemberId(member.getId());

            List<TagInfo> tags = mappings.stream()
                .map(mapping -> tagMap.get(mapping.getTagId()))
                .filter(tag -> tag != null)
                .map(TagInfo::from)
                .toList();

            memberTagsMap.put(member.getId(), tags);
        }

        return memberTagsMap;
    }
}
