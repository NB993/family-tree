package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberWithTagsInfo;
import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersWithTagsQuery;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagMappingInfo.TagSimpleInfo;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 구성원 조회 서비스입니다.
 * 기존 구현된 FindFamilyMemberPort를 활용하여 구성원을 조회하고 정렬합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class FindFamilyMemberService implements FindFamilyMemberUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;
    private final FamilyValidationService familyValidationService;
    private final FindFamilyMemberTagPort findFamilyMemberTagPort;
    private final FindFamilyMemberTagMappingPort findFamilyMemberTagMappingPort;

    /**
     * FindFamilyMemberService 생성자입니다.
     *
     * @param findFamilyMemberPort           Family 구성원 조회 포트
     * @param familyValidationService        Family 검증 서비스
     * @param findFamilyMemberTagPort        태그 조회 포트
     * @param findFamilyMemberTagMappingPort 태그 매핑 조회 포트
     */
    public FindFamilyMemberService(
        FindFamilyMemberPort findFamilyMemberPort,
        FamilyValidationService familyValidationService,
        FindFamilyMemberTagPort findFamilyMemberTagPort,
        FindFamilyMemberTagMappingPort findFamilyMemberTagMappingPort
    ) {
        this.findFamilyMemberPort = findFamilyMemberPort;
        this.familyValidationService = familyValidationService;
        this.findFamilyMemberTagPort = findFamilyMemberTagPort;
        this.findFamilyMemberTagMappingPort = findFamilyMemberTagMappingPort;
    }

    /**
     * 특정 Family 구성원을 조회합니다.
     * 권한에 따른 필터링이 적용됩니다.
     *
     * @param query 단건 조회 조건을 담은 쿼리 객체
     * @return 조회된 Family 구성원
     * @throws NullPointerException query가 null인 경우 (개발자 실수)
     * @throws FTException 권한이 없거나 구성원을 찾을 수 없는 경우
     */
    @Override
    public FamilyMember find(FindFamilyMemberByIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. Family 존재 여부 검증
        familyValidationService.validateFamilyExists(query.getFamilyId());
        
        // 2. 구성원 조회하면서 동시에 현재 사용자 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 3. 대상 구성원 조회
        FamilyMember targetMember = findFamilyMemberPort.findById(query.getTargetMemberId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));

        // 4. 권한 확인
        if (!shouldIncludeMember(targetMember, currentMember)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }

        return targetMember;
    }

    /**
     * Family 구성원 목록을 조회합니다.
     * 기존 FindFamilyMemberPort의 findAllByFamilyId()를 활용하여 조회합니다.
     *
     * @param query 복수 조회 조건을 담은 쿼리 객체
     * @return 나이순으로 정렬된 Family 구성원 목록
     * @throws NullPointerException query가 null인 경우 (개발자 실수)
     * @throws FTException Family가 존재하지 않거나 사용자가 Family 구성원이 아닌 경우
     */
    @Override
    public List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. Family 존재 여부 검증
        familyValidationService.validateFamilyExists(query.getFamilyId());
        
        // 2. 구성원 조회하면서 동시에 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 3. 전체 구성원 조회 (기존 구현 활용)
        List<FamilyMember> allMembers = findFamilyMemberPort.findAllByFamilyId(query.getFamilyId());

        // 4. 권한별 필터링 및 정렬
        return filterAndSortMembers(allMembers, currentMember);
    }

    /**
     * 권한별 필터링 및 나이순 정렬을 수행합니다.
     *
     * @param allMembers 전체 구성원 목록
     * @param currentMember 현재 사용자 정보
     * @return 필터링 및 정렬된 구성원 목록
     */
    private List<FamilyMember> filterAndSortMembers(List<FamilyMember> allMembers, FamilyMember currentMember) {
        return allMembers.stream()
            .filter(member -> shouldIncludeMember(member, currentMember))
            .sorted(createAgeComparator())
            .collect(Collectors.toList());
    }

    /**
     * 구성원을 결과에 포함할지 결정합니다.
     * ADMIN 이상은 SUSPENDED 포함 모든 구성원, 일반 사용자는 ACTIVE만 조회 가능합니다.
     *
     * @param member 확인할 구성원
     * @param currentMember 현재 사용자
     * @return 포함 여부
     */
    private boolean shouldIncludeMember(FamilyMember member, FamilyMember currentMember) {
        // ADMIN 이상은 SUSPENDED도 볼 수 있음
        if (isAdminOrAbove(currentMember.getRole())) {
            return true;
        }
        
        // 일반 사용자는 ACTIVE 상태만 볼 수 있음
        return member.getStatus() == FamilyMemberStatus.ACTIVE;
    }

    /**
     * ADMIN 이상 권한인지 확인합니다.
     *
     * @param role 확인할 권한
     * @return ADMIN 이상 권한 여부
     */
    private boolean isAdminOrAbove(FamilyMemberRole role) {
        return role == FamilyMemberRole.ADMIN || role == FamilyMemberRole.OWNER;
    }

    /**
     * 나이순(어린 순서) 정렬을 위한 Comparator를 생성합니다.
     * 생일 정보가 없는 구성원은 맨 뒤에 배치됩니다.
     *
     * @return 나이순 정렬 Comparator
     */
    private Comparator<FamilyMember> createAgeComparator() {
        return (member1, member2) -> {
            LocalDateTime birthday1 = member1.getBirthday();
            LocalDateTime birthday2 = member2.getBirthday();

            // 생일 정보가 없는 경우 맨 뒤로
            if (birthday1 == null && birthday2 == null) {
                return 0;
            }
            if (birthday1 == null) {
                return 1; // member1을 뒤로
            }
            if (birthday2 == null) {
                return -1; // member2를 뒤로
            }

            // 생일이 늦을수록 어리므로 앞에 배치 (내림차순)
            return birthday2.compareTo(birthday1);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FamilyMemberWithTagsInfo> findAll(FindFamilyMembersWithTagsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. Family 존재 여부 검증
        familyValidationService.validateFamilyExists(query.familyId());

        // 2. 구성원 조회하면서 동시에 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(query.familyId(), query.currentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 3. 전체 구성원 조회 및 필터링/정렬
        List<FamilyMember> allMembers = findFamilyMemberPort.findAllByFamilyId(query.familyId());
        List<FamilyMember> filteredMembers = filterAndSortMembers(allMembers, currentMember);

        // 4. Family의 모든 태그 조회
        List<FamilyMemberTag> allTags = findFamilyMemberTagPort.findAllByFamilyId(query.familyId());
        Map<Long, FamilyMemberTag> tagMap = allTags.stream()
            .collect(Collectors.toMap(FamilyMemberTag::getId, Function.identity()));

        // 5. 배치 조회로 N+1 문제 해결
        List<Long> memberIds = filteredMembers.stream()
            .map(FamilyMember::getId)
            .toList();

        List<FamilyMemberTagMapping> allMappings = findFamilyMemberTagMappingPort.findAllByMemberIds(memberIds);

        // 매핑을 memberId별로 그룹화
        Map<Long, List<FamilyMemberTagMapping>> mappingsByMemberId = allMappings.stream()
            .collect(Collectors.groupingBy(FamilyMemberTagMapping::getMemberId));

        // orphan 태그 확인 및 로깅
        Set<Long> existingTagIds = tagMap.keySet();
        allMappings.stream()
            .filter(mapping -> !existingTagIds.contains(mapping.getTagId()))
            .forEach(mapping -> log.warn(
                "Orphan tag mapping detected: memberId={}, tagId={}",
                mapping.getMemberId(), mapping.getTagId()
            ));

        // 6. 각 멤버에 태그 정보 매핑
        return filteredMembers.stream()
            .map(member -> {
                List<FamilyMemberTagMapping> memberMappings =
                    mappingsByMemberId.getOrDefault(member.getId(), List.of());

                List<TagSimpleInfo> tags = memberMappings.stream()
                    .map(mapping -> tagMap.get(mapping.getTagId()))
                    .filter(tag -> tag != null)
                    .map(tag -> new TagSimpleInfo(tag.getId(), tag.getName(), tag.getColor()))
                    .toList();

                return new FamilyMemberWithTagsInfo(member, tags);
            })
            .toList();
    }
}
