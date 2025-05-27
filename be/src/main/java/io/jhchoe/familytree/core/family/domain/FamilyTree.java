package io.jhchoe.familytree.core.family.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 가족트리 전체 구조를 나타내는 도메인 객체입니다.
 * 트리 시각화에 필요한 모든 정보를 포함합니다.
 */
public final class FamilyTree {
    
    private final Long familyId;
    private final FamilyTreeNode centerMember;
    private final List<FamilyTreeGeneration> generations;
    private final FamilyTreeMetadata metadata;
    
    /**
     * FamilyTree 객체를 생성합니다.
     *
     * @param familyId 가족 ID
     * @param centerMember 트리의 중심 구성원
     * @param generations 세대별 구성원 목록
     * @param metadata 트리 메타데이터
     */
    private FamilyTree(
        Long familyId,
        FamilyTreeNode centerMember,
        List<FamilyTreeGeneration> generations,
        FamilyTreeMetadata metadata
    ) {
        this.familyId = Objects.requireNonNull(familyId, "familyId must not be null");
        this.centerMember = Objects.requireNonNull(centerMember, "centerMember must not be null");
        this.generations = Collections.unmodifiableList(Objects.requireNonNull(generations, "generations must not be null"));
        this.metadata = Objects.requireNonNull(metadata, "metadata must not be null");
    }
    
    /**
     * 새로운 FamilyTree를 생성합니다.
     *
     * @param familyId 가족 ID
     * @param centerMember 트리의 중심 구성원
     * @param allMembers 모든 구성원 목록
     * @return 생성된 FamilyTree 객체
     */
    public static FamilyTree create(
        Long familyId,
        FamilyTreeNode centerMember,
        List<FamilyTreeNode> allMembers
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(centerMember, "centerMember must not be null");
        Objects.requireNonNull(allMembers, "allMembers must not be null");
        
        // 세대별로 구성원 그룹화
        List<FamilyTreeGeneration> generations = groupMembersByGeneration(allMembers);
        
        // 메타데이터 생성
        FamilyTreeMetadata metadata = FamilyTreeMetadata.create(allMembers, generations);
        
        return new FamilyTree(familyId, centerMember, generations, metadata);
    }
    
    /**
     * 구성원들을 세대별로 그룹화하여 FamilyTreeGeneration 목록을 생성합니다.
     *
     * @param allMembers 모든 구성원 목록
     * @return 세대별로 그룹화된 목록
     */
    private static List<FamilyTreeGeneration> groupMembersByGeneration(List<FamilyTreeNode> allMembers) {
        Map<Integer, List<FamilyTreeNode>> membersByGeneration = allMembers.stream()
            .collect(Collectors.groupingBy(FamilyTreeNode::generation));
        
        return membersByGeneration.entrySet().stream()
            .map(entry -> FamilyTreeGeneration.create(entry.getKey(), entry.getValue()))
            .sorted((g1, g2) -> Integer.compare(g1.level(), g2.level()))
            .collect(Collectors.toList());
    }
    
    /**
     * 가족 ID를 반환합니다.
     *
     * @return 가족 ID
     */
    public Long getFamilyId() {
        return familyId;
    }
    
    /**
     * 트리의 중심 구성원을 반환합니다.
     *
     * @return 중심 구성원
     */
    public FamilyTreeNode getCenterMember() {
        return centerMember;
    }
    
    /**
     * 세대별 구성원 목록을 반환합니다.
     *
     * @return 세대별 구성원 목록 (읽기 전용)
     */
    public List<FamilyTreeGeneration> getGenerations() {
        return generations;
    }
    
    /**
     * 트리 메타데이터를 반환합니다.
     *
     * @return 트리 메타데이터
     */
    public FamilyTreeMetadata getMetadata() {
        return metadata;
    }
    
    /**
     * 모든 구성원 목록을 반환합니다.
     *
     * @return 모든 구성원 목록
     */
    public List<FamilyTreeNode> getAllMembers() {
        return generations.stream()
            .flatMap(generation -> generation.members().stream())
            .collect(Collectors.toList());
    }
    
    /**
     * 지정된 구성원 ID로 구성원을 찾습니다.
     *
     * @param memberId 찾을 구성원 ID
     * @return 해당 구성원, 없으면 null
     */
    public FamilyTreeNode findMemberById(Long memberId) {
        return getAllMembers().stream()
            .filter(member -> member.memberId().equals(memberId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 트리가 비어있는지 확인합니다.
     *
     * @return 구성원이 없으면 true, 그렇지 않으면 false
     */
    public boolean isEmpty() {
        return generations.isEmpty() || getAllMembers().isEmpty();
    }
}
