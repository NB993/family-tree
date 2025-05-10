package io.jhchoe.familytree.core.relationship.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.LoadFamilyPort;
import io.jhchoe.familytree.core.relationship.adapter.in.request.DefineFamilyRelationshipRequest;
import io.jhchoe.familytree.core.relationship.adapter.in.response.FamilyRelationshipResponse;
import io.jhchoe.familytree.core.relationship.application.port.in.DefineFamilyRelationshipCommand;
import io.jhchoe.familytree.core.relationship.application.port.in.DefineFamilyRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyRelationshipQuery;
import io.jhchoe.familytree.core.relationship.application.port.in.FindFamilyRelationshipUseCase;
import io.jhchoe.familytree.core.relationship.application.port.in.FindMemberRelationshipsQuery;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationship;
import io.jhchoe.familytree.core.relationship.domain.FamilyRelationshipType;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가족 관계 관리 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/family-tree/members/{memberId}/relationships")
@RequiredArgsConstructor
public class FamilyRelationshipController {

    private final DefineFamilyRelationshipUseCase defineFamilyRelationshipUseCase;
    private final FindFamilyRelationshipUseCase findFamilyRelationshipUseCase;
    private final LoadFamilyPort loadFamilyPort;

    /**
     * 현재 로그인한 사용자에 대해 특정 대상과의 관계를 정의합니다.
     *
     * @param ftUser    현재 인증된 사용자
     * @param memberId  로그인한 사용자의 가족 구성원 ID
     * @param request   관계 정의 요청 정보
     * @return 생성/수정된 가족 관계 정보
     */
    @PostMapping
    public ResponseEntity<FamilyRelationshipResponse> defineRelationship(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long memberId,
        @Valid @RequestBody DefineFamilyRelationshipRequest request
    ) {
        // TODO: 실제 구현 시에는 사용자에게 해당 Family와 구성원 권한이 있는지 확인해야 합니다.
        // 여기서는 간단히 memberId가 사용자의 것인지만 검증합니다.
        
        // 1. 요청에서 familyId 가져오기 - 실제 구현 시 구성원으로 가족 ID 조회 필요
        Long familyId = 1L; // 임시 값, 실제로는 구성원 ID로 가족 ID 조회
        
        // 2. Command 객체 생성
        DefineFamilyRelationshipCommand command = new DefineFamilyRelationshipCommand(
            familyId,
            memberId, // 현재 로그인한 사용자의 구성원 ID
            request.toMemberId(), // 관계를 정의할 대상 ID
            request.relationshipType(),
            request.customRelationship(),
            request.description()
        );
        
        // 3. 유스케이스 실행
        Long relationshipId = defineFamilyRelationshipUseCase.defineRelationship(command);
        
        // 4. 생성/수정된 관계 조회
        FindFamilyRelationshipQuery query = new FindFamilyRelationshipQuery(
            familyId,
            memberId,
            request.toMemberId()
        );
        
        FamilyRelationship relationship = findFamilyRelationshipUseCase.findRelationship(query)
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "relationship"));
        
        // 5. 응답 반환
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(FamilyRelationshipResponse.from(relationship));
    }
    
    /**
     * 현재 로그인한 사용자가 정의한 모든 가족 관계를 조회합니다.
     *
     * @param ftUser   현재 인증된 사용자
     * @param memberId 로그인한 사용자의 가족 구성원 ID
     * @return 가족 관계 목록
     */
    @GetMapping
    public ResponseEntity<List<FamilyRelationshipResponse>> getAllRelationships(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long memberId
    ) {
        // TODO: 실제 구현 시에는 사용자에게 해당 Family와 구성원 권한이 있는지 확인해야 합니다.
        // 여기서는 간단히 memberId가 사용자의 것인지만 검증합니다.
        
        // 1. 요청에서 familyId 가져오기 - 실제 구현 시 구성원으로 가족 ID 조회 필요
        Long familyId = 1L; // 임시 값, 실제로는 구성원 ID로 가족 ID 조회
        
        // 2. 쿼리 객체 생성
        FindMemberRelationshipsQuery query = new FindMemberRelationshipsQuery(
            familyId,
            memberId
        );
        
        // 3. 유스케이스 실행
        List<FamilyRelationship> relationships = findFamilyRelationshipUseCase.findAllRelationshipsByMember(query);
        
        // 4. 응답 반환
        List<FamilyRelationshipResponse> responses = relationships.stream()
            .map(FamilyRelationshipResponse::from)
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
    @GetMapping("/{toMemberId}")
    public ResponseEntity<FamilyRelationshipResponse> getRelationship(
        @AuthFTUser FTUser ftUser,
        @PathVariable Long memberId,
        @PathVariable Long toMemberId
    ) {
        // TODO: 실제 구현 시에는 사용자에게 해당 Family와 구성원 권한이 있는지 확인해야 합니다.
        // 여기서는 간단히 memberId가 사용자의 것인지만 검증합니다.
        
        // 1. 요청에서 familyId 가져오기 - 실제 구현 시 구성원으로 가족 ID 조회 필요
        Long familyId = 1L; // 임시 값, 실제로는 구성원 ID로 가족 ID 조회
        
        // 2. 쿼리 객체 생성
        FindFamilyRelationshipQuery query = new FindFamilyRelationshipQuery(
            familyId,
            memberId,
            toMemberId
        );
        
        // 3. 유스케이스 실행
        FamilyRelationship relationship = findFamilyRelationshipUseCase.findRelationship(query)
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "relationship"));
        
        // 4. 응답 반환
        return ResponseEntity.ok(FamilyRelationshipResponse.from(relationship));
    }
    
    /**
     * 가능한 가족 관계 타입 목록을 반환합니다.
     *
     * @return 가족 관계 타입 목록
     */
    @GetMapping("/types")
    public ResponseEntity<List<RelationshipTypeDto>> getRelationshipTypes() {
        List<RelationshipTypeDto> types = List.of(FamilyRelationshipType.values()).stream()
            .map(type -> new RelationshipTypeDto(
                type.name(),
                type.getDisplayName()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(types);
    }
    
    /**
     * 관계 타입 DTO 레코드
     */
    private record RelationshipTypeDto(String code, String displayName) {}
}
