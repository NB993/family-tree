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
