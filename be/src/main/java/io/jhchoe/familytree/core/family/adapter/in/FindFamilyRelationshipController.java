package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindAllFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberRelationshipResponse;
import io.jhchoe.familytree.core.family.adapter.in.response.FamilyMemberRelationshipTypeResponse;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가족 관계 관리 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FindFamilyRelationshipController {

    private final FindFamilyMemberRelationshipUseCase findFamilyMemberRelationshipUseCase;
    private final FindFamilyPort findFamilyPort;

    /**
     * 현재 로그인한 사용자가 정의한 모든 가족 관계를 조회합니다.
     *
     * @param ftUser   현재 인증된 사용자
     * @return 가족 관계 목록
     */
    @GetMapping("/{familyId}/members/relationships")
    public ResponseEntity<List<FamilyMemberRelationshipResponse>> findAllFamilyMemberRelationships(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long familyId
    ) {
        FindAllFamilyMemberRelationshipsQuery query = new FindAllFamilyMemberRelationshipsQuery(
            familyId, ftUser.getId());

        List<FamilyMemberRelationship> relationships = findFamilyMemberRelationshipUseCase.findAll(query);
        
        List<FamilyMemberRelationshipResponse> responses = relationships.stream()
            .map(FamilyMemberRelationshipResponse::from)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 현재 로그인한 사용자와 특정 대상 간의 관계를 조회합니다.
     *
     * @param ftUser    현재 인증된 사용자
     * @return 가족 관계 정보
     */
    @GetMapping("/{familyId}/members/{toMemberId}/relationships")
    public ResponseEntity<FamilyMemberRelationshipResponse> findFamilyMemberRelationship(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long familyId,
        @PathVariable Long toMemberId
    ) {
        FindFamilyMemberRelationshipQuery query = new FindFamilyMemberRelationshipQuery(
            familyId, ftUser.getId(), toMemberId);

        FamilyMemberRelationship relationship = findFamilyMemberRelationshipUseCase.find(query);

        // 4. 응답 반환
        return ResponseEntity.ok(FamilyMemberRelationshipResponse.from(relationship));
    }
    
    /**
     * 가능한 가족 관계 타입 목록을 반환합니다.
     *
     * @return 가족 관계 타입 목록
     */
    @GetMapping("/members/relationship-types")
    public ResponseEntity<List<FamilyMemberRelationshipTypeResponse>> findRelationshipTypes() {
        List<FamilyMemberRelationshipTypeResponse> types = List.of(FamilyMemberRelationshipType.values()).stream()
            .map(type -> new FamilyMemberRelationshipTypeResponse(
                type.name(),
                type.getDisplayName()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(types);
    }
}
