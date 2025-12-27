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

## 객체지향 설계 원칙 (OOP Design Principles)

모든 코드는 단순히 구조를 따르는 것을 넘어, 객체지향의 이점을 최대한 활용해야 합니다. 아래 원칙들을 반드시 준수하여 코드를 작성하세요.

### 1. Tell, Don't Ask (묻지 말고 시켜라)

서비스 계층이나 다른 객체에서 `getter`로 상태를 가져와 `if/else`로 분기하는 로직을 작성하지 마세요. 대신, 해당 데이터를 가진 객체에게 작업을 **시키세요(Tell)**. 이는 객체의 캡슐화를 강화하고 응집도를 높입니다.

**❌ 잘못된 예시 (Asking)**
```java
// Service Layer
FamilyMember member = memberPort.find(memberId);
if (member.getRole() == Role.ADMIN || member.isOwner()) {
    // 관리자일 때의 로직...
}

List<Member> members = family.getMembers();
if (members.size() >= 5) {
    throw new FamilyCapacityExceededException();
}
```

**✅ 올바른 예시 (Telling)**
```java
// Service Layer - 객체에게 메시지를 보낸다
FamilyMember member = memberPort.find(memberId);
member.doAdminAction(); // member 객체 스스로 역할을 확인하고 행동

Family family = familyPort.find(familyId);
family.addMember(newMember); // family 객체 스스로 정원 초과 여부를 확인
```

### 2. 도메인 객체는 똑똑하게 (Rich Domain Model)

도메인 객체(`@Entity`가 아님)는 단순한 데이터 덩어리(Anemic Domain Model)가 되어서는 안 됩니다. 도메인 객체는 관련된 비즈니스 로직과 규칙을 **스스로 포함**해야 합니다.

**❌ Anemic Domain Model (사용 금지)**
```java
public class Family {
    private Long id;
    private List<Member> members;

    // getter/setter만 존재
}

// Service에서 모든 로직 처리
public class FamilyService {
    public void addMember(Family family, Member newMember) {
        if (family.getMembers().size() >= 5) { // 서비스가 규칙을 판단
            throw new RuntimeException("정원 초과");
        }
        // ...
    }
}
```

**✅ Rich Domain Model (권장)**
```java
public class Family {
    private Long id;
    private List<Member> members;
    private static final int MAX_MEMBERS = 5;

    // Family 객체가 스스로 규칙을 검증하는 '행동'을 가짐
    public void addMember(Member newMember) {
        if (this.members.size() >= MAX_MEMBERS) {
            throw new FamilyCapacityExceededException();
        }
        validateNewMember(newMember);
        this.members.add(newMember);
    }
}

// Service는 오케스트레이션만 담당
public class FamilyService {
    public void joinFamily(Long familyId, Member newMember) {
        Family family = familyPort.find(familyId);
        family.addMember(newMember); // Family에게 작업을 위임
        familyPort.save(family);
    }
}
```

### 3. 서비스 메서드는 짧고 간결하게

서비스(`@Service`)의 역할은 **지휘자(Orchestrator)**입니다. 복잡한 비즈니스 로직을 직접 구현하는 대신, 여러 **똑똑한 도메인 객체(Rich Domain Objects)**들을 조율하여 유스케이스를 완성해야 합니다.

- **규칙**: 서비스의 public 메서드는 15~20줄을 넘지 않도록 작성하세요.
- **리팩토링 신호**: 메서드가 길어지거나, 3개 이상의 다른 Port/Service에 의존한다면, 책임을 분리하여 새로운 도메인 서비스나 도메인 객체의 메서드로 로직을 이전해야 합니다.

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
