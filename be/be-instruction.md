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
│       │   └── out/           # 아웃바운드 어댑터(레포지토리)
│       ├── application/       # 애플리케이션 계층
│       │   ├── port/          # 포트 인터페이스
│       │   │   ├── in/        # 인바운드 포트(유스케이스)
│       │   │   └── out/       # 아웃바운드 포트
│       │   └── service/       # 서비스 구현체
│       └── domain/            # 도메인 모델
```

## 계층별 명명 규칙 및 책임

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

### 인바운드 포트 계층

- **위치**: `core/{도메인명}/application/port/in/`
- **역할**: 애플리케이션이 외부에 제공하는 기능 인터페이스 정의
- **명명 규칙**: `{행동}{도메인}UseCase` (예: `CreateFamilyUseCase`, `QueryFamilyUseCase`)
- **개발 지침**:
    - 각 유스케이스는 단일 메서드를 가진 인터페이스로 정의합니다
    - 입력 파라미터는 `{행동}{도메인}Command` 또는 `{행동}{도메인}Query` 객체로 캡슐화합니다
    - Command 객체는 생성자에서 유효성 검증을 수행합니다
    - 반환 타입은 도메인 객체 또는 ID(식별자)를 우선 사용합니다

### 아웃바운드 포트 계층

- **위치**: `core/{도메인명}/application/port/out/`
- **역할**: 외부 시스템과의 상호작용을 위한 인터페이스 정의
- **명명 규칙**: `{행동}{도메인}Port` (예: `LoadFamilyPort`, `CreateFamilyPort`)
- **개발 지침**:
    - 인터페이스는 최소한의 기능만 정의합니다
    - 도메인 객체 타입을 파라미터와 반환 타입으로 사용합니다
    - 기술적인 세부사항(예: SQL, JPA)을 노출하지 않습니다

### 애플리케이션 서비스 계층

- **위치**: `core/{도메인명}/application/service/`
- **역할**: 유스케이스 구현 및 비즈니스 프로세스 조정
- **명명 규칙**:
    - 단일 유스케이스 구현: `{행동}{도메인}Service` (예: `CreateFamilyService`)
    - 여러 유스케이스 구현: `{도메인}Service` (예: `FamilyService`)
- **개발 지침**:
    - 각 서비스는 하나 이상의 유스케이스 인터페이스를 구현합니다
    - 도메인 로직은 도메인 객체에 위임합니다
    - 외부 시스템 상호작용은 아웃바운드 포트를 통해 수행합니다
    - `@Transactional` 어노테이션을 클래스 또는 메서드 레벨에 적용합니다

### 인바운드 어댑터 계층

- **위치**: `core/{도메인명}/adapter/in/`
- **역할**: 외부 요청을 애플리케이션 코어와 연결
- **명명 규칙**: `{행동}{도메인}Controller` (예: `CreateFamilyController`, `ModifyFamilyController`)
- **개발 지침**:
    - 모든 API 엔드포인트는 `/api/`로 시작합니다
    - 컨트롤러 메서드는 요청 객체를 커맨드/쿼리 객체로 변환합니다
    - 인바운드 포트(유스케이스)를 호출하여 비즈니스 로직을 실행합니다
    - 응답은 `ApiResponse<T>` 객체로 래핑합니다
    - `@RestController`, `@RequestMapping` 어노테이션을 사용합니다
    - 입력 유효성 검증은 `@Valid` 어노테이션으로 수행합니다

### 아웃바운드 어댑터 계층

- **위치**: `core/{도메인명}/adapter/out/`
- **역할**: 외부 시스템과의 통신 구현
- **명명 규칙**: `{도메인}Adapter` (예: `FamilyAdapter`)
- **개발 지침**:
    - 아웃바운드 포트 인터페이스를 구현합니다
    - JPA 엔티티는 `{도메인}JpaEntity` 형식으로 명명합니다
    - 엔티티와 도메인 객체 간 변환 메서드를 구현합니다
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

### 인수 테스트 (Acceptance Test)

- **위치**: `src/test/java/{도메인 패키지}/adapter/in/`
- **명명 규칙**: `{컨트롤러}Test`
- **개발 지침**:
    - RestAssured와 MockMvc를 조합하여 End-to-End 테스트를 작성합니다
    - 성공/실패 케이스를 모두 테스트합니다
    - `@SpringBootTest` 어노테이션으로 통합 테스트 환경을 설정합니다
    - 필요한 빈은 `@MockitoBean`으로 모킹합니다
    - 테스트 사용자 인증은 `@FTMockUser` 커스텀 어노테이션을 사용합니다

### REST Docs 테스트

- **위치**: `src/test/java/{도메인 패키지}/docs/`
- **명명 규칙**: `{도메인}DocsTest`
- **개발 지침**:
    - Spring REST Docs를 사용하여 API 문서를 자동 생성합니다
    - 문서화 대상 엔드포인트별로 테스트 메서드를 작성합니다
    - 요청/응답 필드를 명시적으로 문서화합니다
    - API에서 발생 가능한 모든 예외 코드를 문서화합니다
    - `ApiDocsTestBase` 클래스를 상속받아 문서화 환경을 구성합니다

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
- Lombok을 사용하여 보일러플레이트 코드를 최소화합니다
- 불변 객체와 함수형 프로그래밍 스타일을 권장합니다
- 메서드 매개변수는 `final` 키워드를 사용합니다
- 모든 클래스와 메서드에 JavaDoc을 작성합니다
- 상수는 대문자 스네이크 케이스(UPPER_SNAKE_CASE)로 명명합니다
- 도메인 객체의 유효성 검증은 생성자 또는 팩토리 메서드에서 수행합니다

## 개발 검증 체크리스트

코드 제출 전 다음 항목을 확인하세요:

1. [ ] 도메인 로직이 도메인 객체 내에 적절히 캡슐화되었습니까?
2. [ ] 포트와 어댑터가 명확하게 분리되어 있습니까?
3. [ ] 모든 클래스가 단일 책임 원칙을 준수합니까?
4. [ ] 예외는 적절한 ExceptionCode로 처리됩니까?
5. [ ] 단위 테스트와 인수 테스트가 작성되었습니까?
6. [ ] API 문서화가 REST Docs를 통해 수행되었습니까?
7. [ ] 명명 규칙이 일관되게 적용되었습니까?

이 지침을 따르면 헥사고날 아키텍처 기반의 견고하고 유지보수가 용이한 백엔드 시스템을 구축할 수 있습니다.
