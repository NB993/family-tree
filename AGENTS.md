# Family Tree Project - Claude Code Configuration

## 🚨 세션 시작 시 필수 실행

다음 명령어를 순서대로 실행하여 모든 지침을 읽어주세요:

```bash
# 필수 문서 읽기 (6개)
cat be/instructions/index.md
cat be/instructions/naming-conventions.md  
cat be/instructions/architecture-overview.md
cat be/instructions/coding-standards.md
cat be/instructions/testing-guidelines.md
cat be/instructions/commit-guidelines.md

# 선택적 문서
cat be/instructions/ai-collaboration-guidelines.md  # Git 작업 시 참고
```

- git reset --hard 절대 금지
- be/instructions/ 수정 시 승인 필요

## important-instruction-reminders

Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.


## 각 지침 문서 내용

1. be/instructions/index.md
# 백엔드 개발 지침 목차

이 문서는 백엔드 개발 지침서의 목차입니다. AI는 개발을 시작하기 전, 반드시 각 문서의 내용을 모두 읽고 개발을 시작하세요.

## 개발 지침 문서 목록

### 필수 문서 (6개)

1. [명명 규칙 지침서](naming-conventions.md)
   - 필수 명명 규칙 (Find/Save/Modify/Delete)
   - 계층별 명명 규칙
   - 일반 명명 규칙
   - 정적 팩토리 메서드 규칙 (newXxx, withId)

2. [아키텍처 개요](architecture-overview.md)
   - 헥사고날 아키텍처 설계 원칙
   - 기술 스택 및 버전
   - 핵심 계층 구조 및 예시 코드
   - JPA 엔티티 설계 및 성능 최적화
   - 개발 순서 (코어 → 인프라 → 프레젠테이션)

3. [코드 작성 스타일 가이드라인](coding-standards.md)
   - 기본 원칙 및 코드 스타일
   - 예외 처리 규칙
   - 컬렉션 처리 및 관계 매핑
   - JPA 엔티티 작성 규칙
   - null 체크 규칙

4. [테스트 코드 작성 가이드라인](testing-guidelines.md)
   - 테스트 분류 (단위, 인수, API 문서)
   - 테스트 작성 시점 및 방법
   - 테스트 시 엔티티 생성 규칙
   - 테스트 실패 디버깅
   - 테스트 실행 방법

5. [커밋 가이드라인](commit-guidelines.md)
   - 커밋 메시지 작성 규칙
   - 커밋 작업 절차

6. [AI 협업 가이드라인](ai-collaboration-guidelines.md) *(선택적)*
   - 코드 품질 기준
   - Git 작업 주의사항
   - 예외 상황 처리

## 개발 프로세스 요약

백엔드 개발 프로세스는 다음과 같은 순서로 진행됩니다:

1. **코어 계층 (application)**
   - Domain → UseCase → Service → Command/Query
   - 단위 테스트 작성

2. **인프라 계층 (adapter/out)**
   - JpaEntity → Adapter → Repository
   - 어댑터 테스트 작성

3. **프레젠테이션 계층 (adapter/in)**
   - Controller → Request/Response DTO
   - 인수 테스트 작성

## 주요 명명 규칙 요약

| 동작 유형 | 사용할 접두사 | 잘못된 예시 |
|---------|------------|------------|
| 조회 | **Find** | Get, Retrieve, Query |
| 등록 | **Save** | Create, Add, Insert |
| 수정 | **Modify** | Update, Change, Edit |
| 삭제 | **Delete** | Remove, Erase |

모든 인바운드 어댑터, 인바운드 포트, 아웃바운드 포트는 반드시 위 접두사를 사용해야 합니다.





2. be/instructions/naming-conventions.md

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





3. be/instructions/architecture-overview.md
# 아키텍처 개요

## 아키텍처 설계 원칙

본 프로젝트는 헥사고날 아키텍처(Hexagonal Architecture) 기반의 클린 코드 구조를 따르며, Spring Boot 프레임워크를 활용합니다. 이 구조는 도메인 로직을 핵심으로 두고, 외부 시스템과의 상호 작용을 포트와 어댑터를 통해 분리하여 관리합니다.

## 기술 스택 및 버전
- Java: Java 21 (LTS)
- Spring Boot: 3.4.2
- Spring Data JPA: Spring Boot 의존성 버전
- Spring Security: Spring Boot 의존성 버전
- 데이터베이스: H2(개발), MySQL 8.0(운영)
- Lombok: 1.18.30
- JUnit 5: Spring Boot 의존성 버전
- AssertJ: 3.24.2
- Rest Assured: 5.5.1
- Spring REST Docs: 3.0.3
- Mockito: 5.10.0
- TestContainers: 1.19.4

## 핵심 계층 구조

### 도메인 모델 계층
- **위치**: `core/{도메인명}/domain/`
- **역할**: 도메인의 핵심 개념, 규칙, 상태를 표현
- **지침**:
    - 모든 도메인 객체는 불변성 유지
    - 비즈니스 로직은 도메인 객체 내부에 캡슐화
    - 생성자는 private, 정적 팩토리 메서드 활용
    - 모든 필드는 final로 선언

### 인바운드 포트 계층
- **위치**: `core/{도메인명}/application/port/in/`
- **역할**: 애플리케이션이 외부에 제공하는 기능 인터페이스 정의
- **지침**:
    - 각 유스케이스는 단일 메서드를 가진 인터페이스로 정의합니다
    - 입력 파라미터는 Command 또는 Query 객체로 캡슐화합니다
    - Command 객체는 생성자에서 유효성 검증을 수행합니다
    - 반환 타입은 조회의 경우 도메인 객체를, 생성 또는 수정의 경우 ID(식별자)를 사용합니다
    - 유스케이스 메서드는 Optional 타입을 리턴하지 않습니다. Optional 데이터에 대한 분기 처리는 구현체 Service 클래스의 메서드 내부에서 처리합니다

**예시: FindFamilyUseCase 인터페이스**

```java
package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.Family;

/**
 * Family 조회를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface FindFamilyUseCase {

    /**
     * 지정된 ID의 Family를 조회합니다.
     *
     * @param query Family 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 Family 객체, 존재하지 않을 경우 예외 발생
     * @throws FTException 해당 ID의 Family가 존재하지 않을 경우
     */
    Family findById(FindFamilyQuery query);
}
```

### 아웃바운드 포트 계층
- **위치**: `core/{도메인명}/application/port/out/`
- **역할**: 외부 시스템과의 상호작용을 위한 인터페이스 정의
- **지침**:
    - 인터페이스는 최소한의 기능만 정의
    - 도메인 객체 타입을 파라미터와 반환 타입으로 사용

### 애플리케이션 서비스 계층
- **위치**: `core/{도메인명}/application/service/`
- **역할**: 유스케이스 구현 및 비즈니스 프로세스 조정
- **지침**:
    - 각 서비스는 단일 유스케이스 인터페이스를 구현합니다
    - 도메인 로직은 도메인 객체에 위임합니다
    - 예외는 유스케이스에서 발생시키며 어댑터에서 예외를 발생시키지 않습니다
    - 외부 시스템 상호작용은 아웃바운드 포트를 통해 수행합니다
    - `@Transactional` 어노테이션을 클래스 또는 메서드 레벨에 적용합니다
    - 서비스가 구현한 메서드는 UseCase 인터페이스의 주석을 그대로 이용할 수 있도록 `{@inheritDoc}` 주석을 추가합니다

**예시: FindFamilyService 클래스**

```java
package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 조회 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyService implements FindFamilyUseCase {

    private final FindFamilyPort findFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Family findById(FindFamilyQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.find(query.getId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
    }
}
```

### 인바운드 어댑터 계층
- **위치**: `core/{도메인명}/adapter/in/`
- **역할**: 외부 요청을 애플리케이션 코어와 연결
- **지침**:
    - 모든 API 엔드포인트는 `/api/`로 시작합니다
    - 컨트롤러 메서드는 요청 객체를 커맨드/쿼리 객체로 변환하는 역할만 담당합니다
    - 인바운드 포트(유스케이스)를 호출하여 비즈니스 로직을 실행합니다
    - 컨트롤러는 비즈니스 로직 처리나 예외 발생을 담당하지 않습니다 - 이는 서비스 계층의 책임입니다
    - 인바운트 어댑터의 역할은 Request DTO를 Command/Query 객체로 변환하고, 응답을 Response DTO로 변환하는 것입니다
    - 유스케이스에서 응답받은 도메인 객체는 Response DTO로 변환만 합니다
    - 응답은 `ResponseEntity<T>` 를 사용합니다
    - `@RestController`, `@RequestMapping` 어노테이션을 사용합니다
    - 입력 유효성 검증은 `@Valid` 어노테이션으로 수행합니다
    - 하나의 Controller에는 반드시 하나의 API만 작성합니다

**예시: FindFamilyController 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.domain.Family;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 조회를 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class FindFamilyController {

    private final FindFamilyUseCase findFamilyUseCase;

    /**
     * ID로 Family를 조회합니다.
     *
     * @param id 조회할 Family의 ID
     * @return 조회된 Family 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<FindFamilyResponse> findById(@PathVariable Long id) {
        // 1. 쿼리 객체 생성
        FindFamilyQuery query = new FindFamilyQuery(id);
        
        // 2. 유스케이스 실행
        Family family = findFamilyUseCase.findById(query);
        
        // 3. 응답 변환 및 반환
        FindFamilyResponse response = new FindFamilyResponse(
            family.getId(),
            family.getName(),
            family.getDescription(),
            family.getProfileUrl(),
            family.getCreatedAt()
        );
        
        return ResponseEntity.ok(response);
    }
}
```

### 아웃바운드 어댑터 계층
- **위치**: `core/{도메인명}/adapter/out/`
- **역할**: 외부 시스템과의 통신 구현
- **지침**:
    - 아웃바운드 포트 인터페이스를 구현합니다
    - 코어에서는 도메인 객체를 전달받아 아웃바운드 어댑터에서 엔티티로 변환한 후 작업합니다
    - 아웃바운드 어댑터에서 조회한 엔티티는 도메인 객체로 변환하여 응답합니다

#### JPA 엔티티 설계 원칙
- JPA 엔티티는 `{도메인}JpaEntity` 형식으로 명명합니다
- 모든 JPA 엔티티는 기본 생성자를 `protected` 접근 제한자로 선언합니다
- 엔티티 필드는 직접 접근하지 않고 Getter를 통해 접근합니다
- `@Table`, `@Column` 등의 어노테이션을 명시하여 데이터베이스 스키마와 매핑합니다
- JPA 리포지토리는 Spring Data JPA 인터페이스로 정의합니다

#### 엔티티-도메인 변환 패턴
- 엔티티→도메인 변환: JpaEntity의 `toXxx()` 메서드
- 도메인→엔티티 변환: JpaEntity의 정적 `from()` 메서드

**예시: FamilyAdapter 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Family 관련 아웃바운드 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyAdapter implements FindFamilyPort {

    private final FamilyJpaRepository familyJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Family> find(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        
        return familyJpaRepository.findById(id)
            .map(FamilyJpaEntity::toFamily);
    }
}
```

#### JPA 엔티티 작성 예시

```java
package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.Family;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Family 엔티티를 DB에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "family")
public class FamilyJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "profile_url")
    private String profileUrl;

    /**
     * FamilyJpaEntity 객체를 생성하는 생성자입니다.
     */
    public FamilyJpaEntity(
        Long id,
        String name,
        String description,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
    }

    /**
     * Family 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param family 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyJpaEntity from(Family family) {
        Objects.requireNonNull(family, "family must not be null");
        
        return new FamilyJpaEntity(
            family.getId(),
            family.getName(),
            family.getDescription(),
            family.getProfileUrl(),
            family.getCreatedBy(),
            family.getCreatedAt(),
            family.getModifiedBy(),
            family.getModifiedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public Family toFamily() {
        return Family.withId(
            id,
            name,
            description,
            profileUrl,
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
```

#### JPA Repository 작성 예시

```java
package io.jhchoe.familytree.core.family.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Family JPA 엔티티에 대한 리포지토리 인터페이스입니다.
 */
public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {
}
```

#### 영속성 관련 성능 최적화

##### N+1 문제 방지
- 연관 엔티티를 조회할 때 N+1 문제를 방지하기 위해 페치 조인을 사용합니다
- 필요한 경우 `@EntityGraph`를 활용합니다
- `@OneToMany`, `@ManyToMany` 관계에서는 지연 로딩(`fetch = FetchType.LAZY`)을 기본으로 합니다

```java
@Query("SELECT f FROM family f JOIN FETCH f.members WHERE f.id = :id")
Optional<FamilyJpaEntity> findByIdWithMembers(@Param("id") Long id);

@EntityGraph(attributePaths = {"members"})
Optional<FamilyJpaEntity> findById(Long id);
```

##### 대량 데이터 처리
- 대량의 데이터를 처리할 때는 벌크 연산을 활용합니다
- 페이징 처리 시 카운트 쿼리를 최적화합니다

```java
@Modifying
@Query("UPDATE family f SET f.name = :name WHERE f.id = :id")
int updateName(@Param("id") Long id, @Param("name") String name);

@Query(value = "SELECT f FROM family f",
       countQuery = "SELECT COUNT(f.id) FROM family f")
Page<FamilyJpaEntity> findAllWithOptimizedCount(Pageable pageable);
```

##### 트랜잭션 관리
- 모든 변경 작업은 트랜잭션 내에서 수행합니다
- 조회 작업은 `@Transactional(readOnly = true)`를 사용하여 성능을 최적화합니다
- 트랜잭션 경계는 서비스 계층에서 관리합니다

## 개발 순서

프로젝트 개발은 다음 순서로 진행합니다:

1. **코어 계층 (application)**: Domain → UseCase → Service → Command/Query
2. **인프라 계층 (adapter/out)**: JpaEntity → Adapter → Repository
3. **프레젠테이션 계층 (adapter/in)**: Controller → Request/Response DTO

각 계층 개발 완료 후 테스트 작성 및 검증을 수행합니다.





4. be/instructions/coding-standards.md

# 코드 작성 스타일 가이드라인

## 기본 원칙

- Java 21 이상의 기능을 활용한다
- 불변 객체와 OOP 스타일을 권장합니다. 필요 시 함수형 프로그래밍 스타일을 혼용 가능
- 메서드 매개변수는 `final` 키워드를 사용한다
- 모든 클래스와 메서드에 JavaDoc을 작성한다
- 인터페이스를 구현하는 쪽에서는 `{@inheritDoc}`을 사용한다
- 모든 코드는 가능한 한 간결하고 명확하게 작성한다

## 코드 구조 및 스타일

### 코드 레이아웃

- 들여쓰기는 4칸 공백을 기준으로 한다
- 메서드 시그니처에 인자가 3개 이상 선언된 경우 첫 번째 인자부터 모든 인자를 줄바꿈하여 선언한다
- 메서드가 길어질 경우 단일 책임 원칙에 따라 더 작은 메서드로 분리한다
- 한 줄에 120자를 넘지 않도록 합니다

### 명명 컨벤션

- 가능한 한 접근제어자의 범위를 좁게 선언합니다 (public보다는 private, package-private 등)
- 변수, 메서드 이름은 그 목적이 명확하게 드러나도록 작명합니다
- 약어는 가급적 사용하지 않으며, 부득이한 경우 널리 알려진 것만 사용합니다
- boolean 변수나 메서드는 `is`, `has`, `can` 등의 접두사를 사용합니다
- 상수는 대문자 스네이크 케이스(UPPER_SNAKE_CASE)로 명명합니다

### 클래스 구조

- 필드 선언 → 생성자 → 정적 메서드 → 인스턴스 메서드 순서로 작성합니다
- 생성자는 private으로 선언하고, 정적 팩토리 메서드를 통해 객체를 생성합니다
- 모든 필드는 final로 선언하여 불변성을 유지합니다
- 상속보다는 컴포지션을 활용합니다

### 코드 스타일

- Lombok은 `@Getter`, `@RequiredArgsConstructor`만 사용하고, `@Builder` 패턴은 절대 사용하지 않습니다.
- API Request/Response DTO는 record 타입으로 작성합니다
- 조건문은 기본적으로 긍정문으로 작성합니다 (부정문은 가독성을 떨어뜨립니다)
- 메서드 내에서 return 분기 처리가 있는 경우 기본적으로 빠른 return 문을 사용합니다
- 상수는 항상 static final로 선언하고, 가능하면 클래스 상단에 배치합니다

## 예외 처리

### 기본 원칙

- 예외는 명확하고 의미있는 메시지와 함께 발생시킵니다
- 예외를 무시하지 않습니다. 반드시 처리하거나 명시적으로 상위로 전파합니다
- Try-catch 블록 내에서는 최소한의 코드만 포함합니다
- 체크 예외(Checked Exception)보다는 언체크 예외(Unchecked Exception)를 선호합니다

### 예외 처리 위치

- 예외는 유스케이스에서 발생시키며 어댑터에서 예외를 발생시키지 않습니다
- 도메인 엔티티의 유효성 검증은 생성자 또는 팩토리 메서드에서 수행합니다
- Command 객체는 생성자에서 유효성 검증을 수행합니다
- 예외 처리 시에는 FTException을 사용하고, 적절한 예외 코드를 지정합니다

### 코어 계층 null 체크 규칙

**중요**: 코어 계층(애플리케이션 서비스)에서 Command/Query 객체가 null인 경우 NPE를 발생시켜야 합니다.

- **목적**: 개발자 실수를 조기에 발견하여 개발 단계에서 수정하도록 유도
- **방법**: `Objects.requireNonNull(query, "query must not be null")` 사용
- **이유**: Command/Query 객체는 생성자에서 이미 사용자 입력 검증을 완료했으므로, 코어 계층까지 null이 넘어오는 것은 개발자의 실수임

```java
// ✅ 올바른 예시: 코어 계층에서 NPE 발생
@Override
@Transactional(readOnly = true)
public FamilyMember find(FindFamilyMemberByIdQuery query) {
    Objects.requireNonNull(query, "query must not be null"); // NPE 발생 → 500 에러
    
    // 비즈니스 로직...
    return member;
}

// ❌ 잘못된 예시: IllegalArgumentException 사용하지 말 것
@Override
public FamilyMember find(FindFamilyMemberByIdQuery query) {
    if (query == null) {
        throw new IllegalArgumentException("query must not be null"); // 400 에러로 오해 가능
    }
    // ...
}
```

**계층별 책임 분리**:
- **Query/Command 생성자**: 사용자 입력 검증 → `IllegalArgumentException` (400 에러)
- **코어 계층**: 개발자 실수 검증 → `NullPointerException` (500 에러)

```java
// Query 객체 생성자에서는 IllegalArgumentException
public FindFamilyMemberByIdQuery(Long familyId, Long currentUserId, Long targetMemberId) {
    if (familyId == null || familyId <= 0) {
        throw new IllegalArgumentException("유효한 가족 ID가 필요합니다."); // 400 에러
    }
    // ...
}

// 코어 계층에서는 Objects.requireNonNull
@Override
public FamilyMember find(FindFamilyMemberByIdQuery query) {
    Objects.requireNonNull(query, "query must not be null"); // 500 에러
    // ...
}
```

**에러 메시지 정확성**:
- Family 존재 여부를 먼저 검증하여 명확한 에러 메시지 제공
- "Family가 존재하지 않습니다" vs "해당 Family의 구성원이 아닙니다" 구분

```java
// 1. Family 존재 여부 검증 → FAMILY_NOT_FOUND
familyValidationService.validateFamilyExists(query.getFamilyId());

// 2. 구성원 권한 검증 → NOT_FAMILY_MEMBER  
FamilyMember currentMember = findFamilyMemberPort
    .findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId())
    .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));
```

```java
// 도메인 객체 검증 예시
private Family(
    Long id,
    String name,
    String description,
    String profileUrl,
    Long createdBy,
    LocalDateTime createdAt,
    Long modifiedBy,
    LocalDateTime modifiedAt
) {
    Objects.requireNonNull(name, "name must not be null");
    if (name.isBlank()) {
        throw new IllegalArgumentException("name must not be blank");
    }
    if (name.length() > 50) {
        throw new IllegalArgumentException("name length must be less than or equal to 50");
    }
    
    this.id = id;
    this.name = name;
    this.description = description;
    this.profileUrl = profileUrl;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.modifiedBy = modifiedBy;
    this.modifiedAt = modifiedAt;
}
```

## 컬렉션 처리

- 빈 컬렉션은 null 대신 빈 컬렉션(`Collections.emptyList()`, `Collections.emptySet()` 등)을 반환합니다
- 컬렉션은 방어적 복사를 통해 불변성을 유지합니다
- Java 8+ Stream API를 적극 활용합니다
- 컬렉션 반복 처리 시 forEach보다 stream의 map, filter 등을 활용합니다

```java
// 방어적 복사 예시
public List<Member> getMembers() {
    return Collections.unmodifiableList(new ArrayList<>(this.members));
}

// Stream API 활용 예시
public List<Member> getActiveMembers() {
    return this.members.stream()
        .filter(member -> member.getStatus() == MemberStatus.ACTIVE)
        .collect(Collectors.toList());
}
```

### 일대다, 다대다 관계 매핑
- 양방향 관계에서는 일관성을 유지하기 위한 헬퍼 메서드를 제공합니다
- `@OneToMany`, `@ManyToMany` 관계에서는 지연 로딩(`fetch = FetchType.LAZY`)을 기본으로 합니다

```java
@OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<FamilyMemberJpaEntity> members = new ArrayList<>();

// 양방향 관계 헬퍼 메서드
public void addMember(FamilyMemberJpaEntity member) {
    this.members.add(member);
    member.setFamily(this);
}

public void removeMember(FamilyMemberJpaEntity member) {
    this.members.remove(member);
    member.setFamily(null);
}
```

## 비동기 처리

- CompletableFuture, Reactor, RxJava 등을 사용할 때는 적절한 스케줄러와 예외 처리를 항상 포함합니다
- 블로킹 작업은 별도의 스레드풀에서 실행합니다
- 비동기 작업의 결과를 동기적으로 기다려야 할 경우 타임아웃을 설정합니다

## JPA 엔티티 작성 규칙

### 도메인 JPA 엔티티 규칙

- **setter 사용 금지**: 도메인 JPA 엔티티에서는 setter 메서드를 제거합니다
- **생성자 패턴**: 기본 생성자로 생성한 뒤 setter로 데이터를 주입하는 방식을 금지합니다
- **JPA 기본 생성자**: JPA가 필요로 하는 기본 생성자는 protected로 선언합니다
- **private 생성자 필수**: JpaEntity는 기본 생성자 이외의 모든 생성자는 private으로 선언하여 외부에서 직접 생성을 차단합니다
- **팩토리 메서드 활용**: 도메인 엔티티를 `from()` 정적 팩토리 메서드로 받아서 JPA 엔티티를 생성합니다

### DB 독립적인 코드 작성

- **JPA 표준 어노테이션 사용**: 특정 DB에 종속적인 기능보다 JPA 표준 어노테이션을 우선 사용합니다
- **네이티브 쿼리 최소화**: 가능한 JPQL이나 Criteria API를 사용하고, 네이티브 쿼리는 불가피한 경우에만 사용합니다
- **DB 특화 타입 자제**: PostgreSQL의 `jsonb`, `array` 타입 등 특정 DB만의 타입 사용을 자제합니다

```java
// ❌ 금지: setter 사용
FamilyJpaEntity entity = new FamilyJpaEntity();
entity.setName("패밀리명");
entity.setDescription("설명");

// ✅ 권장: from() 정적 메서드 사용
Family domain = Family.create("패밀리명", "설명", "프로필URL", 1L);
FamilyJpaEntity entity = FamilyJpaEntity.from(domain);
```

### JPA Repository 메서드 작성 규칙

- **메서드 이름 기반 쿼리 우선**: JPQL `@Query` 사용보다는 메서드 이름 기반 쿼리를 우선 사용합니다
- **JPQL 사용 시 주석 필수**: JPQL `@Query`를 사용해야 하는 경우, 메서드 바로 위에 주석으로 사용 이유를 명시합니다
- **성능 최적화가 필요한 쿼리는 `@Query` 어노테이션을 사용**하여 JPQL 또는 네이티브 쿼리로 작성합니다
- **대용량 데이터 처리가 필요한 경우 페이징 처리**를 적용합니다
- **복잡한 쿼리는 Querydsl**을 사용하여 타입-세이프하게 작성합니다

```java
// ✅ 권장: 메서드 이름 기반 쿼리
List<FamilyMemberJpaEntity> findByFamilyIdAndStatus(Long familyId, FamilyMemberStatus status);

// ✅ JPQL 사용 시 주석으로 이유 명시
/**
 * 복잡한 조인과 서브쿼리가 필요하여 JPQL로 작성
 * 메서드 이름으로는 표현하기 어려운 복잡한 조건
 */
@Query("SELECT f FROM FamilyJpaEntity f WHERE f.id IN (SELECT fm.familyId FROM FamilyMemberJpaEntity fm WHERE fm.userId = :userId)")
List<FamilyJpaEntity> findFamiliesByUserId(@Param("userId") Long userId);

// JPQL 사용 예시
@Query("SELECT f FROM family f WHERE f.name LIKE %:keyword%")
List<FamilyJpaEntity> findByNameContaining(@Param("keyword") String keyword);

// 페이징 처리 예시
Page<FamilyJpaEntity> findAll(Pageable pageable);
```

## 기타 권장 사항

- 가능하면 외부 라이브러리에 의존하지 않는 순수 자바 코드를 작성합니다
- 모든 비즈니스 로직은 도메인 객체 내부에 캡슐화합니다
- 드문 경우에만 상속을 사용하고, 대부분은 컴포지션을 통해 재사용성을 높입니다
- Java의 최신 기능(sealed classes, records, pattern matching 등)을 적극 활용합니다

## 클래스별 작성 예시

### 도메인 엔티티 작성 예시

```java
public final class Family {
    private final Long id;
    private final String name;
    private final String description;
    private final String profileUrl;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    private Family(
        Long id,
        String name,
        String description,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(name, "name must not be null");
        // 유효성 검증
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    // 새 엔티티 생성
    public static Family create(
        String name,
        String description,
        String profileUrl,
        Long userId
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new Family(
            null, name, description, profileUrl, 
            userId, now, userId, now
        );
    }

    // 기존 엔티티 로드
    public static Family withId(
        Long id,
        String name,
        String description,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        return new Family(
            id, name, description, profileUrl,
            createdBy, createdAt, modifiedBy, modifiedAt
        );
    }

    // Getter 메서드
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // 비즈니스 메서드들...
}
```

### Command/Query 객체 작성 예시

```java
public record FindFamilyQuery(Long id) {
    public FindFamilyQuery {
        Objects.requireNonNull(id, "id must not be null");
    }
}
```

### 서비스 작성 예시

```java
@Service
@RequiredArgsConstructor
public class FindFamilyService implements FindFamilyUseCase {
    private final FindFamilyPort findFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Family findById(final FindFamilyQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.find(query.id())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
    }
}
```





5. be/instructions/testing-guidelines.md

# 테스트 코드 작성 가이드라인

## 테스트 분류

본 프로젝트는 다음과 같은 테스트 종류를 사용합니다:

1. **단위 테스트**: 개별 클래스나 메서드의 기능 검증
2. **인수 테스트**: API 엔드포인트 및 전체 기능 흐름 검증
3. **API 문서 테스트**: API 명세 문서화 및 검증

## 단위 테스트 작성 방법

### 공통 규칙

- **위치**: `src/test/java/{패키지 경로}`
- **어노테이션 순서**: `@Test` → `@DisplayName` 순서로 배치
- **메서드 이름**: snake_case로 `{행동}_{결과}_{조건}` 형식 사용
    - 예: `return_forbidden_when_exceed_max_join_limit()`
    - 예: `save_success_when_user_has_admin_role()`
- **테스트 설명**: `@DisplayName`에 명확한 한글 설명 사용
    - 형식: "{조건}일 때 {대상}은/는 {결과}합니다"
    - 예: `@DisplayName("최대 가입 가능 수를 초과했을 때 FORBIDDEN을 반환합니다")`

### 테스트 메서드 작성 패턴

```java
@Test
@DisplayName("관리자 권한이 있을 때 공지사항 작성에 성공합니다")
void save_success_when_user_has_admin_role() {
    // given
    FamilyMember adminMember = FamilyMember.withRole(
        1L, 1L, 1L, "관리자", "profile.jpg", now(),
        "KR", ACTIVE, ADMIN, null, null, null, null
    );
    when(findFamilyMemberPort.findByFamilyIdAndUserId(anyLong(), anyLong()))
        .thenReturn(Optional.of(adminMember));
    
    // when
    Long savedId = announcementService.save(new SaveAnnouncementCommand(
        1L, 1L, "공지사항", "내용"
    ));
    
    // then
    assertThat(savedId).isNotNull();
    verify(saveAnnouncementPort).save(any(Announcement.class));
}
```

간단한 테스트의 경우 given-when-then 주석 생략 가능합니다:

```java
@Test
@DisplayName("ID가 null인 경우 IllegalArgumentException이 발생합니다")
void throw_exception_when_id_is_null() {
    assertThatThrownBy(() -> new FindFamilyQuery(null))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### Command/Query 객체 테스트

- 생성자의 유효성 검증 로직을 테스트합니다
- 테스트 클래스에 `@DisplayName("[Unit Test] {Command/Query 클래스명}Test"` 선언합니다

### 도메인 엔티티 테스트

- 도메인 엔티티의 정적 팩토리 메서드 및 비즈니스 로직을 테스트합니다
- 테스트 클래스에 `@DisplayName("[Unit Test] {도메인 엔티티 명}Test"` 선언합니다

### Service 클래스 테스트

- 테스트 클래스에 `@DisplayName("[Unit Test] {Service 클래스명}Test"` 선언합니다
- Service 변수에 `@InjectMocks` 선언합니다
- Service 클래스가 의존하는 outbound port는 `@MockitoBean`을 선언하여 Mocking합니다
- 각 Mocking 코드에 주석으로 Mocking의 의도를 설명합니다

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyServiceTest")
class FindFamilyServiceTest {

    @InjectMocks
    private FindFamilyService findFamilyService;
    
    @Mock
    private FindFamilyPort findFamilyPort;
    
    @Test
    @DisplayName("유효한 ID로 조회 시 Family 객체를 반환합니다")
    void return_family_when_id_is_valid() {
        // given
        Long familyId = 1L;
        FindFamilyQuery query = new FindFamilyQuery(familyId);
        Family expectedFamily = Family.withId(
            familyId, "가족이름", "설명", "프로필URL", 1L, now(), 1L, now()
        );
        
        // Mocking: 유효한 ID로 Family 조회 모킹
        when(findFamilyPort.find(familyId)).thenReturn(Optional.of(expectedFamily));
        
        // when
        Family actualFamily = findFamilyService.findById(query);
        
        // then
        assertThat(actualFamily).isEqualTo(expectedFamily);
    }
}
```

### Adapter 클래스 테스트

- 테스트 클래스는 `AdapterTestBase` 상속합니다
- `@DisplayName("[Unit Test] {Adapter 클래스명}Test"` 선언합니다
- 필요한 JpaRepository `@Autowired` 합니다
- 테스트 대상 Adapter를 변수명 `sut`로 private 선언합니다
- `@BeforeEach setUp()` 메서드에서 sut에 필요한 JpaRepository을 생성자로 주입하여 sut 초기화합니다

```java
@DisplayName("[Unit Test] FamilyAdapterTest")
class FamilyAdapterTest extends AdapterTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;
    
    private FamilyAdapter sut;
    
    @BeforeEach
    void setUp() {
        sut = new FamilyAdapter(familyJpaRepository);
    }
    
    @Test
    @DisplayName("ID로 조회 시 Family 객체를 반환합니다")
    void find_returns_family_when_exists() {
        // given
        FamilyJpaEntity savedEntity = familyJpaRepository.save(
            new FamilyJpaEntity(null, "가족이름", "설명", "프로필URL", 1L, now(), 1L, now())
        );
        
        // when
        Optional<Family> result = sut.find(savedEntity.getId());
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getName()).isEqualTo(savedEntity.getName());
    }
}
```

## 인수 테스트 작성 방법

### 공통 규칙

- **위치**: 해당 컨트롤러가 있는 패키지
- **명명 규칙**: `{Find/Save/Modify/Delete}{도메인명}AcceptanceTest`
- **메서드 이름**: 단위 테스트와 동일한 규칙 적용
- **환경 설정**: `@SpringBootTest` 어노테이션 사용
- **테스트 클래스**: `AcceptanceTestBase` 상속
- `@DisplayName("[Acceptance Test] {Controller 클래스명}Test")` 선언

### 인수 테스트 작성 패턴

```java
@DisplayName("[Acceptance Test] FindFamilyControllerTest")
class FindFamilyAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;
    
    @Test
    @DisplayName("존재하는 ID로 가족 조회 시 200 OK와 가족 정보를 반환합니다")
    void find_returns_200_and_family_when_exists() {
        // given
        Family family = Family.create("가족이름", "설명", "프로필URL", 1L);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedEntity.getId();
        
        // when & then
        given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("data.id", equalTo(familyId.intValue()))
            .body("data.name", equalTo("가족이름"))
            .body("data.description", equalTo("설명"))
            .body("data.profileUrl", equalTo("프로필URL"));
    }
}
```

### 인수 테스트 중요 규칙

- 무조건 DB 데이터로 테스트. 기본적으로 mocking 미사용
- DB 데이터 생성을 위한 JpaRepository `@Autowired`
- 절대 `@BeforeEach`에서 테스트용 데이터 생성 금지. 각 테스트 메서드의 given 영역에서 데이터 생성

#### 테스트 시 엔티티 생성 규칙

- 테스트 코드에서는 JpaEntity 기본 생성자를 사용하지 않습니다
- 도메인 엔티티의 신규 엔티티 생성용 정적 메서드를 이용하여 도메인 엔티티를 생성한 뒤 `JpaEntity.from` 메서드를 호출하여 생성합니다
- 인수 테스트에서도 동일한 방식으로 테스트 데이터를 생성합니다

```java
// 올바른 테스트 데이터 생성 방식
@Test
void find_returns_family_when_exists() {
    // given
    Family family = Family.create("가족이름", "설명", "프로필URL", 1L);
    FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
    
    // when & then
    // ...
}

// 잘못된 방식 (기본 생성자 사용)
@Test
void wrong_way_to_create_test_data() {
    // 절대 사용하지 말 것!
    FamilyJpaEntity entity = new FamilyJpaEntity();
    entity.setName("가족이름"); // 컴파일 에러 - final 필드는 변경 불가
}
```
- **모든 테스트 메서드에 `@WithMockOAuth2User` 어노테이션 필수**: 인증이 필요한 API 테스트 시 일관된 Mock OAuth2 사용자 설정
- GET 메서드 이외에는 `.given()` 다음에 `.postProcessors(SecurityMockMvcRequestPostProcessors.csrf())` 필수 설정
- 요청 Body 데이터는 Multiline Strings (`"""`)를 이용
- 응답 Body 검증 시 RestAssured 내장 함수 활용
- API 내에서 발생 가능한 모든 예외 케이스를 테스트

## API 문서 테스트 작성 방법

### 공통 규칙

- **위치**: 해당 컨트롤러가 있는 패키지
- **명명 규칙**: `{Find/Save/Modify/Delete}{도메인명}DocsTest`
- **메서드 이름**: 단위 테스트와 동일한 규칙 적용 (snake_case)
- **테스트 클래스**: `ApiDocsTestBase` 상속
- `@DisplayName("[Docs Test] {Controller 클래스명}DocsTest")` 선언

### API 문서 테스트 중요 규칙

#### 데이터 생성 규칙 (인수 테스트와 동일)
- 무조건 DB 데이터로 테스트. 기본적으로 mocking 미사용
- DB 데이터 생성을 위한 JpaRepository `@Autowired`
- 테스트 시 엔티티 생성 규칙은 인수 테스트와 동일 (위 인수 테스트 섹션 참조)
- 절대 `@BeforeEach`에서 테스트용 데이터 생성 금지. 각 테스트 메서드의 given 영역에서 데이터 생성

#### RestAssuredMockMvc + REST Docs 전용 규칙
- `given()` → `when()` → `then()` → `apply(document(...))` 패턴 사용
- **모든 테스트 메서드에 `@WithMockOAuth2User` 어노테이션 필수**: 인증이 필요한 API 테스트 시 일관된 Mock OAuth2 사용자 설정
- GET 메서드 이외에는 `.given()` 다음에 `.postProcessors(SecurityMockMvcRequestPostProcessors.csrf())` 필수 설정
- 요청 Body 데이터는 Multiline Strings (`"""`)를 이용
- **문서화 필수 요소**:
    - `preprocessRequest(prettyPrint())`
    - `preprocessResponse(prettyPrint())`
    - `pathParameters()` (Path Variable 있는 경우)
    - `queryParameters()` (Query Parameter 있는 경우)
    - `requestFields()` (Request Body 있는 경우)
    - `responseFields()` (모든 응답 필드 문서화)

#### 예외 케이스 문서화 규칙
- API 내에서 발생 가능한 모든 예외 케이스를 별도 테스트 메서드로 작성
- 각 예외별로 `document("api-name-error-case", ...)` 형태로 문서화
- 예외 발생 지점:
    - **API Path Variable 검증 실패**
    - **API Request Parameter 검증 실패**
    - **API Request DTO 검증 실패**
    - **Command/Query 객체 생성 실패**
    - **UseCase 비즈니스 로직 예외**

#### 문서화 명명 규칙
- 성공 케이스: `document("find-family-tree", ...)`
- 실패 케이스: `document("find-family-tree-family-not-found", ...)`
- Path Variable 검증 실패: `document("find-family-tree-invalid-path-variable", ...)`
- Request Parameter 검증 실패: `document("find-family-tree-invalid-request-param", ...)`
- Request DTO 검증 실패: `document("find-family-tree-invalid-request", ...)`

## 테스트 작성 시점 및 방법

### 코어 계층 테스트
- **Service**: @InjectMocks, @Mock 사용
- **Command/Query**: 생성자 유효성 검증
- **Domain**: 정적 팩토리 메서드 및 비즈니스 로직
- **테스트 클래스명**: `@DisplayName("[Unit Test] {클래스명}Test")`

### 인프라 계층 테스트
- **Adapter**: AdapterTestBase 상속, sut 패턴 사용
- **JpaEntity**: 변환 메서드 (from, toXxx) 테스트
- **테스트 대상 변수명**: `sut` (System Under Test)
- **@BeforeEach**: sut 초기화

### 프레젠테이션 계층 테스트
- **Controller**: AcceptanceTestBase 상속
- **실제 DB 사용**: Mocking 미사용 원칙
- **데이터 생성**: 도메인 정적 메서드 → JpaEntity.from()
- **@WithMockOAuth2User**: 모든 테스트 메서드에 필수

## 테스트 실패 디버깅

### 단일 테스트 분석 원칙
**❌ 절대 여러 실패 테스트를 동시에 분석하지 말 것**
**✅ 반드시 첫 번째 실패 테스트만 선택하여 완전히 해결 후 다음으로 이동**

### 테스트 실패 분석 단계

#### 1. 에러 메시지 정확히 읽기
```
[테스트명] > [실패 메시지] FAILED
    [예외타입] at [파일명.java]:[라인번호]
```

#### 2. 라인 번호 기반 원인 분석
- 실패한 라인으로 즉시 이동
- 해당 라인에서 무엇을 하고 있는지 확인
- 왜 실패했는지 그 라인만 집중해서 분석

#### 3. 단일 원인 해결
- 한 번에 하나의 원인만 수정
- 수정 후 해당 테스트만 다시 실행
- 통과할 때까지 반복

### 테스트 실행 방법 (Claude Code 환경)
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "FamilyServiceTest"

# 특정 테스트 메서드 실행
./gradlew test --tests "FamilyServiceTest.find_returns_family_when_exists"
```

### 테스트 실패 시 금지 사항
- [ ] 여러 테스트 실패를 한번에 분석하려고 하기
- [ ] 에러 메시지 읽지 않고 추측으로 원인 찾기
- [ ] 라인 번호 무시하고 관련 없는 파일 뒤지기





6. be/instructions/commit-guidelines.md

# 커밋 가이드라인

## 문서 정보
- **목적**: 개발자 AI를 위한 Git 커밋 메시지 작성 규칙
- **버전**: v1.2
- **작성일**: 2025-09-06

---

## 1. 커밋 메시지 작성 규칙


### 1.1 커밋 메시지 제목 형식
```
{타입} [{협업구분}] {구현내용}

타입:
- feat: 기능 구현
- test: 테스트만 작성/수정
- fix: 버그 수정
- docs: 문서 작성/수정
- refactor: 코드 리팩토링

협업구분:
- [by-ai]: AI가 오롯이 구현
- [with-ai]: 사람과 AI가 협력하여 구현

```

### 1.3 커밋 메시지 본문 형식 (마크다운)
```markdown
- 작업 요약 (3줄 이내)

## 구현된 주요 컴포넌트

### 도메인 객체 (해당시)
- 구현한 Entity, ValueObject, DomainEvent 클래스들 나열

### 애플리케이션 계층 (해당시)
- UseCase, Service, Command/Query, Port 인터페이스 등 나열

### 인프라 계층 (해당시)
- Adapter, Repository, Configuration 등 나열

### 프레젠테이션 계층 (해당시)
- Controller, Request/Response DTO 등 나열

### 특화 사항 (해당시)
- 모바일 최적화
- 성능 고려사항
- 보안 강화
- 접근성 개선

## 테스트 구현 (해당시)
- 작성한 테스트 클래스들 ✅
- 테스트 커버리지 정보
- 특별한 테스트 전략

## 해결된 이슈 (해당시)
- 해결한 기술적 문제들 나열
- 예외 처리 개선사항

## 워크플로우 개선 (해당시)
- 개발 프로세스 개선사항
- 문서 개선사항

## 다음 단계
- 향후 작업 계획
- 의존성이 있는 후속 작업
```

---

## 2. 커밋 메시지 예시

### 2.1 기능 구현 예시
```
feat [by-ai] 디자인 시스템 v1.0 완성 - 모바일 퍼스트 따뜻한 계열
```

### 2.2 문서 작업 예시
```
docs [with-ai] XX 방식 변경 문서화
```

### 2.3 테스트만 작성하는 경우
```
test [with-ai] 사용자 인증 - 단위 테스트 작성
```

### 2.4 버그 수정 예시
```
fix [by-ai] 가족트리 데이터 구조 설계 - 순환 참조 버그 수정
```
---

## 3. AI의 커밋 작업 절차

#### Step 1: VCS 상태 확인
- 커밋되지 않은 작업 내역 및 최근 커밋 5개 내역 확인

#### Step 2: 커밋 메시지 작성
- 위의 양식에 따라 상세한 커밋 메시지 작성
- 본문에는 구현된 컴포넌트와 테스트 정보 포함

### 3.2 주의사항

#### ✅ 반드시 지켜야 할 것
- 테스트 통과 후에만 커밋
- 커밋 메시지 양식 엄격 준수

---

