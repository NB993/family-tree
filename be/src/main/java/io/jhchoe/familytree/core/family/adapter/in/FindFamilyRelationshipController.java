package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberRelationshipsQuery;
import io.jhchoe.familytree.core.family.application.port.out.LoadFamilyPort;
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
@RequestMapping("/api/families/members")
@RequiredArgsConstructor
public class FindFamilyRelationshipController {

    private final FindFamilyMemberRelationshipUseCase findFamilyMemberRelationshipUseCase;
    private final LoadFamilyPort loadFamilyPort;

    /**
     * 현재 로그인한 사용자가 정의한 모든 가족 관계를 조회합니다.
     *
     * @param ftUser   현재 인증된 사용자
     * @param memberId 로그인한 사용자의 가족 구성원 ID
     * @return 가족 관계 목록
     */
    @GetMapping("/{memberId}/relationships")
    public ResponseEntity<List<FamilyMemberRelationshipResponse>> findAllFamilyMemberRelationships(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long memberId
    ) {
        // TODO: 실제 구현 시에는 사용자에게 해당 Family와 구성원 권한이 있는지 확인해야 합니다.
        // 여기서는 간단히 memberId가 사용자의 것인지만 검증합니다.
        
        // 1. 요청에서 familyId 가져오기 - 실제 구현 시 구성원으로 가족 ID 조회 필요
        Long familyId = 1L; // 임시 값, 실제로는 구성원 ID로 가족 ID 조회
        
        // 2. 쿼리 객체 생성
        FindFamilyMemberRelationshipsQuery query = new FindFamilyMemberRelationshipsQuery(
            familyId,
            memberId
        );
        
        // 3. 유스케이스 실행
        List<FamilyMemberRelationship> relationships = findFamilyMemberRelationshipUseCase.findAllRelationshipsByMember(query);
        
        // 4. 응답 반환
        List<FamilyMemberRelationshipResponse> responses = relationships.stream()
            .map(FamilyMemberRelationshipResponse::from)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 현재 로그인한 사용자와 특정 대상 간의 관계를 조회합니다.
     *
     * @param ftUser    현재 인증된 사용자
     * @param memberId  로그인한 사용자의 가족 구성원 ID
     * @param toMemberId 관계를 조회할 대상 구성원 ID
     * @return 가족 관계 정보
     */
    @GetMapping("/{memberId}/relationships/{toMemberId}")
    public ResponseEntity<FamilyMemberRelationshipResponse> findFamilyMemberRelationship(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long memberId,
        @PathVariable Long toMemberId
    ) {
        // TODO: 실제 구현 시에는 사용자에게 해당 Family와 구성원 권한이 있는지 확인해야 합니다.
        // 여기서는 간단히 memberId가 사용자의 것인지만 검증합니다.
        
        // 1. 요청에서 familyId 가져오기 - 실제 구현 시 구성원으로 가족 ID 조회 필요
        Long familyId = 1L; // 임시 값, 실제로는 구성원 ID로 가족 ID 조회
        
        // 2. 쿼리 객체 생성
        FindFamilyMemberRelationshipQuery query = new FindFamilyMemberRelationshipQuery(
            familyId,
            memberId,
            toMemberId
        );
        
        // 3. 유스케이스 실행
        FamilyMemberRelationship relationship = findFamilyMemberRelationshipUseCase.findRelationship(query)
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "relationship"));
        
        // 4. 응답 반환
        return ResponseEntity.ok(FamilyMemberRelationshipResponse.from(relationship));
    }
    
    /**
     * 가능한 가족 관계 타입 목록을 반환합니다.
     *
     * @return 가족 관계 타입 목록
     */
    @GetMapping("/relationship-types")
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
