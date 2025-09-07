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
  - **save, modify 메서드는 저장 또는 수정한 엔티티의 ID(Long)를 반환합니다**

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
  - **save, modify 메서드는 저장 또는 수정한 엔티티의 ID(Long)를 반환합니다**

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
