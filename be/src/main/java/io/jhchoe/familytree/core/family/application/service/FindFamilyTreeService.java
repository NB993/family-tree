package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyTreeQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyTreeUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyTreePort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyTree;
import io.jhchoe.familytree.core.family.domain.FamilyTreeNode;
import io.jhchoe.familytree.core.family.domain.FamilyTreeRelation;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가족트리 조회 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyTreeService implements FindFamilyTreeUseCase {

    private final FindFamilyTreePort findFamilyTreePort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FamilyTree findFamilyTree(final FindFamilyTreeQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. 가족 존재 여부 확인
        Family family = findFamilyTreePort.findFamily(query.familyId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));

        // 2. 가족의 모든 활성 구성원 조회
        List<FamilyMember> familyMembers = findFamilyTreePort.findActiveFamilyMembers(query.familyId());
        
        if (familyMembers.isEmpty()) {
            // 구성원이 없는 경우 빈 트리 반환
            return createEmptyFamilyTree(query.familyId());
        }

        // 3. 중심 구성원 결정
        FamilyMember centerMember = determineCenterMember(familyMembers, query.centerMemberId());

        // 4. 가족 구성원 간 관계 조회
        List<FamilyMemberRelationship> relationships = findFamilyTreePort.findFamilyMemberRelationships(query.familyId());

        // 5. 트리 구조 생성
        return buildFamilyTree(query.familyId(), centerMember, familyMembers, relationships, query.getMaxGenerationsOrDefault());
    }

    /**
     * 빈 가족트리를 생성합니다.
     *
     * @param familyId 가족 ID
     * @return 빈 가족트리
     */
    private FamilyTree createEmptyFamilyTree(Long familyId) {
        // 임시 중심 구성원 생성 (빈 트리용)
        FamilyTreeNode emptyCenterMember = new FamilyTreeNode(
            -1L, "빈 가족", null, null, null,
            FamilyMemberRole.MEMBER,
            FamilyMemberStatus.ACTIVE,
            false, 1, List.of()
        );

        return FamilyTree.create(familyId, emptyCenterMember, List.of());
    }

    /**
     * 중심 구성원을 결정합니다.
     *
     * @param familyMembers 가족 구성원 목록
     * @param centerMemberId 요청된 중심 구성원 ID (nullable)
     * @return 중심 구성원
     */
    private FamilyMember determineCenterMember(List<FamilyMember> familyMembers, Long centerMemberId) {
        if (centerMemberId != null) {
            return familyMembers.stream()
                .filter(member -> member.getId().equals(centerMemberId))
                .findFirst()
                .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));
        }

        // 중심 구성원이 지정되지 않은 경우 첫 번째 구성원을 중심으로 설정
        return familyMembers.get(0);
    }

    /**
     * 가족트리를 구성합니다.
     *
     * @param familyId 가족 ID
     * @param centerMember 중심 구성원
     * @param familyMembers 모든 가족 구성원
     * @param relationships 구성원 간 관계
     * @param maxGenerations 최대 세대 수
     * @return 구성된 가족트리
     */
    private FamilyTree buildFamilyTree(
        Long familyId,
        FamilyMember centerMember,
        List<FamilyMember> familyMembers,
        List<FamilyMemberRelationship> relationships,
        int maxGenerations
    ) {
        // 관계 맵 생성 (fromMemberId -> List<Relationship>)
        Map<Long, List<FamilyMemberRelationship>> relationshipMap = relationships.stream()
            .collect(Collectors.groupingBy(FamilyMemberRelationship::getFromMemberId));

        // 구성원별 세대 계산
        Map<Long, Integer> memberGenerations = calculateGenerations(centerMember, familyMembers, relationshipMap, maxGenerations);

        // FamilyTreeNode 생성
        List<FamilyTreeNode> treeNodes = createTreeNodes(familyMembers, relationshipMap, memberGenerations, centerMember.getUserId());

        // 중심 구성원 노드 찾기
        FamilyTreeNode centerNode = treeNodes.stream()
            .filter(node -> node.memberId().equals(centerMember.getId()))
            .findFirst()
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));

        return FamilyTree.create(familyId, centerNode, treeNodes);
    }

    /**
     * 구성원별 세대를 계산합니다.
     *
     * @param centerMember 중심 구성원
     * @param familyMembers 모든 가족 구성원
     * @param relationshipMap 관계 맵
     * @param maxGenerations 최대 세대 수
     * @return 구성원 ID -> 세대 레벨 맵
     */
    private Map<Long, Integer> calculateGenerations(
        FamilyMember centerMember,
        List<FamilyMember> familyMembers,
        Map<Long, List<FamilyMemberRelationship>> relationshipMap,
        int maxGenerations
    ) {
        Map<Long, Integer> generations = new HashMap<>();
        
        // 중심 구성원을 1세대(부모 세대)로 설정
        int centerGeneration = 1;
        generations.put(centerMember.getId(), centerGeneration);

        // BFS 방식으로 세대 계산
        calculateGenerationsBFS(centerMember.getId(), centerGeneration, relationshipMap, generations, maxGenerations);

        return generations;
    }

    /**
     * BFS 방식으로 세대를 계산합니다.
     *
     * @param currentMemberId 현재 구성원 ID
     * @param currentGeneration 현재 세대
     * @param relationshipMap 관계 맵
     * @param generations 세대 결과 맵
     * @param maxGenerations 최대 세대 수
     */
    private void calculateGenerationsBFS(
        Long currentMemberId,
        int currentGeneration,
        Map<Long, List<FamilyMemberRelationship>> relationshipMap,
        Map<Long, Integer> generations,
        int maxGenerations
    ) {
        List<FamilyMemberRelationship> currentRelationships = relationshipMap.getOrDefault(currentMemberId, List.of());

        for (FamilyMemberRelationship relationship : currentRelationships) {
            Long toMemberId = relationship.getToMemberId();
            
            // 이미 세대가 계산된 구성원은 스킵
            if (generations.containsKey(toMemberId)) {
                continue;
            }

            int targetGeneration = calculateTargetGeneration(currentGeneration, relationship);
            
            // 최대 세대 범위 내에서만 추가
            if (targetGeneration >= 0 && targetGeneration < maxGenerations) {
                generations.put(toMemberId, targetGeneration);
                
                // 재귀적으로 다음 세대 계산
                calculateGenerationsBFS(toMemberId, targetGeneration, relationshipMap, generations, maxGenerations);
            }
        }
    }

    /**
     * 관계 유형에 따라 대상 구성원의 세대를 계산합니다.
     *
     * @param currentGeneration 현재 구성원의 세대
     * @param relationship 관계 정보
     * @return 대상 구성원의 세대
     */
    private int calculateTargetGeneration(int currentGeneration, FamilyMemberRelationship relationship) {
        return switch (relationship.getRelationshipType()) {
            // 상위 세대 (부모, 삼촌/이모)
            case FATHER, MOTHER, UNCLE, AUNT -> currentGeneration - 1;
            // 하위 세대 (자녀, 조카)
            case SON, DAUGHTER, NEPHEW, NIECE -> currentGeneration + 1;
            // 2세대 상위 (조부모)
            case GRANDFATHER, GRANDMOTHER -> currentGeneration - 2;
            // 2세대 하위 (손자녀)
            case GRANDSON, GRANDDAUGHTER -> currentGeneration + 2;
            // 동일 세대 (배우자, 형제자매, 사촌)
            case HUSBAND, WIFE, ELDER_BROTHER, ELDER_SISTER, YOUNGER_BROTHER, YOUNGER_SISTER, COUSIN -> currentGeneration;
            // 사용자 정의는 동일 세대로 처리
            case CUSTOM -> currentGeneration;
        };
    }

    /**
     * FamilyTreeNode 목록을 생성합니다.
     *
     * @param familyMembers 가족 구성원 목록
     * @param relationshipMap 관계 맵
     * @param memberGenerations 구성원별 세대 맵
     * @param currentUserId 현재 사용자 ID
     * @return FamilyTreeNode 목록
     */
    private List<FamilyTreeNode> createTreeNodes(
        List<FamilyMember> familyMembers,
        Map<Long, List<FamilyMemberRelationship>> relationshipMap,
        Map<Long, Integer> memberGenerations,
        Long currentUserId
    ) {
        List<FamilyTreeNode> treeNodes = new ArrayList<>();

        for (FamilyMember member : familyMembers) {
            // 세대가 계산되지 않은 구성원은 제외
            Integer generation = memberGenerations.get(member.getId());
            if (generation == null) {
                continue;
            }

            // 해당 구성원의 관계 목록 생성
            List<FamilyTreeRelation> relations = createTreeRelations(
                relationshipMap.getOrDefault(member.getId(), List.of()),
                memberGenerations.keySet()
            );

            FamilyTreeNode treeNode = FamilyTreeNode.from(member, currentUserId, generation, relations);
            treeNodes.add(treeNode);
        }

        return treeNodes;
    }

    /**
     * FamilyTreeRelation 목록을 생성합니다.
     *
     * @param relationships 해당 구성원의 관계 목록
     * @param includedMemberIds 트리에 포함된 구성원 ID 집합
     * @return FamilyTreeRelation 목록
     */
    private List<FamilyTreeRelation> createTreeRelations(
        List<FamilyMemberRelationship> relationships,
        Set<Long> includedMemberIds
    ) {
        return relationships.stream()
            .filter(rel -> includedMemberIds.contains(rel.getToMemberId())) // 트리에 포함된 구성원과의 관계만
            .map(FamilyTreeRelation::from)
            .collect(Collectors.toList());
    }
}
