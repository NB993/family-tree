# 명명 규칙 지침서

## ⚠️ 필수 명명 규칙 (개발 전 확인)

| 동작 유형 | 사용할 접두사 | 잘못된 예시 |
|---------|------------|------------|
| 조회 | **Find** | Get, Retrieve, Query |
| 등록 | **Save** | Create, Add, Insert |
| 수정 | **Modify** | Update, Change, Edit |
| 삭제 | **Delete** | Remove, Erase |

모든 인바운드 어댑터, 인바운드 포트, 아웃바운드 포트는 반드시 위 접두사를 사용해야 합니다.

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
