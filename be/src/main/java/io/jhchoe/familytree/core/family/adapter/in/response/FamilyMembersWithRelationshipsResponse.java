package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 가족 구성원과 관계 정보를 조합하여 다양한 API 응답을 생성하는 일급 객체입니다.
 * 도메인 지식을 캡슐화하고 API별 맞춤 응답을 제공합니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>Family 홈 API용 데이터 변환</li>
 *   <li>관계 조회 최적화 (인덱스 맵 활용)</li>
 *   <li>권한별 필터링 지원</li>
 *   <li>향후 확장 가능한 변환 메서드 제공</li>
 * </ul>
 */
public class FamilyMembersWithRelationshipsResponse {
    
    private final List<FamilyMember> members;
    private final List<FamilyMemberRelationship> relationships;
    
    // 성능 최적화를 위한 관계 인덱스 맵 (지연 초기화)
    private Map<String, FamilyMemberRelationship> relationshipIndex;
    
    /**
     * FamilyMembersWithRelationshipsResponse 생성자입니다.
     * 
     * @param members 가족 구성원 목록 (null 불허)
     * @param relationships 가족 관계 목록 (null인 경우 빈 리스트로 처리)
     */
    public FamilyMembersWithRelationshipsResponse(List<FamilyMember> members, List<FamilyMemberRelationship> relationships) {
        Objects.requireNonNull(members, "members must not be null");
        
        // 방어적 복사로 불변성 보장
        this.members = List.copyOf(members);
        this.relationships = List.copyOf(Objects.requireNonNullElse(relationships, Collections.emptyList()));
    }
    
    /**
     * Family 홈 API 응답용 데이터 구조로 변환합니다.
     * 현재 사용자 기준으로 각 구성원과의 관계 정보를 매핑합니다.
     *
     * <p>변환 규칙:</p>
     * <ul>
     *   <li>ACTIVE 상태 구성원만 포함</li>
     *   <li>나이순(어린 순서) 정렬</li>
     *   <li>관계 정보가 없는 경우 Optional.empty() 처리</li>
     * </ul>
     *
     * @param currentUserId 현재 로그인한 사용자 ID (null 불허)
     * @return Family 홈 API 응답 리스트
     * @throws IllegalArgumentException currentUserId가 null이거나 0 이하인 경우
     */
    public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships(Long currentUserId) {
        return toMemberWithRelationships(currentUserId, Collections.emptyMap());
    }

    /**
     * Family 홈 API 응답용 데이터 구조로 변환합니다. (태그 정보 포함)
     * 현재 사용자 기준으로 각 구성원과의 관계 정보 및 태그 정보를 매핑합니다.
     *
     * <p>변환 규칙:</p>
     * <ul>
     *   <li>ACTIVE 상태 구성원만 포함</li>
     *   <li>나이순(어린 순서) 정렬</li>
     *   <li>관계 정보가 없는 경우 Optional.empty() 처리</li>
     *   <li>태그 정보가 없는 경우 빈 리스트 처리</li>
     * </ul>
     *
     * @param currentUserId 현재 로그인한 사용자 ID (null 불허)
     * @param memberTagsMap 멤버 ID별 태그 목록 맵
     * @return Family 홈 API 응답 리스트
     * @throws IllegalArgumentException currentUserId가 null이거나 0 이하인 경우
     */
    public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships(
        Long currentUserId,
        Map<Long, List<FamilyMemberWithRelationshipResponse.TagInfo>> memberTagsMap
    ) {
        Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        if (currentUserId <= 0) {
            throw new IllegalStateException("currentUserId must be positive");
        }

        Map<Long, List<FamilyMemberWithRelationshipResponse.TagInfo>> tagsMap =
            memberTagsMap != null ? memberTagsMap : Collections.emptyMap();

        return members.stream()
            .filter(member -> member.getStatus() == FamilyMemberStatus.ACTIVE)
            .map(member -> {
                Optional<FamilyMemberRelationship> relationship =
                    findRelationship(currentUserId, member.getId());
                List<FamilyMemberWithRelationshipResponse.TagInfo> tags =
                    tagsMap.getOrDefault(member.getId(), Collections.emptyList());
                return new FamilyMemberWithRelationshipResponse(member, relationship, tags);
            })
            .sorted(createAgeComparator()) // 나이순 정렬
            .toList();
    }
    
    /**
     * 특정 구성원이 다른 구성원에 대해 정의한 관계를 조회합니다.
     * 성능 최적화를 위해 인덱스 맵을 활용하여 O(1) 조회를 제공합니다.
     * 
     * @param fromMemberId 관계를 정의한 구성원 ID (null 불허)
     * @param toMemberId 관계가 정의된 대상 구성원 ID (null 불허)
     * @return 관계 정보 (없으면 Optional.empty())
     * @throws IllegalArgumentException fromMemberId 또는 toMemberId가 null이거나 0 이하인 경우
     */
    public Optional<FamilyMemberRelationship> findRelationship(Long fromMemberId, Long toMemberId) {
        Objects.requireNonNull(fromMemberId, "fromMemberId must not be null");
        if (fromMemberId <= 0) {
            throw new IllegalStateException("fromMemberId must be positive");
        }
        Objects.requireNonNull(toMemberId, "toMemberId must not be null");
        if (toMemberId <= 0) {
            throw new IllegalStateException("toMemberId must be positive");
        }
        
        initializeRelationshipIndex();
        String key = createRelationshipKey(fromMemberId, toMemberId);
        return Optional.ofNullable(relationshipIndex.get(key));
    }
    
    /**
     * ACTIVE 상태 구성원 목록을 반환합니다.
     * 
     * @return ACTIVE 상태 구성원 목록
     */
    public List<FamilyMember> getActiveMembers() {
        return members.stream()
            .filter(member -> member.getStatus() == FamilyMemberStatus.ACTIVE)
            .toList();
    }
    
    /**
     * 특정 구성원이 정의한 모든 관계를 Map 형태로 반환합니다.
     * 
     * @param memberId 구성원 ID (null 불허)
     * @return 대상 구성원 ID를 키로 하는 관계 맵
     * @throws IllegalArgumentException memberId가 null이거나 0 이하인 경우
     */
    public Map<Long, FamilyMemberRelationship> getRelationshipsFrom(Long memberId) {
        Objects.requireNonNull(memberId, "memberId must not be null");
        if (memberId <= 0) {
            throw new IllegalStateException("memberId must be positive");
        }
        
        return relationships.stream()
            .filter(rel -> rel.getFromMemberId().equals(memberId))
            .collect(Collectors.toMap(
                FamilyMemberRelationship::getToMemberId,
                Function.identity(),
                (existing, replacement) -> replacement // 중복 키 처리
            ));
    }
    
    /**
     * 관계 조회 성능 최적화를 위한 인덱스 맵을 초기화합니다.
     * 지연 초기화 패턴을 사용하여 필요할 때만 생성합니다.
     */
    private void initializeRelationshipIndex() {
        if (relationshipIndex == null) {
            relationshipIndex = relationships.stream()
                .collect(Collectors.toMap(
                    rel -> createRelationshipKey(rel.getFromMemberId(), rel.getToMemberId()),
                    Function.identity(),
                    (existing, replacement) -> replacement, // 중복 키 처리
                    HashMap::new
                ));
        }
    }
    
    /**
     * 관계 인덱스 키를 생성합니다.
     * 
     * @param fromMemberId 관계 정의자 ID
     * @param toMemberId 관계 대상자 ID
     * @return 인덱스 키 문자열
     */
    private String createRelationshipKey(Long fromMemberId, Long toMemberId) {
        return fromMemberId + ":" + toMemberId;
    }
    
    /**
     * 나이순(어린 순서) 정렬을 위한 Comparator를 생성합니다.
     * 생일 정보가 없는 구성원은 맨 뒤에 배치됩니다.
     * 
     * @return 나이순 정렬 Comparator
     */
    private Comparator<FamilyMemberWithRelationshipResponse> createAgeComparator() {
        return (response1, response2) -> {
            FamilyMember member1 = response1.getMember();
            FamilyMember member2 = response2.getMember();
            
            if (member1.getBirthday() == null && member2.getBirthday() == null) {
                return 0;
            }
            if (member1.getBirthday() == null) {
                return 1; // member1을 뒤로
            }
            if (member2.getBirthday() == null) {
                return -1; // member2를 뒤로
            }
            
            // 생일이 늦을수록 어리므로 앞에 배치 (내림차순)
            return member2.getBirthday().compareTo(member1.getBirthday());
        };
    }
    
    // 향후 확장을 위한 추가 변환 메서드들 (주석으로 예시 제공)
    
    /**
     * 가족트리 API용 노드 구조로 변환합니다. (향후 구현 예정)
     * 
     * @return 가족트리 노드 응답 리스트
     */
    // public List<FamilyTreeNodeResponse> toTreeNodes() {
    //     // 트리 구조 변환 로직 구현 예정
    //     throw new UnsupportedOperationException("Not implemented yet");
    // }
    
    /**
     * 관계 통계 정보로 변환합니다. (향후 구현 예정)
     * 
     * @return 관계 통계 응답
     */
    // public RelationshipStatisticsResponse toStatistics() {
    //     // 관계 통계 계산 로직 구현 예정
    //     throw new UnsupportedOperationException("Not implemented yet");
    // }
}
