# 명명 규칙 지침서

## ⚠️ 필수 명명 규칙 (개발 전 확인)

| 동작 유형 | 사용할 접두사 | 잘못된 예시 |
|---------|------------|------------|
| 조회 | **Find** | Get, Retrieve, Query |
| 등록 | **Save** | Create, Add, Insert |
| 수정 | **Modify** | Update, Change, Edit |
| 삭제 | **Delete** | Remove, Erase |

모든 인바운드 어댑터, 인바운드 포트, 아웃바운드 포트는 반드시 위 접두사를 사용해야 합니다.

## UseCase 및 Query 클래스 명명 규칙

### UseCase 인터페이스 명명 규칙

#### 1. UseCase 인터페이스명
- **단수형 사용**: `FindFamilyMemberUseCase` (O), `FindFamilyMembersUseCase` (X)
- **패턴**: `{동사}{도메인객체}UseCase`
- **예시**: 
  - `FindFamilyMemberUseCase`
  - `SaveFamilyMemberUseCase`
  - `ModifyFamilyMemberUseCase`

#### 2. UseCase 메서드명 (엄격 통일)
- **단건 조회**: `find()` - 모든 단건 조회에서 동일한 메서드명 사용
- **복수 조회**: `findAll()` - 모든 복수 조회에서 동일한 메서드명 사용
- **조회 의도 구분**: Query 객체 클래스명으로만 구분 (메서드명으로 구분 금지)

**올바른 예시**:
```java
public interface FindFamilyMemberUseCase {
    // 단건 조회 - 메서드명은 항상 find()
    FamilyMember find(FindFamilyMemberByIdQuery query);
    FamilyMember find(FindFamilyMemberByEmailQuery query);
    FamilyMember find(FindFamilyMemberByUserIdQuery query);
    
    // 복수 조회 - 메서드명은 항상 findAll()
    List<FamilyMember> findAll(FindAllFamilyMembersQuery query);
    List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdQuery query);
    List<FamilyMember> findAll(FindFamilyMembersByRoleQuery query);
}
```

**금지된 예시**:
```java
// ❌ 금지: 메서드명으로 조회 의도 구분
public interface FindFamilyMemberUseCase {
    FamilyMember findById(Long id);           // 금지
    FamilyMember findByEmail(String email);   // 금지
    List<FamilyMember> findMembers(...);      // 금지
    List<FamilyMember> findActiveMembers(...); // 금지
}
```

### Query 클래스 명명 규칙

#### 1. 단건 조회용 Query
- **패턴**: `{동사}{도메인객체}Query`
- **예시**: `FindFamilyMemberQuery`, `SaveFamilyMemberQuery`

#### 2. 복수 조회용 Query  
- **패턴**: `{동사}All{도메인객체복수형}Query`
- **예시**: `FindAllFamilyMembersQuery`, `FindAllFamilyMemberRelationshipsQuery`

#### 3. 특수 조건 Query
- **패턴**: `{동사}{조건}{도메인객체}Query`
- **예시**: `FindActiveFamilyMembersQuery`, `FindSuspendedFamilyMembersQuery`

### 실제 구현 예시

#### 기존 구현 (참고용)
```java
// UseCase 인터페이스
public interface FindFamilyMemberRelationshipUseCase {
    FamilyMemberRelationship find(FindFamilyMemberRelationshipQuery query);
    List<FamilyMemberRelationship> findAll(FindAllFamilyMemberRelationshipsQuery query);
}

// 단건 조회용 Query
public class FindFamilyMemberRelationshipQuery {
    private final Long familyId;
    private final Long fromMemberId;
    private final Long toMemberId;
}

// 복수 조회용 Query
public class FindAllFamilyMemberRelationshipsQuery {
    private final Long familyId;
    private final Long fromMemberId;
}
```

### 주의사항

1. **일관성 유지**: 모든 UseCase에서 동일한 패턴 적용
2. **단수형 원칙**: UseCase 인터페이스명은 항상 단수형
3. **메서드명 엄격 통일**: 단건 조회는 `find()`, 복수 조회는 `findAll()` 만 사용
4. **Query 객체로 의도 구분**: 메서드명이 아닌 Query 클래스명으로 조회 의도 표현
5. **조회 기준 명시**: Query 클래스명에 조회 기준이 명확히 드러나야 함
6. **Validation**: 각 Query 클래스에서 입력값 검증 필수

### 조회 기준별 Query 클래스명 가이드

#### 단일 필드 기준 조회
```java
// ID 기준
FindFamilyMemberByIdQuery
FindUserByIdQuery

// 이메일 기준  
FindUserByEmailQuery
FindFamilyMemberByEmailQuery

// 사용자 ID 기준
FindFamilyMemberByUserIdQuery

// 이름 기준
FindFamilyMemberByNameQuery
```

#### 복합 조건 조회
```java
// Family 내 특정 상태의 구성원들
FindActiveFamilyMembersByFamilyIdQuery
FindSuspendedFamilyMembersByFamilyIdQuery

// 역할별 조회
FindFamilyMembersByRoleQuery
FindAdminFamilyMembersQuery

// 날짜 범위 조회
FindFamilyMembersByBirthdayRangeQuery
FindFamilyMembersByJoinDateRangeQuery

// 복합 조건
FindActiveFamilyMembersByRoleAndFamilyIdQuery
```

#### 메서드 오버로딩을 활용한 일관성 확보
```java
// ✅ 올바른 방식: 메서드명 통일 + Query 객체로 의도 구분
public interface FindFamilyMemberUseCase {
    // 모든 단건 조회는 find() 메서드명 통일
    FamilyMember find(FindFamilyMemberByIdQuery query);
    FamilyMember find(FindFamilyMemberByEmailQuery query);
    FamilyMember find(FindFamilyMemberByUserIdQuery query);
    
    // 모든 복수 조회는 findAll() 메서드명 통일  
    List<FamilyMember> findAll(FindAllFamilyMembersQuery query);
    List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdQuery query);
    List<FamilyMember> findAll(FindFamilyMembersByRoleQuery query);
}

// ❌ 잘못된 방식: 메서드명으로 조회 의도 구분
public interface FindFamilyMemberUseCase {
    FamilyMember findById(Long id);                    // 금지
    FamilyMember findByEmail(String email);            // 금지
    List<FamilyMember> findActiveMembers(...);         // 금지
    List<FamilyMember> findMembersByRole(...);         // 금지
}
```

**장점**:
- 메서드명의 일관성 확보
- Query 객체만으로 조회 의도 파악 가능
- IDE 자동완성에서 find/findAll 두 개만 표시
- 코드 가독성 및 유지보수성 향상

### 기획 단계에서 확인해야 할 질문들

기획자 AI가 UseCase 설계 시 개발자에게 확인해야 할 필수 질문:

#### 단건 조회 UseCase
1. **조회 기준**: "어떤 필드를 기준으로 조회하시겠습니까? (ID/UserID/Email 등)"
2. **유니크성**: "해당 필드가 유니크한 값인가요?"
3. **실패 처리**: "조회 실패 시 어떻게 처리하시겠습니까? (예외/Optional)"
4. **권한 제한**: "조회 대상에 권한 제한이 있나요?"

#### 복수 조회 UseCase  
1. **필터링**: "어떤 조건으로 필터링하시겠습니까? (상태/역할/날짜범위)"
2. **정렬**: "정렬 기준은 무엇입니까? (생성일/이름/사용자정의)"
3. **권한**: "권한별 접근 제한이 있나요?"
4. **페이징**: "페이징이 필요합니까?"

### 네이밍 검증 체크리스트

#### Query 클래스명 검증
- [ ] 조회 기준이 클래스명에 명확히 표현되었는가?
- [ ] `By{필드명}` 형태로 조회 필드가 명시되었는가?
- [ ] 복수 조회의 경우 필터링 조건이 명시되었는가?
- [ ] 하나의 Query가 하나의 명확한 조회 책임만 가지는가?

#### UseCase 메서드명 검증  
- [ ] 단건 조회는 모두 `find()` 메서드명을 사용하는가?
- [ ] 복수 조회는 모두 `findAll()` 메서드명을 사용하는가?
- [ ] `findById()`, `findByEmail()`, `findMembers()` 등 금지된 메서드명을 사용하지 않았는가?
- [ ] 메서드 오버로딩으로 Query 타입별 구분이 가능한가?

## 계층별 명명 규칙

| 계층 | 패턴 | 올바른 예시 | 잘못된 예시 |
|-----|-----|---------|------------|
| 인바운드 포트 | **{Find/Save/Modify/Delete}{도메인}UseCase** | `FindFamilyUseCase`, `ModifyFamilyUseCase` | `UpdateFamilyUseCase`, `GetFamilyUseCase` |
| 아웃바운드 포트 | **{Find/Save/Modify/Delete}{도메인}Port** | `FindFamilyPort`, `ModifyFamilyPort` | `GetFamilyPort`, `UpdateFamilyPort` |
| 서비스 | **{도메인}Service** 또는 **{Find/Save/Modify/Delete}{도메인}Service** | `FamilyService`, `FindFamilyService` | `FamilyManager`, `UpdateFamilyService` |
| 인바운드 어댑터 | **{Find/Save/Modify/Delete}{도메인}Controller** | `FindFamilyController` | `GetFamilyController` |
| 아웃바운드 어댑터 | **{도메인}Adapter** | `FamilyAdapter` | `FamilyRepository` |
| JPA 엔티티 | **{도메인}JpaEntity** | `FamilyJpaEntity` | `FamilyEntity` |
| 요청 DTO | **{Find/Save/Modify/Delete}{도메인}Request** | `SaveFamilyRequest` | `CreateFamilyRequest` |
| 응답 DTO | **{Find/Save/Modify/Delete}{도메인}Response** | `FindFamilyResponse` | `FamilyDTO` |
| 커맨드/쿼리 객체 | **{Find/Save/Modify/Delete}{도메인}Command/Query** | `SaveFamilyCommand` | `CreateFamilyCommand` |

## 일반 명명 규칙

- 클래스: PascalCase (예: `FamilyMember`)
- 인터페이스: PascalCase (예: `FindFamilyUseCase`)
- 메서드, 변수: camelCase (예: `findById()`, `familyId`)
- 상수: UPPER_SNAKE_CASE (예: `MAX_FAMILY_MEMBERS`)
- 패키지: 소문자 (예: `io.jhchoe.familytree.core.family.domain`)

## 주석 및 문서화 스타일

### 어투 및 문장 표현 통일

- **기본 어투**: 모든 주석, JavaDoc, 테스트 설명은 다음 규칙을 준수
  - 서술문: "~합니다" 형식으로 끝냅니다
  - 의문문: "~합니까?" 형식으로 끝냅니다
  - 명령문: "~하세요" 형식으로 끝냅니다
  
- **JavaDoc 패턴**:
  - 클래스 설명: "{클래스명}은/는 {주요 기능}을 담당하는 {종류}입니다."
  - 메서드 설명: "{동작}을/를 {수행방식}으로 수행합니다."
  - 파라미터 설명: "{파라미터명}: {역할}" 형식으로 작성합니다
  - 반환값 설명: "조회된 {객체} 반환, 존재하지 않을 경우 {대체값} 반환" 형식으로 작성합니다
  
- **상속 구현 시**: 서비스 클래스가 인터페이스를 구현할 경우 `{@inheritDoc}` 주석을 사용합니다

### 예시
```java
/**
 * UserService는 사용자 관련 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
public class UserService implements FindUserUseCase {

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        // 구현...
    }
}

/**
 * 사용자 ID로 사용자를 조회합니다.
 *
 * @param id 조회할 사용자의 ID
 * @return 조회된 사용자, 존재하지 않을 경우 예외 발생
 * @throws UserNotFoundException 사용자를 찾을 수 없는 경우 발생
 */
User findById(Long id);
```

## 엔티티-도메인 변환 패턴

### 변환 책임
- 엔티티→도메인 변환: JpaEntity 클래스의 `toXxx()` 메서드가 담당합니다
- 도메인→엔티티 변환: JpaEntity 클래스의 정적 `from(xxx)` 메서드가 담당합니다

### 변환 메서드 시그니처
```java
// 도메인 → 엔티티 변환
public static XxxJpaEntity from(Xxx domainObject) {
    Objects.requireNonNull(domainObject, "domainObject must not be null");
    // 변환 로직
    return new XxxJpaEntity(...);
}

// 엔티티 → 도메인 변환
public Xxx toXxx() {
    // 변환 로직
    return Xxx.withId(...);
}
```

### 컬렉션 처리
- 컬렉션은 항상 방어적 복사를 수행합니다
- 빈 컬렉션은 `Collections.emptyList()`로 반환합니다 (null 반환 금지)

### 순환 참조 처리
- 양방향 관계에서는 한쪽에서만 변환을 수행합니다
- 필요한 경우 ID만 참조하는 가벼운 객체를 사용합니다

## 도메인 모델 정적 팩토리 메서드명 규칙

### 🎯 정적 팩토리 메서드 명명 규칙 (강제 준수)

#### 신규 생성: `newXxx`
- **목적**: 완전히 새로운 도메인 객체 생성 (ID 없음)
- **패턴**: `newXxx(필요한_파라미터들)`
- **특징**: ID가 null이거나 기본값인 새로운 객체 생성

```java
/**
 * 새로운 가족 구성원을 생성합니다.
 *
 * @param name 구성원 이름
 * @param role 구성원 역할
 * @param birthDate 생년월일
 * @return 새로 생성된 가족 구성원 (ID 없음)
 */
public static FamilyMember newMember(String name, FamilyRole role, LocalDate birthDate) {
    return new FamilyMember(null, name, role, birthDate);
}
```

#### 기존 데이터 복원: `withId`
- **목적**: JPA 엔티티에서 도메인 엔티티로 변환 시 사용
- **패턴**: `withId(id, 기타_파라미터들)`
- **특징**: 이미 존재하는 데이터를 도메인 객체로 복원

```java
/**
 * 기존 가족 구성원 데이터를 도메인 객체로 복원합니다.
 *
 * @param id 구성원 ID
 * @param name 구성원 이름
 * @param role 구성원 역할
 * @param birthDate 생년월일
 * @return 복원된 가족 구성원 (ID 포함)
 */
public static FamilyMember withId(Long id, String name, FamilyRole role, LocalDate birthDate) {
    return new FamilyMember(id, name, role, birthDate);
}
```

### 🚫 금지된 메서드명

#### 절대 사용 금지
```java
// ❌ 금지된 메서드명들
public static FamilyMember of(...) { }         // 모호함
public static FamilyMember create(...) { }     // 모호함
public static FamilyMember from(...) { }       // JPA 엔티티 전용
public static FamilyMember build(...) { }      // 빌더 패턴과 혼동
public static FamilyMember getInstance(...) { } // 싱글톤과 혼동
public static FamilyMember valueOf(...) { }    // 값 변환과 혼동
```

#### 혼동 방지를 위한 명확한 구분
- `from()`: JPA 엔티티에서만 사용 (도메인 → JPA 엔티티 변환)
- `newXxx()`: 도메인 객체에서만 사용 (신규 생성)
- `withId()`: 도메인 객체에서만 사용 (기존 데이터 복원)

### 📝 메서드 선택 가이드

#### 신규 생성이 필요한 경우 → `newXxx`
```java
// 사용자가 새로운 가족 구성원을 등록할 때
FamilyMember newMember = FamilyMember.newMember("홍길동", FamilyRole.FATHER, birthDate);
```

#### DB에서 조회한 데이터를 복원할 때 → `withId`
```java
// JPA 엔티티에서 도메인 객체로 변환할 때
public FamilyMember toMember() {
    return FamilyMember.withId(this.id, this.name, this.role, this.birthDate);
}
```

### ⚠️ 중요 주의사항

1. **ID 처리 규칙**
   - `newXxx`: ID는 null 또는 기본값
   - `withId`: ID는 반드시 유효한 값 (null 체크 필수)

2. **유효성 검사**: 두 메서드 모두 파라미터 유효성 검사 필수

3. **불변성 유지**: 정적 팩토리 메서드로 생성된 객체는 불변 객체여야 함

4. **문서화 필수**: 모든 정적 팩토리 메서드에 JavaDoc 작성

### 📋 코드 리뷰 체크리스트

#### 정적 팩토리 메서드 작성 시 확인사항
- [ ] 메서드명이 `newXxx` 또는 `withId` 규칙을 따르는가?
- [ ] 금지된 메서드명(`of`, `create`, `from` 등)을 사용하지 않았는가?
- [ ] ID 처리가 올바르게 되었는가? (newXxx는 null, withId는 유효값)
- [ ] 파라미터 유효성 검사가 포함되었는가?
- [ ] JavaDoc이 완전히 작성되었는가?
- [ ] 생성된 객체가 불변성을 유지하는가?
- [ ] 특수한 상태나 역할은 파라미터로 전달하고 있는가? (별도 메서드 지양)
