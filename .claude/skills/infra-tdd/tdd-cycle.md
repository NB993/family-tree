# 인프라 계층 TDD 사이클

## TDD 순서

```
1. JpaEntity TDD
   └─ 변환 메서드 테스트 → 구현 → 리팩토링

2. Repository 인터페이스 정의

3. Adapter TDD
   └─ Red → Green → Refactor
```

## 특이사항

- **Mocking 미사용**: 실제 DB(Testcontainer) 사용
- **베이스 클래스**: `AdapterTestBase` 상속
- **sut 패턴**: 테스트 대상을 `sut` 변수로 선언

## JpaEntity TDD

### Red (테스트 먼저)

```java
@DisplayName("[Unit Test] FamilyJpaEntityTest")
class FamilyJpaEntityTest {

    @Test
    @DisplayName("from 메서드로 도메인 객체에서 JPA 엔티티를 생성합니다")
    void from_creates_entity_from_domain() {
        // given
        Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);

        // when - JpaEntity 클래스가 없어 컴파일 에러
        FamilyJpaEntity entity = FamilyJpaEntity.from(family);

        // then
        assertThat(entity.getName()).isEqualTo(family.getName());
        assertThat(entity.getDescription()).isEqualTo(family.getDescription());
        assertThat(entity.getProfileUrl()).isEqualTo(family.getProfileUrl());
    }

    @Test
    @DisplayName("from 메서드에 null 전달 시 NullPointerException이 발생합니다")
    void from_throws_exception_when_null() {
        assertThatThrownBy(() -> FamilyJpaEntity.from(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("family는 null일 수 없습니다");
    }
}
```

### Green (최소 구현)

```java
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

    private FamilyJpaEntity(Long id, String name, String description, String profileUrl,
                            Long createdBy, LocalDateTime createdAt,
                            Long modifiedBy, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        // BaseEntity 필드 설정
    }

    public static FamilyJpaEntity from(Family family) {
        Objects.requireNonNull(family, "family는 null일 수 없습니다");
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

    public Family toFamily() {
        return Family.withId(
            id, name, description, profileUrl,
            getCreatedBy(), getCreatedAt(),
            getModifiedBy(), getModifiedAt()
        );
    }
}
```

### Refactor (개선)

- 컬럼 매핑 확인
- 관계 매핑 추가 (필요시)

## Adapter TDD

### Red (테스트 먼저)

```java
@DisplayName("[Unit Test] FamilyAdapterTest")
class FamilyAdapterTest extends AdapterTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    private FamilyAdapter sut;  // System Under Test

    @BeforeEach
    void setUp() {
        sut = new FamilyAdapter(familyJpaRepository);
    }

    @Test
    @DisplayName("ID로 조회 시 Family 객체를 반환합니다")
    void find_returns_family_when_exists() {
        // given - newXxx() + from() 패턴
        Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));

        // when - Adapter 클래스가 없어 컴파일 에러
        Optional<Family> result = sut.find(savedEntity.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getName()).isEqualTo(savedEntity.getName());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
    void find_returns_empty_when_not_exists() {
        // given
        Long nonExistentId = 999L;

        // when
        Optional<Family> result = sut.find(nonExistentId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Family 저장 시 ID를 반환합니다")
    void save_returns_id() {
        // given
        Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);

        // when
        Long savedId = sut.save(family);

        // then
        assertThat(savedId).isNotNull();
        assertThat(familyJpaRepository.findById(savedId)).isPresent();
    }
}
```

### Green (최소 구현)

1. Repository 인터페이스 정의:

```java
public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {
}
```

2. Adapter 구현:

```java
@Component
@RequiredArgsConstructor
public class FamilyAdapter implements FindFamilyPort, SaveFamilyPort {

    private final FamilyJpaRepository familyJpaRepository;

    @Override
    public Optional<Family> find(Long id) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        return familyJpaRepository.findById(id)
            .map(FamilyJpaEntity::toFamily);
    }

    @Override
    public Long save(Family family) {
        Objects.requireNonNull(family, "family는 null일 수 없습니다");
        FamilyJpaEntity entity = FamilyJpaEntity.from(family);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(entity);
        return savedEntity.getId();
    }
}
```

### Refactor (개선)

- 쿼리 최적화 (N+1 문제)
- 벌크 연산 추가 (필요시)

## Fixture 사용 규칙

### 테스트 Fixture 헬퍼 클래스 우선 사용

테스트 데이터 생성 시 **반드시 기존 Fixture 헬퍼 클래스를 먼저 확인**하고 사용합니다.

**Fixture 클래스 위치:**
- `be/src/test/java/io/jhchoe/familytree/test/fixture/`

**사용 순서:**
1. 해당 도메인의 Fixture 클래스가 있는지 확인 (예: `FamilyFixture`, `UserFixture`, `FamilyMemberFixture`)
2. 필요한 메서드가 있으면 해당 메서드 사용
3. 메서드가 없으면 사용자에게 기존 Fixture 클래스에 새 메서드 추가 문의
4. Fixture 클래스 자체가 없으면 사용자에게 새로 생성 문의

```java
// ✅ 올바른 방식: Fixture 클래스의 메서드 사용
Family family = FamilyFixture.createFamily();
FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));

// ❌ 잘못된 방식: Fixture 메서드 있는데 직접 생성
Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);
```

## 테스트 데이터 생성 규칙

### 올바른 방식: newXxx() + from() 패턴

```java
// ✅ 올바른 테스트 데이터 생성
@Test
void find_returns_family_when_exists() {
    // given - newXxx() + from() 패턴 사용
    Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);
    FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));

    // when & then
    // ...
}
```

### 금지된 방식

```java
// ❌ 잘못된 방식 (기본 생성자 직접 사용)
FamilyJpaEntity entity = new FamilyJpaEntity();
entity.setName("가족이름");  // 컴파일 에러 - setter 없음

// ❌ 잘못된 방식 (JpaEntity 생성자 직접 호출)
FamilyJpaEntity entity = new FamilyJpaEntity(
    null, "가족이름", "설명", "프로필URL", 1L, now(), 1L, now()
);  // 컴파일 에러 - private 생성자
```

## 테스트 작성 규칙

### 클래스 선언

```java
@DisplayName("[Unit Test] {클래스명}Test")
class FamilyAdapterTest extends AdapterTestBase {
    // AdapterTestBase가 테스트 환경 설정 제공
}
```

### sut 변수명 사용

```java
private FamilyAdapter sut;  // System Under Test

// sut로 테스트 대상임을 명확히 표시
Optional<Family> result = sut.find(id);
```

### @BeforeEach에서 데이터 생성 금지

```java
// ❌ 금지: @BeforeEach에서 테스트 데이터 생성
@BeforeEach
void setUp() {
    sut = new FamilyAdapter(familyJpaRepository);
    // testFamily = Family.newFamily(...);  // 금지!
}

// ✅ 권장: 각 테스트 메서드의 given에서 데이터 생성
@Test
void test() {
    // given
    Family family = Family.newFamily(...);  // 여기서 생성
    // ...
}
```

## TDD 체크리스트

### Red 단계
- [ ] 테스트 먼저 작성했는가?
- [ ] 컴파일 에러 해결을 위한 최소 stub만 생성했는가?
- [ ] 테스트 실행하여 의도한 실패를 확인했는가?

### Green 단계
- [ ] 테스트를 통과시키는 최소한의 코드인가?
- [ ] 변환 메서드 (from/toXxx)를 올바르게 구현했는가?
- [ ] Port 인터페이스를 올바르게 구현했는가?

### Refactor 단계
- [ ] 모든 테스트가 통과하는가?
- [ ] N+1 문제가 없는가?
- [ ] 쿼리 최적화가 필요한가?

## 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 Adapter 테스트
./gradlew test --tests "FamilyAdapterTest"

# 특정 메서드 테스트
./gradlew test --tests "FamilyAdapterTest.find_returns_family_when_exists"
```

## 금지 사항

- Mocking 사용 금지 (실제 DB 사용)
- @BeforeEach에서 테스트 데이터 생성 금지
- JpaEntity setter 사용 금지
- JpaEntity 생성자 직접 호출 금지