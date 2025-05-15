# 백엔드 개발 지침서

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

## 프로젝트 구조

```
io.jhchoe.familytree/
├── common/                    # 공통 컴포넌트
│   ├── auth/                  # 인증 관련 
│   ├── config/                # 설정 클래스
│   ├── exception/             # 예외 처리 
│   ├── support/               # 공통 지원 클래스
│   └── util/                  # 유틸리티 클래스
├── core/                      # 핵심 도메인 
│   └── {도메인명}/            # 도메인별 패키지
│       ├── adapter/           # 어댑터 계층
│       │   ├── in/            # 인바운드 어댑터(컨트롤러)
│       │   │   ├── request/   # 요청 DTO
│       │   │   └── response/  # 응답 DTO (필요시)
│       │   └── out/           # 아웃바운드 어댑터(레포지토리)
│       │       └── persistence/ # 영속성 관련 클래스
│       ├── application/       # 애플리케이션 계층
│       │   ├── port/          # 포트 인터페이스
│       │   │   ├── in/        # 인바운드 포트(유스케이스)
│       │   │   └── out/       # 아웃바운드 포트
│       │   └── service/       # 서비스 구현체
│       └── domain/            # 도메인 모델
```

## 명명 규칙

### 일반 명명 규칙

- 행동 (이 규칙은 인바운드 어댑터, 인바운드 포트, 아웃바운드 포트에 일괄 적용됩니다)
  - 조회: Find
  - 등록: Save
  - 수정: Modify
  - 삭제: Delete
- 클래스: PascalCase (예: `FamilyMember`)
- 인터페이스: PascalCase (예: `FindFamilyUseCase`)
- 메서드, 변수: camelCase (예: `createFamily()`, `familyId`)
- 상수: UPPER_SNAKE_CASE (예: `MAX_FAMILY_MEMBERS`)
- 패키지: 소문자 (예: `io.jhchoe.familytree.core.family.domain`)

### 계층별 명명 규칙

| 계층       | 접두사 | 중간부분 | 접미사           | 예시                                     |
|----------|--------|----------|---------------|----------------------------------------|
| 인바운드 포트  | Find, Save, Modify, Delete | 도메인명 | UseCase       | `FindFamilyUseCase`                    |
| 아웃바운드 포트 | Find, Save, Modify, Delete | 도메인명 | Port          | `FindFamilyPort`                       |
| 서비스      | 없음 또는 UseCase의 접두사 | 도메인명 | Service       | `FamilyService`, `FindFamilyService`   |
| 인바운드 어댑터 | Find, Save, Modify, Delete | 도메인명 | Controller    | `FindFamilyController`                 |
| 아웃바운드 어댑터 | 없음 | 도메인명 | Adapter       | `FamilyAdapter`                        |
| JPA 엔티티  | 없음 | 도메인명 | JpaEntity     | `FamilyJpaEntity`                      |
| 요청 DTO   | Find, Save, Modify, Delete | 도메인명 | Request       | `SaveFamilyRequest`                    |
| 응답 DTO   | Find, Save, Modify, Delete | 도메인명 | Response      | `FindFamilyResponse`                   |
| 커맨드/쿼리 객체 | Find, Save, Modify, Delete | 도메인명 | Command/Query | `SaveFamilyCommand`, `FindFamilyQuery` |

## 계층별 구성 및 개발 지침

### 도메인 모델 계층

- **위치**: `core/{도메인명}/domain/`
- **역할**: 도메인의 핵심 개념, 규칙, 상태를 표현
- **명명 규칙**: 도메인 이름 그대로 사용 (예: `Family`, `FamilyMember`)
- **개발 지침**:
  - 모든 도메인 객체는 불변성(immutability)을 최대한 유지합니다
  - 비즈니스 로직은 도메인 객체 내부에 캡슐화합니다
  - 생성자는 private으로 선언하고 정적 팩토리 메서드를 통해 객체를 생성합니다
  - 모든 필드는 final로 선언하고 setter를 지양합니다
  - 값 객체(Value Object)는 별도 패키지에 분리합니다

**예시: Family 도메인 객체**

```java
package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * Family를 나타내는 클래스입니다. 이 클래스는 Family의 주요 구성 요소인 ID, 이름, 설명, 프로필 URL을 포함합니다.
 */
@Getter
public class Family {
    private final Long id;
    private final String name;
    private final String description;
    private final String profileUrl;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    /**
     * Family 객체를 생성하는 생성자입니다.
     */
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
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 새로운 Family 객체를 생성하는 정적 팩터리 메서드입니다.
     */
    public static Family newFamily(
        String name,
        String description,
        String profileUrl
    ) {
        Objects.requireNonNull(name, "name must not be null");
        return new Family(null, name, description, profileUrl, null, null, null, null);
    }

    /**
     * 기존 Family 객체를 불러오는 정적 팩터리 메서드입니다.
     */
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
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        return new Family(id, name, description, profileUrl, createdBy, createdAt, modifiedBy, modifiedAt);
    }

    /**
     * Family의 정보를 업데이트합니다.
     */
    public Family update(String name, String description, String profileUrl) {
        Objects.requireNonNull(name, "name must not be null");
        return new Family(this.id, name, description, profileUrl, 
            this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt);
    }
}
```

### 인바운드 포트 계층

- **위치**: `core/{도메인명}/application/port/in/`
- **역할**: 애플리케이션이 외부에 제공하는 기능 인터페이스 정의
- **명명 규칙**: `{행동}{도메인}UseCase` (예: `FindFamilyUseCase`, `SaveFamilyUseCase`)
- **메서드명 규칙**: 등록 - save, 조회 - find, 수정 - modify, 삭제 - delete
- **개발 지침**:
  - 각 유스케이스는 단일 메서드를 가진 인터페이스로 정의합니다
  - 입력 파라미터는 `{행동}{도메인}Command` 또는 `{행동}{도메인}Query` 객체로 캡슐화합니다
  - Command 객체는 생성자에서 유효성 검증을 수행합니다
  - 반환 타입은 도메인 객체 또는 ID(식별자)를 우선 사용합니다
  - 유스케이스 메서드는 Optional 타입을 리턴하지 않습니다. Optional 데이터에 대한 분기 처리는 구현체 Service 클래스의 메서드 내부에서 처리합니다.

**예시: FindFamilyUseCase 인터페이스**

```java
package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Optional;

/**
 * Family 조회를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface FindFamilyUseCase {

    /**
     * 지정된 ID의 Family를 조회합니다.
     *
     * @param query Family 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 Family를 Optional로 래핑하여 반환, 존재하지 않을 경우 빈 Optional 반환
     */
    Optional<Family> findById(FindFamilyQuery query);
}
```

**예시: FindFamilyQuery 객체**

```java
package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * Family 조회를 위한 쿼리 객체입니다.
 */
@Getter
public class FindFamilyQuery {
    private final Long id;

    /**
     * Family 조회 쿼리 객체를 생성합니다.
     *
     * @param id 조회할 Family의 ID
     * @throws IllegalArgumentException ID가 null이거나 0보다 작을 경우 예외 발생
     */
    public FindFamilyQuery(Long id) {
        validateId(id);
        this.id = id;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Family ID는 필수입니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("유효하지 않은 Family ID입니다.");
        }
    }
}
```

**예시: SaveFamilyUseCase 인터페이스**

```java
package io.jhchoe.familytree.core.family.application.port.in;

/**
 * Family 생성을 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface SaveFamilyUseCase {

    /**
     * Family 생성을 처리합니다.
     *
     * @param command Family 생성에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 생성된 Family의 고유 식별자
     */
    Long save(SaveFamilyCommand command);
}
```

**예시: SaveFamilyCommand 객체**

```java
package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * Family 정보를 생성하기 위한 커맨드 객체입니다.
 */
@Getter
public class SaveFamilyCommand {
    private final String name;
    private final String profileUrl;
    private final String description;

    /**
     * Family 생성 커맨드 객체를 생성합니다.
     * 
     * @param name Family 이름 (필수, 최대 길이: 100자)
     * @param profileUrl 프로필 URL (선택)
     * @param description Family 설명 (선택, 최대 길이: 200자)
     * @throws IllegalArgumentException 입력값이 유효하지 않을 경우 예외 발생
     */
    public SaveFamilyCommand(String name, String profileUrl, String description) {
        validateName(name);
        validateProfileUrl(profileUrl);
        validateDescription(description);
        
        this.name = name;
        this.profileUrl = profileUrl;
        this.description = description;
    }
    
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Family 이름을 입력해주세요.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Family 이름은 100자 이내로 작성해주세요.");
        }
    }
    
    private void validateProfileUrl(String profileUrl) {
        if (profileUrl != null && !profileUrl.isBlank()) {
            if (!profileUrl.startsWith("http://") && !profileUrl.startsWith("https://")) {
                throw new IllegalArgumentException("프로필 URL 형식이 유효하지 않습니다.");
            }
        }
    }
    
    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("Family 설명은 200자 이내로 작성해주세요.");
        }
    }
}
```

### 아웃바운드 포트 계층

- **위치**: `core/{도메인명}/application/port/out/`
- **역할**: 외부 시스템과의 상호작용을 위한 인터페이스 정의
- **명명 규칙**: `{행동}{도메인}Port` (예: `LoadFamilyPort`, `CreateFamilyPort`)
- **메서드명 규칙**: 등록 - save, 조회 - find, 수정 - modify, 삭제 - delete.
- **개발 지침**:
  - 인터페이스는 최소한의 기능만 정의합니다
  - 도메인 객체 타입을 파라미터와 반환 타입으로 사용합니다
  - 기술적인 세부사항(예: SQL, JPA)을 노출하지 않습니다

**예시: FindFamilyPort 인터페이스**

```java
package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Optional;

/**
 * Family 엔티티를 조회하기 위한 포트 인터페이스입니다.
 */
public interface FindFamilyPort {

    /**
     * 지정된 ID에 해당하는 Family 엔티티를 조회합니다.
     *
     * @param id 조회할 Family의 고유 식별자
     * @return 조회된 Family 객체를 포함하는 Optional, 존재하지 않는 경우 빈 Optional 반환
     */
    Optional<Family> find(Long id);
}
```

**예시: SaveFamilyPort 인터페이스**

```java
package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;

/**
 * Family 정보를 저장하는 데 사용되는 포트입니다.
 */
public interface SaveFamilyPort {

    /**
     * 지정된 Family 정보를 기반으로 새 가족을 저장합니다.
     *
     * @param family 저장할 Family 정보를 담고 있는 객체
     * @return 저장된 Family의 ID
     */
    Long save(Family family);
}
```

### 애플리케이션 서비스 계층

- **위치**: `core/{도메인명}/application/service/`
- **역할**: 유스케이스 구현 및 비즈니스 프로세스 조정
- **명명 규칙**:
  - 단일 유스케이스 구현: `{행동}{도메인}Service` (예: `FindFamilyService`)
- **개발 지침**:
  - 각 서비스는 단일 유스케이스 인터페이스를 구현합니다
  - 도메인 로직은 도메인 객체에 위임합니다
  - 예외는 유스케이스에서 발생시키며 어댑터에서 예외를 발생시키지 않습니다. 
  - 외부 시스템 상호작용은 아웃바운드 포트를 통해 수행합니다
  - `@Transactional` 어노테이션을 클래스 또는 메서드 레벨에 적용합니다
  - 서비스가 구현한 메서드는 UseCase 인터페이스의 주석을 그대로 이용할 수 있도록 `{@inheritDoc}` 주석을 추가합니다.

**예시: FamilyService 클래스**

```java
package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.FindFamilyQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindFamilyService implements SaveFamilyUseCase, FindFamilyUseCase {

    private final SaveFamilyPort saveFamilyPort;
    private final FindFamilyPort findFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveFamilyCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Family family = Family.newFamily(command.getName(), command.getDescription(), command.getProfileUrl());
        return saveFamilyPort.save(family);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Family findById(FindFamilyQuery query) {
        Objects.requireNonNull(query, "query must not be null");

      return findFamilyPort.findFamily(query.getId())
              .orElseThrow(() -> FTException.NOT_FOUND);
    }
}
```

### 인바운드 어댑터 계층

- **위치**: `core/{도메인명}/adapter/in/`
- **역할**: 외부 요청을 애플리케이션 코어와 연결
- **명명 규칙**: `{행동}{도메인}Controller` (예: `FindFamilyController`, `SaveFamilyController`)
- **메서드명 규칙**: 등록 - save, 조회 - find, 수정 - modify, 삭제 - delete.
- **개발 지침**:
  - 모든 API 엔드포인트는 `/api/`로 시작합니다
  - 컨트롤러 메서드는 요청 객체를 커맨드/쿼리 객체로 변환하는 역할만 담당합니다
  - 인바운드 포트(유스케이스)를 호출하여 비즈니스 로직을 실행합니다
  - 컨트롤러는 비즈니스 로직 처리나 예외 발생을 담당하지 않습니다 - 이는 서비스 계층의 책임입니다
  - UseCase 인터페이스 메서드는 Optional 타입을 리턴하지 않습니다. Optional 데이터에 대한 분기 처리는 구현체 Service 클래스의 역할이므로 인바운트 어댑터에서는 Optional에 대한 분기 처리를 담당하지 않습니다.
  - 인바운트 어댑터의 역할은 Request Dto를 application 계층으로 전달할 Command 또는 Query 정보로 변환하는 역할, 그리고 UseCase가 응답해준 데이터를 Response Dto로 변환하여 응답하는 역할만 수행합니다.
  - 유스케이스에서 응답받은 도메인 객체는 단순히 `{행동}{도메인}Response` DTO로 변환만 합니다
  - 응답은 `ResponseEntity<T>` 를 사용합니다
  - `@RestController`, `@RequestMapping` 어노테이션을 사용합니다
  - 입력 유효성 검증은 `@Valid` 어노테이션으로 수행합니다
  - 인바운드 어댑터에 선언되는 HTTP API 경로는 항상 prefix로 `/api`를 붙입니다

**예시: SaveFamilyController 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family 생성을 위한 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class SaveFamilyController {

    private final SaveFamilyUseCase saveFamilyUseCase;

    /**
     * 새로운 Family를 생성합니다.
     *
     * @param ftUser 현재 인증된 사용자
     * @param request Family 생성 요청 정보
     * @return 생성된 Family의 ID
     */
    @PostMapping
    public ResponseEntity<Long> save(
                        @AuthFTUser FTUser ftUser,
        @RequestBody @Valid SaveFamilyRequest request
    ) {
        SaveFamilyCommand command = new SaveFamilyCommand(
            request.name(),
            request.profileUrl(),
            request.description()
        );

        Long familyId = saveFamilyUseCase.save(command);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(familyId);
    }
}
```

**예시: SaveFamilyRequest 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Family 생성 요청을 위한 DTO 클래스입니다.
 */
public record SaveFamilyRequest(
    @NotBlank(message = "Family 이름을 입력해주세요.")
    @Size(max = 100, message = "Family 이름은 100자 이내로 작성해주세요.")
    String name,

    String profileUrl,

    @Size(max = 200, message = "Family 설명은 200자 이내로 작성해주세요.")
    String description
) {
}
```

**예시: FindFamilyController 클래스**

```java
package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
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
    public ResponseEntity<FindFamilyResponse> find(@PathVariable Long id) {
        FindFamilyQuery query = new FindFamilyQuery(id);
        
        Family family = findFamilyUseCase.findById(query);
        FindFamilyReponse response = FindFamilyReponse(
                family.getId(),
                family.getName(),
                family.getDescription(),
                family.getCreatedBy(),
                family.getCreatedAt(),
                family.getModifiedBy(),
                family.getModifiedAt()
        );
        return ResponseEntity.ok(response);
    }
}
```

### 아웃바운드 어댑터 계층

- **위치**: `core/{도메인명}/adapter/out/`
- **역할**: 외부 시스템과의 통신 구현
- **명명 규칙**: `{도메인}Adapter` (예: `FamilyAdapter`)
- **개발 지침**:
  - 아웃바운드 포트 인터페이스를 구현합니다
  - JPA 엔티티는 `{도메인}JpaEntity` 형식으로 명명합니다
  - 코어에서는 도메인 객체를 전달받아 아웃바운드 어댑터에서 엔티티로 변환한 후 작업합니다.
  - 아웃바운드 어댑터에서 조회한 엔티티는 도메인 객체로 변환하여 응답합니다.
  - JPA 리포지토리는 Spring Data JPA 인터페이스로 정의합니다

## 엔티티-도메인 변환 패턴

### 변환 책임
- 엔티티→도메인 변환: JpaEntity 클래스의 `toXxx()` 메서드 담당
- 도메인→엔티티 변환: JpaEntity 클래스의 정적 `from(xxx)` 메서드 담당

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
- 컬렉션은 항상 방어적 복사 수행
- 빈 컬렉션은 `Collections.emptyList()`로 반환 (null 반환 금지)

### 순환 참조 처리
- 양방향 관계에서는 한쪽에서만 변환 수행
- 필요한 경우 ID만 참조하는 가벼운 객체 사용

## 예외 처리 가이드라인

- **위치**: `common/exception/`
- **핵심 클래스**:
  - `ExceptionCodeType`: 예외 코드 타입 인터페이스
  - `FTException`: 애플리케이션 공통 예외 클래스
  - `ErrorResponse`: 클라이언트 응답용 예외 정보 클래스
  - `GlobalExceptionHandler`: 전역 예외 처리 핸들러
- **개발 지침**:
  - 모든 비즈니스 예외는 FTException 클래스를 직접 사용합니다
  - 예외를 구분하는 것은 생성자에 전달하는 ExceptionCodeType 구현체(enum)입니다
  - 자주 사용하는 예외는 FTException 클래스 내부에 상수로 정의되어 있습니다
  - 예외 코드는 도메인별로 `{도메인}ExceptionCode` enum으로 정의합니다
  - 모든 예외는 `GlobalExceptionHandler`에서 일관되게 처리합니다
  - 유효성 검증 실패는 상세 정보를 포함한 `ErrorResponse`로 응답합니다

## 테스트 코드 작성 가이드라인

### 단위 테스트 (Unit Test)

- **위치**: `src/test/java/{패키지 경로}`
- **명명 규칙**: `{테스트 대상 클래스}Test`
- **개발 지침**:
  - JUnit 5와 AssertJ를 사용합니다
  - 모든 클래스는 단위 테스트를 작성합니다
  - 테스트 메서드 이름은 `given_when_then` 형식으로 작성합니다
  - `@DisplayName`으로 한글 테스트 의도를 명시합니다
  - 각 테스트는 하나의 기능/시나리오만 검증합니다
  - Mockito를 사용한 의존성 모킹을 통해 격리된 테스트를 작성합니다
  - 매개변수화 테스트는 `@ParameterizedTest`와 함께 사용합니다
  - Mock Bean을 사용해야 하는 경우 `@MockitoBean`을 사용합니다 - `@MockBean`은 사용하지 않음

### 인수 테스트 (Acceptance Test)

- **위치**: 해당 컨트롤러가 있는 패키지
- **명명 규칙**: `{행동}{도메인명}AcceptanceTest`
- **개발 지침**:
  - RestAssured와 MockMvc를 조합하여 End-to-End 테스트를 작성합니다
  - 성공/실패 케이스를 모두 테스트합니다
  - `@SpringBootTest` 어노테이션으로 통합 테스트 환경을 설정합니다
  - 인수테스트는 mock을 사용하지 않고 테스트를 수행합니다
  - 테스트 사용자 인증은 `@WithMockOAuth2User` 어노테이션을 사용합니다
  - Mock Bean을 사용해야 하는 경우 `@MockitoBean`을 사용합니다 - `@MockBean`은 사용하지 않음

### API 문서 테스트

- **위치**: 해당 컨트롤러가 있는 패키지
- **명명 규칙**: `{행동}{도메인명}DocsTest`
- **개발 지침**:
  - Spring REST Docs를 사용하여 API 문서를 자동 생성합니다
  - 문서화 대상 엔드포인트별로 테스트 메서드를 작성합니다
  - 요청/응답 필드를 명시적으로 문서화합니다
  - API에서 발생 가능한 모든 예외 코드를 문서화합니다
    - 예외발생 가능 포인트
      - API Path Variable
      - API Request DTO (`/in/XxxXxxRequest`)
      - `application/port/in/XxxXxxCommand`
      - `application/port/in/XxxXxxQuery`
      - `application/port/in/XxxXxxUseCase`
  - `ApiDocsTestBase` 클래스를 상속받아 문서화 환경을 구성합니다
  - Mock Bean을 사용해야 하는 경우 `@MockitoBean`을 사용합니다 - `@MockBean`은 사용하지 않음

## 공통 컴포넌트 가이드라인

### 인증 및 권한

- **위치**: `common/auth/`
- **개발 지침**:
  - 사용자 인증은 `@AuthFTUser` 어노테이션으로 컨트롤러에서 처리합니다
  - 권한 검사는 Spring Security 구성을 통해 URI 패턴별로 설정합니다
  - OAuth2와 폼 로그인 인증을 모두 지원합니다
  - 인증 예외는 `AuthExceptionCode`를 통해 처리합니다

### 감사 (Auditing)

- **위치**: `common/support/`
- **개발 지침**:
  - 모든 엔티티는 `CreatorBaseEntity` 또는 `ModifierBaseEntity`를 상속받습니다
  - Spring Data JPA Auditing을 통해 생성/수정 정보를 자동 관리합니다
  - `@CreatedBy`, `@LastModifiedBy` 값은 현재 인증된 사용자 ID로 설정됩니다

## 도메인 이벤트 처리

### 이벤트 정의 및 발행
- 도메인 이벤트는 `core/{도메인명}/domain/event` 패키지에 정의
- 이벤트 클래스는 `{동작}{도메인}Event` 형식으로 명명 (예: `FamilyCreatedEvent`)
- 이벤트는 불변(immutable) 객체로 구현
- 모든 이벤트는 Spring ApplicationEventPublisher를 통해 발행

### 이벤트 구독
- 이벤트 핸들러는 `core/{도메인명}/application/eventhandler` 패키지에 정의
- `@EventListener` 어노테이션을 사용하여 이벤트 구독
- 트랜잭션 전파 설정은 `@TransactionalEventListener`로 명시

```java
// 이벤트 정의 예시
public record FamilyCreatedEvent(Long familyId, String name, LocalDateTime createdAt) {
    public static FamilyCreatedEvent from(Family family) {
        return new FamilyCreatedEvent(
            family.getId(), 
            family.getName(), 
            family.getCreatedAt()
        );
    }
}

// 이벤트 발행 예시 (서비스 클래스 내)
@Autowired
private ApplicationEventPublisher eventPublisher;

@Transactional
public Long create(CreateFamilyCommand command) {
    // 비즈니스 로직
    Family family = Family.newFamily(...);
    Long id = createFamilyPort.create(family);
    
    // 이벤트 발행
    eventPublisher.publishEvent(FamilyCreatedEvent.from(family));
    
    return id;
}

// 이벤트 구독 예시
@Component
@RequiredArgsConstructor
public class FamilyEventHandler {
    private final NotificationPort notificationPort;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFamilyCreated(FamilyCreatedEvent event) {
        notificationPort.sendNotification(
            NotificationType.FAMILY_CREATED,
            event.familyId(),
            "새로운 가족이 생성되었습니다: " + event.name()
        );
    }
}
```

## 코드 스타일 가이드라인

- Java 21 이상의 기능을 활용합니다
- Lombok을 사용하여 보일러플레이트 코드를 최소화하되, Getter, Setter만 사용합니다. 단, Response Dto의 경우 Builder 사용을 허용합니다
- Request/Response Dto는 record 타입을 사용합니다
- 불변 객체와 함수형 프로그래밍 스타일을 권장합니다
- 메서드 매개변수는 `final` 키워드를 사용합니다
- 모든 클래스와 메서드에 JavaDoc을 작성합니다. 인터페이스를 구현하는 쪽에서는 `{@inheritDoc}` 을 사용하여 인터페이스에 작성된 주석을 재사용하도록 합니다
- 메서드 내에서 return 분기 처리가 있는 경우 기본적으로 빠른 return 문을 사용합니다. 단, 가독성을 고려하여 변경할 수 있습니다
- 조건문은 기본적으로 긍정문으로 작성합니다. 단, 가독성을 고려하여 변경할 수 있습니다
- 메서드 시그니처에 인자가 3개 이상 선언된 경우 첫 번째 인자부터 모든 인자를 줄바꿈하여 선언합니다
- 가능한 한 접근제어자의 범위를 좁게 선언합니다
- 상수는 대문자 스네이크 케이스(UPPER_SNAKE_CASE)로 명명합니다
- 도메인 객체의 유효성 검증은 생성자 또는 팩토리 메서드에서 수행합니다
- 개발한 비즈니스 로직은 `be/README.md` 파일에 문서화합니다. 이때 각 도메인간 내용을 적절히 연계하여 작성합니다

## 개발 검증 체크리스트

코드 제출 전 다음 항목을 확인하세요:

1. [ ] 도메인 로직이 도메인 객체 내에 적절히 캡슐화되었습니까?
2. [ ] 포트와 어댑터가 명확하게 분리되어 있습니까?
3. [ ] 모든 클래스가 단일 책임 원칙을 준수합니까?
4. [ ] 예외는 적절한 ExceptionCode로 처리됩니까?
5. [ ] 단위 테스트와 인수 테스트, REST Docs 테스트가 작성되었습니까?
6. [ ] API 문서화가 REST Docs를 통해 수행되었습니까?
7. [ ] 명명 규칙이 일관되게 적용되었습니까?
8. [ ] 개발한 비즈니스 로직을 `be/README.md` 파일에 문서화했습니까?

이 지침을 따르면 헥사고날 아키텍처 기반의 견고하고 유지보수가 용이한 백엔드 시스템을 구축할 수 있습니다.
