# 백엔드 개발 지침서

## ⚠️ 필수 명명 규칙 (개발 전 확인)

| 동작 유형 | 사용할 접두사 | 잘못된 예시 |
|---------|------------|------------|
| 조회 | **Find** | Get, Retrieve, Query |
| 등록 | **Save** | Create, Add, Insert |
| 수정 | **Modify** | Update, Change, Edit |
| 삭제 | **Delete** | Remove, Erase |

모든 인바운드 어댑터, 인바운드 포트, 아웃바운드 포트는 반드시 위 접두사를 사용해야 합니다.

## 명명 규칙

### 계층별 명명 규칙

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

### 일반 명명 규칙

- 클래스: PascalCase (예: `FamilyMember`)
- 인터페이스: PascalCase (예: `FindFamilyUseCase`)
- 메서드, 변수: camelCase (예: `findById()`, `familyId`)
- 상수: UPPER_SNAKE_CASE (예: `MAX_FAMILY_MEMBERS`)
- 패키지: 소문자 (예: `io.jhchoe.familytree.core.family.domain`)

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

## 아키텍처 개요

본 프로젝트는 헥사고날 아키텍처(Hexagonal Architecture) 기반의 클린 코드 구조를 따르며, Spring Boot 프레임워크를 활용합니다. 이 구조는 도메인 로직을 핵심으로 두고, 외부 시스템과의 상호 작용을 포트와 어댑터를 통해 분리하여 관리합니다.

## 핵심 계층별 개발 지침

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
  - 반환 타입은 도메인 객체 또는 ID(식별자)를 우선 사용합니다
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

**예시: FindFamilyController 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.support.ApiResponse;
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
    public ResponseEntity<ApiResponse<FindFamilyResponse>> findById(@PathVariable Long id) {
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
        
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
```

**예시: FindFamilyResponse 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.in.response;

import java.time.LocalDateTime;

/**
 * Family 조회 응답을 위한 DTO 클래스입니다.
 */
public record FindFamilyResponse(
    Long id,
    String name,
    String description,
    String profileUrl,
    LocalDateTime createdAt
) {
}
```

### 아웃바운드 어댑터 계층
- **위치**: `core/{도메인명}/adapter/out/`
- **역할**: 외부 시스템과의 통신 구현
- **지침**:
  - 아웃바운드 포트 인터페이스를 구현합니다
  - JPA 엔티티는 `{도메인}JpaEntity` 형식으로 명명합니다
  - 코어에서는 도메인 객체를 전달받아 아웃바운드 어댑터에서 엔티티로 변환한 후 작업합니다
  - 아웃바운드 어댑터에서 조회한 엔티티는 도메인 객체로 변환하여 응답합니다
  - JPA 리포지토리는 Spring Data JPA 인터페이스로 정의합니다

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

**예시: FamilyJpaEntity 클래스**

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

**예시: FamilyJpaRepository 인터페이스**

```java
package io.jhchoe.familytree.core.family.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Family JPA 엔티티에 대한 리포지토리 인터페이스입니다.
 */
public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {
}
```

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

## 테스트 코드 작성 가이드라인

### 단위 테스트 스타일

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

// 간단한 테스트의 경우 given-when-then 주석 생략 가능
@Test
@DisplayName("ID가 null인 경우 IllegalArgumentException이 발생합니다")
void throw_exception_when_id_is_null() {
    assertThatThrownBy(() -> new FindFamilyQuery(null))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### 인수 테스트

- **위치**: 해당 컨트롤러가 있는 패키지
- **명명 규칙**: `{Find/Save/Modify/Delete}{도메인명}AcceptanceTest`
- **메서드 이름**: 단위 테스트와 동일한 규칙 적용
- **환경 설정**: `@SpringBootTest` 어노테이션 사용

### API 문서 테스트

- **위치**: 해당 컨트롤러가 있는 패키지
- **명명 규칙**: `{Find/Save/Modify/Delete}{도메인명}DocsTest`
- **문서화 범위**: 요청/응답 필드 및 발생 가능한 모든 예외 코드 문서화
- **예외 문서화**: 다음의 예외 발생 가능 지점을 모두 문서화
  - API Path Variable
  - API Request DTO
  - Command/Query 객체
  - UseCase 인터페이스

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

## 코드 스타일 가이드라인

- Java 21 이상의 기능을 활용합니다
- Lombok은 Getter만 사용하고, 필요한 경우에만 Builder 패턴 적용합니다
- Request/Response DTO는 record 타입으로 작성합니다
- 불변 객체와 함수형 프로그래밍 스타일을 권장합니다
- 메서드 매개변수는 `final` 키워드를 사용합니다
- 모든 클래스와 메서드에 JavaDoc을 작성합니다. 인터페이스를 구현하는 쪽에서는 `{@inheritDoc}`을 사용합니다
- 메서드 내에서 return 분기 처리가 있는 경우 기본적으로 빠른 return 문을 사용합니다
- 조건문은 기본적으로 긍정문으로 작성합니다
- 메서드 시그니처에 인자가 3개 이상 선언된 경우 첫 번째 인자부터 모든 인자를 줄바꿈하여 선언합니다
- 가능한 한 접근제어자의 범위를 좁게 선언합니다
- 상수는 대문자 스네이크 케이스(UPPER_SNAKE_CASE)로 명명합니다
- 도메인 객체의 유효성 검증은 생성자 또는 팩토리 메서드에서 수행합니다

## 개발 전 필수 확인사항 ✓

1. [ ] 도메인별 행동 명명 규칙(Find/Save/Modify/Delete)을 숙지했습니까?
2. [ ] 계층별 클래스 명명 패턴을 이해했습니까?
3. [ ] 도메인 모델의 불변성과 캡슐화 원칙을 이해했습니까?
4. [ ] 헥사고날 아키텍처의 포트와 어댑터 분리 개념을 이해했습니까?
5. [ ] 예외 처리 방식과 FTException 활용법을 숙지했습니까?
6. [ ] 주석과 JavaDoc 작성 어투/패턴을 확인했습니까?
7. [ ] 테스트 코드 작성 방법과 네이밍 규칙을 이해했습니까?

## 코드 제출 전 최종 점검사항 ✓

1. [ ] **명명 규칙 준수**: 모든 접두사가 Find/Save/Modify/Delete 중 올바른 것으로 사용되었는지 확인
   - 조회 기능: Find만 사용 (Get/Retrieve/Query 사용 금지)
   - 등록 기능: Save만 사용 (Create/Add/Insert 사용 금지)
   - 수정 기능: Modify만 사용 (Update/Change/Edit 사용 금지)
   - 삭제 기능: Delete만 사용 (Remove/Erase 사용 금지)
2. [ ] **계층별 클래스명 확인**: 각 계층(UseCase, Port, Controller 등)의 클래스명이 지정된 패턴을 따르는지 확인
3. [ ] **호환성 검토**: 메서드명이나 시그니처를 변경한 경우, 해당 메서드를 호출하는 모든 코드가 정상 작동하는지 확인
4. [ ] **도메인 로직 캡슐화**: 비즈니스 로직이 도메인 객체 내부에 적절히 캡슐화되었는지 확인
5. [ ] **포트/어댑터 분리**: 포트와 어댑터 계층이 명확하게 분리되어 있는지 확인
6. [ ] **예외 발생 위치**: 예외가 유스케이스에서만 발생하고 어댑터에서 발생하지 않는지 확인
7. [ ] **주석 어투 일관성**: 모든 주석과 JavaDoc이 지정된 어투와 패턴을 따르는지 확인
8. [ ] **테스트 코드 작성**: 모든 유스케이스에 대한 단위 테스트가 작성되었는지 확인
9. [ ] **코드 스타일 일관성**: 코드스타일과 네이밍이 프로젝트 전체에서 일관되게 적용되었는지 확인
