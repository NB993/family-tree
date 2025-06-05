# FT-005: Family 홈 전용 구성원 목록+관계 조회 API 개발

## 문서 정보
- **Epic**: FT-003 Family 홈 구성원 목록 조회 기능
- **Story**: FT-005 Family 홈 전용 구성원 목록+관계 조회 API 개발
- **단계**: 1단계 코어 계층 (UseCase + Service + 도메인 객체)
- **작성일**: 2025-06-05
- **버전**: v1.0
- **작성자**: 기획자 AI

---

## 1. 개발 목표 및 배경

### 1.1 Story 목표
Family 홈 화면에서 필요한 **구성원 정보 + 관계 정보**를 효율적으로 조회할 수 있는 UseCase를 구현하여, 기존 개별 UseCase들을 조합하여 복합 데이터를 제공하는 것이 목표

### 1.2 기술적 배경 및 설계 고민

#### 🤔 **핵심 고민: 도메인 순수성 vs 실용성**

**고민 상황:**
```java
// Option 1: 응답 구조에 맞춘 도메인 (❌ 도메인 오염)
public class FamilyMemberWithRelationship {
    private FamilyMember member;
    private FamilyMemberRelationship relationship; // API 응답 구조에 종속
}

// Option 2: 순수 도메인 + 복잡한 변환 (😕 복잡성 증가)
public class FamilyMembersWithRelationships {
    private List<FamilyMember> members;
    private List<FamilyMemberRelationship> relationships;
    // → Controller에서 복잡한 변환 로직 필요
}

// Option 3: 새로운 복합 UseCase (😕 재사용성 저하)  
public interface FindFamilyHomeDataUseCase {
    FamilyHomeData find(FindFamilyHomeDataQuery query); // Family 홈 전용
}
```

#### 💡 **최종 선택된 해결방안: 기존 UseCase 조합 + 일급객체 변환**

```java
@RestController
public class FamilyHomeController {
    
    private final FindFamilyMemberUseCase findMemberUseCase;              // 재사용!
    private final FindFamilyMemberRelationshipUseCase findRelationshipUseCase; // 재사용!
    
    public ResponseEntity<List<FamilyMemberWithRelationshipResponse>> getFamilyHome() {
        // 1. 기존 UseCase들로 독립적 조회
        List<FamilyMember> members = findMemberUseCase.findAll(memberQuery);
        List<FamilyMemberRelationship> relationships = findRelationshipUseCase.findAll(relationQuery);
        
        // 2. 어댑터에서 일급객체로 조합 (도메인 지식 캡슐화)
        FamilyMembersWithRelationshipsResponse responseDTO = 
            new FamilyMembersWithRelationshipsResponse(members, relationships);
            
        // 3. 일급객체 메서드로 API별 맞춤 응답 생성
        List<FamilyMemberWithRelationshipResponse> response = 
            responseDTO.toMemberWithRelationships(currentUserId);
            
        return ResponseEntity.ok(response);
    }
}
```

#### ✅ **선택 이유 및 장점들:**

1. **UseCase 재사용성 극대화**
   - 기존 `FindFamilyMemberUseCase`, `FindFamilyMemberRelationshipUseCase` 그대로 활용
   - 새로운 복합 UseCase 만들 필요 없음
   - 각 UseCase는 단일 책임 원칙 유지

2. **도메인 순수성 보장** 
   - `FamilyMembersWithRelationships` 일급객체는 순수 도메인 지식만 포함
   - API 응답 구조를 전혀 모르는 상태 유지
   - 비즈니스 로직과 표현 로직 완전 분리

3. **API별 맞춤 응답 지원**
   ```java
   public class FamilyMembersWithRelationships {
       // Family 홈용 응답
       public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships(Long currentUserId) { ... }
       
       // 가족트리용 응답  
       public List<FamilyTreeNodeResponse> toTreeNodes() { ... }
       
       // 관계 요약용 응답
       public List<RelationshipSummaryResponse> toSummary() { ... }
   }
   ```

4. **캐싱 전략 최적화**
   ```java
   @Cacheable("familyMembers")
   public List<FamilyMember> findAll(...) { ... }

   @Cacheable("familyRelationships") 
   public List<FamilyMemberRelationship> findAll(...) { ... }
   ```

5. **테스트 용이성**
   - UseCase별 독립적인 단위 테스트 가능
   - 일급객체의 변환 로직만 별도 테스트
   - Mock 객체 의존성 최소화

#### 🎯 **핵심 설계 원칙**

**"기존 UseCase 재사용 + 일급객체 조합 + 어댑터 변환"**

- **도메인 계층**: 순수 비즈니스 로직만 (UI 구조 무관)
- **어댑터 계층**: 도메인 → API 응답 변환 책임
- **일급객체**: 도메인 지식 캡슐화 + 다양한 변환 메서드 제공

---

## 2. 구현 계획

### 2.1 구현할 컴포넌트

#### 2.1.1 응답 DTO 객체 (신규)
```java
/**
 * 가족 구성원과 관계 정보를 조합하여 다양한 API 응답을 생성하는 일급 객체
 */
public class FamilyMembersWithRelationshipsResponse {
    private final List<FamilyMember> members;
    private final List<FamilyMemberRelationship> relationships;
    
    public FamilyMembersWithRelationshipsResponse(List<FamilyMember> members, List<FamilyMemberRelationship> relationships) {
        this.members = members;
        this.relationships = relationships;
    }
    
    // Family 홈 API용 변환
    public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships(Long currentUserId);
    
    // 관계 조회 헬퍼 메서드들
    public Optional<FamilyMemberRelationship> findRelationship(Long fromId, Long toId);
    public List<FamilyMember> getActiveMembers();
    public Map<Long, FamilyMemberRelationship> getRelationshipsFrom(Long memberId);
}
```

### 2.2 기존 컴포넌트 활용

#### 2.2.1 재사용할 UseCase들 ✅
- **FindFamilyMemberUseCase**: 구성원 정보 조회 (기존 구현 완료)
- **FindFamilyMemberRelationshipUseCase**: 관계 정보 조회 (기존 구현 완료)

#### 2.2.2 재사용할 Query 객체들 ✅
- **FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery**: ACTIVE 구성원 조회용
- **FindAllFamilyMemberRelationshipsQuery**: 특정 구성원의 모든 관계 조회용

### 2.3 구현 순서

1. **1단계**: `FamilyMembersWithRelationships` 일급객체 구현
   - 생성자 및 기본 메서드
   - 관계 조회 헬퍼 메서드들
   - 단위 테스트 작성

2. **2단계**: Family 홈 전용 변환 메서드 구현  
   - `toMemberWithRelationshipList()` 메서드
   - 현재 사용자 기준 관계 정보 매핑
   - 변환 로직 테스트

3. **3단계**: 통합 테스트
   - 기존 UseCase들과의 연동 테스트
   - 성능 테스트 (N+1 문제 해결 확인)

---

## 3. 상세 설계

### 3.1 FamilyMembersWithRelationships 클래스 설계

#### 3.1.1 핵심 메서드 설계

```java
public class FamilyMembersWithRelationships {
    
    /**
     * 특정 구성원이 다른 구성원에 대해 정의한 관계를 조회합니다.
     *
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @param toMemberId   관계가 정의된 대상 구성원 ID
     * @return 관계 정보 (없으면 Optional.empty())
     */
    public Optional<FamilyMemberRelationship> findRelationship(Long fromMemberId, Long toMemberId) {
        return relationships.stream()
            .filter(rel -> rel.getFromMemberId().equals(fromMemberId) 
                        && rel.getToMemberId().equals(toMemberId))
            .findFirst();
    }
    
    /**
     * Family 홈 API 응답용 데이터 구조로 변환합니다.
     * 현재 사용자 기준으로 각 구성원과의 관계 정보를 매핑합니다.
     *
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return Family 홈 API 응답 리스트
     */
    public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships(Long currentUserId) {
        return members.stream()
            .filter(member -> member.getStatus() == FamilyMemberStatus.ACTIVE)
            .filter(member -> !member.getId().equals(currentUserId)) // 본인 제외
            .map(member -> {
                Optional<FamilyMemberRelationship> relationship = 
                    findRelationship(currentUserId, member.getId());
                return new FamilyMemberWithRelationshipResponse(member, relationship);
            })
            .sorted(Comparator.comparing(response -> response.getMember().getAge())) // 나이순 정렬
            .toList();
    }
}
```

#### 3.1.2 성능 최적화 고려사항

```java
public class FamilyMembersWithRelationships {
    
    // 관계 조회 성능 최적화를 위한 인덱스 맵 (지연 초기화)
    private Map<String, FamilyMemberRelationship> relationshipIndex;
    
    private void initializeRelationshipIndex() {
        if (relationshipIndex == null) {
            relationshipIndex = relationships.stream()
                .collect(Collectors.toMap(
                    rel -> rel.getFromMemberId() + ":" + rel.getToMemberId(),
                    Function.identity()
                ));
        }
    }
    
    public Optional<FamilyMemberRelationship> findRelationship(Long fromMemberId, Long toMemberId) {
        initializeRelationshipIndex();
        String key = fromMemberId + ":" + toMemberId;
        return Optional.ofNullable(relationshipIndex.get(key));
    }
}
```

### 3.2 기존 UseCase 활용 방안

#### 3.2.1 Controller에서의 UseCase 조합 패턴

```java
@RestController
@RequestMapping("/api/family")
public class FamilyHomeController {
    
    private final FindFamilyMemberUseCase findMemberUseCase;
    private final FindFamilyMemberRelationshipUseCase findRelationshipUseCase;
    
    @GetMapping("/home/members")
    public ResponseEntity<List<FamilyMemberWithRelationshipResponse>> findFamilyHomeMembers(
        @AuthFTUser FTUser ftUser
    ) {
        
        // FTUser에서 인증 정보 추출
        Long familyId = ftUser.getFamilyId();
        Long currentUserId = ftUser.getUserId();
        
        // 기존 UseCase로 독립적 데이터 조회
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery memberQuery = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);
        List<FamilyMember> members = findMemberUseCase.findAll(memberQuery);
        
        FindAllFamilyMemberRelationshipsQuery relationshipQuery = 
            new FindAllFamilyMemberRelationshipsQuery(familyId, currentUserId);
        List<FamilyMemberRelationship> relationships = findRelationshipUseCase.findAll(relationshipQuery);
        
        // 일급객체로 조합 및 변환
        FamilyMembersWithRelationshipsResponse responseDTO = 
            new FamilyMembersWithRelationshipsResponse(members, relationships);
        
        List<FamilyMemberWithRelationshipResponse> response = 
            responseDTO.toMemberWithRelationships(currentUserId);
        
        return ResponseEntity.ok(response);
    }
}
```

---

## 4. 테스트 전략

### 4.1 단위 테스트

#### 4.1.1 FamilyMembersWithRelationships 테스트
```java
@DisplayName("가족 구성원-관계 조합 객체 테스트")
class FamilyMembersWithRelationshipsTest {
    
    @Test
    @DisplayName("특정 구성원 간 관계 조회가 정상 동작한다")
    void should_find_relationship_between_members() {
        // given
        FamilyMember member1 = createFamilyMember(1L);
        FamilyMember member2 = createFamilyMember(2L);
        FamilyMemberRelationship relationship = createRelationship(1L, 2L, FATHER);
        
        FamilyMembersWithRelationships target = 
            new FamilyMembersWithRelationships(List.of(member1, member2), List.of(relationship));
        
        // when
        Optional<FamilyMemberRelationship> result = target.findRelationship(1L, 2L);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getRelationshipType()).isEqualTo(FATHER);
    }
    
    @Test
    @DisplayName("Family 홈 응답 변환이 올바르게 동작한다")
    void should_convert_to_family_home_response_correctly() {
        // given
        Long currentUserId = 1L;
        List<FamilyMember> members = createActiveMembers();
        List<FamilyMemberRelationship> relationships = createRelationships(currentUserId);
        
        FamilyMembersWithRelationships target = 
            new FamilyMembersWithRelationships(members, relationships);
        
        // when
        List<FamilyMemberWithRelationshipResponse> result = 
            target.toMemberWithRelationships(currentUserId);
        
        // then
        assertThat(result).hasSize(3); // 본인 제외
        assertThat(result).isSortedAccordingTo(
            Comparator.comparing(r -> r.getMember().getAge())); // 나이순 정렬 확인
    }
}
```

### 4.2 통합 테스트

#### 4.2.1 Controller 통합 테스트
```java
@DisplayName("Family 홈 API 통합 테스트")
class FamilyHomeControllerIntegrationTest extends ControllerTestBase {
    
    @Test
    @DisplayName("Family 홈 구성원 목록 조회가 정상 동작한다")
    void should_get_family_home_members_successfully() throws Exception {
        // given
        setupFamilyMembersAndRelationships();
        
        // when & then
        mockMvc.perform(get("/api/family/home/members")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].member.age", lessThanOrEqualTo("$[1].member.age"))) // 나이순 정렬
                .andExpect(jsonPath("$[0].relationship").exists());
    }
}
```

---

## 5. 성능 고려사항

### 5.1 데이터베이스 쿼리 최적화

#### 5.1.1 현재 쿼리 패턴 (효율적)
```sql
-- 1번 쿼리: 구성원 조회
SELECT * FROM family_member 
WHERE family_id = ? AND status = 'ACTIVE';

-- 2번 쿼리: 관계 조회  
SELECT * FROM family_member_relationship 
WHERE family_id = ? AND from_member_id = ?;
```

#### 5.1.2 N+1 문제 회피
- ✅ **구성원별 관계 개별 조회 방식 사용 안 함**
- ✅ **한 번에 모든 관계 조회 후 메모리에서 매핑**
- ✅ **인덱스 최적화**: (family_id, from_member_id) 복합 인덱스 활용

### 5.2 캐싱 전략

#### 5.2.1 UseCase 레벨 캐싱
```java
@Service
public class FindFamilyMemberService {
    
    @Cacheable(value = "familyMembers", key = "#query.familyId + ':' + #query.currentUserId")
    public List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query) {
        // 캐시 적용
    }
}

@Service  
public class FindFamilyMemberRelationshipService {
    
    @Cacheable(value = "familyRelationships", key = "#query.familyId + ':' + #query.fromMemberId")
    public List<FamilyMemberRelationship> findAll(FindAllFamilyMemberRelationshipsQuery query) {
        // 캐시 적용
    }
}
```

#### 5.2.2 캐시 무효화 전략
```java
@CacheEvict(value = {"familyMembers", "familyRelationships"}, key = "#familyId")
public void evictFamilyCache(Long familyId) {
    // 구성원 추가/변경/삭제 시 캐시 무효화
}
```

---

## 6. 알려진 이슈 및 제약사항

### 6.1 현재 제약사항
1. **메모리 사용량**: 대가족(50명+)의 경우 메모리 사용량 증가 가능성
2. **관계 복잡도**: 복잡한 관계망에서 성능 저하 가능성  
3. **캐시 정합성**: 실시간 관계 변경 시 캐시 동기화 이슈

### 6.2 향후 개선 계획
1. **페이징 지원**: 대가족 대응을 위한 구성원 목록 페이징
2. **관계 인덱싱**: 복잡한 관계 조회 성능 최적화
3. **이벤트 기반 캐시**: 도메인 이벤트 기반 캐시 무효화

---

## 7. 시니어 개발자 관점 추가 설명

### 7.1 아키텍처 관점에서의 의미

#### 7.1.1 헥사고날 아키텍처 원칙 준수
- **포트 재사용**: 기존 Port 인터페이스들을 조합하여 새로운 기능 구현
- **어댑터 책임 분리**: 도메인 → API 변환은 어댑터가 담당
- **도메인 순수성**: 비즈니스 로직과 표현 로직의 완전한 분리

#### 7.1.2 DDD 관점에서의 가치
- **일급 컬렉션**: `FamilyMembersWithRelationships`가 도메인 지식 캡슐화
- **애그리거트 조합**: 서로 다른 애그리거트(구성원, 관계)의 효율적 조합
- **유비쿼터스 언어**: 도메인 전문가와 개발자 간 공통 언어 유지

### 7.2 확장성 고려사항

#### 7.2.1 새로운 API 추가 시
```java
// 가족트리 API 추가 예시
public List<FamilyTreeNodeResponse> toTreeNodes() {
    // 동일한 도메인 데이터로 트리 구조 응답 생성
}

// 관계 통계 API 추가 예시  
public RelationshipStatisticsResponse toStatistics() {
    // 관계 통계 정보 생성
}
```

#### 7.2.2 성능 최적화 확장
```java
// 관계 그래프 캐싱
private transient Map<Long, Set<Long>> relationshipGraph;

// 경로 탐색 최적화
public List<FamilyMemberRelationship> findRelationshipPath(Long from, Long to) {
    // 관계 경로 탐색 (관계 유추 기능용)
}
```

### 7.3 유지보수성 강화 방안

#### 7.3.1 타입 안전성
```java
// 강타입 ID 객체 도입 고려
public record FamilyId(Long value) {}
public record MemberId(Long value) {}

// 컴파일 타임 안전성 보장
public Optional<FamilyMemberRelationship> findRelationship(MemberId from, MemberId to) { ... }
```

#### 7.3.2 불변성 보장
```java
public class FamilyMembersWithRelationships {
    private final List<FamilyMember> members;           // 방어적 복사
    private final List<FamilyMemberRelationship> relationships; // 방어적 복사
    
    // 수정 메서드 제공 안 함 → 불변 객체
}
```

---

## 8. 다음 단계

### 8.1 Story-005 완료 조건
- [x] 기존 UseCase 분석 및 재사용 계획 수립 ✅
- [ ] `FamilyMembersWithRelationships` 일급객체 구현
- [ ] Family 홈 전용 변환 메서드 구현  
- [ ] 단위 테스트 작성 및 통과
- [ ] 통합 테스트 작성 및 통과
- [ ] 성능 테스트 (N+1 문제 해결 확인)

### 8.2 Story-006 준비사항
- Family 홈 전용 Response DTO 설계
- 인프라 계층 구현 (Repository 확장)
- 프레젠테이션 계층 구현 (Controller)

---

## 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 작성자 |
|------|------|-----------|-----------|--------|
| v1.0.0 | 2025-06-05 | 초기 개발 문서 작성 - 설계 고민과 해결방안 중심 | UseCase 조합 방식 결정 및 아키텍처 설계 완료 | 기획자 AI |
```

---

**🎯 핵심 가치: "기존 컴포넌트 재사용 + 일급객체 조합 + 확장 가능한 변환"**

이 설계 방식은 향후 Family Tree의 다양한 API에서 재활용 가능한 강력한 패턴이 될 것입니다! 🚀
