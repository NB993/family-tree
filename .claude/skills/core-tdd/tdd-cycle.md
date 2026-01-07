# 코어 계층 TDD 사이클

## TDD 순서

```
1. Command/Query TDD
   └─ Red → Green → Refactor

2. Domain TDD
   └─ Red → Green → Refactor

3. UseCase 인터페이스 정의

4. Service TDD
   └─ Red → Green → Refactor
```

## Command/Query TDD

### Red (테스트 먼저)

```java
@DisplayName("[Unit Test] FindFamilyByIdQueryTest")
class FindFamilyByIdQueryTest {

    @Test
    @DisplayName("ID가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_id_is_null() {
        // 이 테스트가 실패하도록 Query 클래스는 아직 존재하지 않음
        assertThatThrownBy(() -> new FindFamilyByIdQuery(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("id는 null일 수 없습니다");
    }

    @Test
    @DisplayName("ID가 0 이하인 경우 IllegalArgumentException이 발생합니다")
    void throw_exception_when_id_is_not_positive() {
        assertThatThrownBy(() -> new FindFamilyByIdQuery(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id는 0보다 커야 합니다");
    }

    @Test
    @DisplayName("유효한 ID로 쿼리 객체가 생성됩니다")
    void create_query_when_id_is_valid() {
        // given
        Long validId = 1L;

        // when
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(validId);

        // then
        assertThat(query.id()).isEqualTo(validId);
    }
}
```

### Green (최소 구현)

테스트 실행 후 컴파일 에러 해결:

```java
public record FindFamilyByIdQuery(Long id) {
    public FindFamilyByIdQuery {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        if (id <= 0) {
            throw new IllegalArgumentException("id는 0보다 커야 합니다");
        }
    }
}
```

### Refactor (개선)

- 추가 유효성 검증이 필요하면 테스트 추가
- 코드 정리

## Domain TDD

### Red (테스트 먼저)

```java
@DisplayName("[Unit Test] FamilyTest")
class FamilyTest {

    @Test
    @DisplayName("newFamily로 새로운 Family가 생성됩니다")
    void create_new_family_successfully() {
        // given
        String name = "테스트 가족";
        String description = "설명";
        String profileUrl = "http://example.com/profile.jpg";
        Long userId = 1L;

        // when - Family 클래스가 없어 컴파일 에러 발생
        Family family = Family.newFamily(name, description, profileUrl, userId);

        // then
        assertThat(family.getId()).isNull();
        assertThat(family.getName()).isEqualTo(name);
        assertThat(family.getDescription()).isEqualTo(description);
        assertThat(family.getCreatedBy()).isEqualTo(userId);
    }

    @Test
    @DisplayName("withId로 기존 Family가 복원됩니다")
    void restore_family_with_id() {
        // given
        Long id = 1L;
        String name = "테스트 가족";
        LocalDateTime now = LocalDateTime.now();

        // when
        Family family = Family.withId(id, name, "설명", "프로필URL", 1L, now, 1L, now);

        // then
        assertThat(family.getId()).isEqualTo(id);
        assertThat(family.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("이름이 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_name_is_null() {
        assertThatThrownBy(() -> Family.newFamily(null, "설명", "프로필URL", 1L))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("name은 null일 수 없습니다");
    }
}
```

### Green (최소 구현)

```java
public final class Family {
    private final Long id;
    private final String name;
    private final String description;
    private final String profileUrl;
    private final Long createdBy;
    private final LocalDateTime createdAt;

    private Family(Long id, String name, String description, String profileUrl,
                   Long createdBy, LocalDateTime createdAt) {
        Objects.requireNonNull(name, "name은 null일 수 없습니다");
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static Family newFamily(String name, String description,
                                    String profileUrl, Long userId) {
        return new Family(null, name, description, profileUrl, userId, LocalDateTime.now());
    }

    public static Family withId(Long id, String name, String description,
                                 String profileUrl, Long createdBy, LocalDateTime createdAt,
                                 Long modifiedBy, LocalDateTime modifiedAt) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        return new Family(id, name, description, profileUrl, createdBy, createdAt);
    }

    // getter 메서드들
}
```

### Refactor (개선)

- 불변성 강화
- 비즈니스 메서드 추가
- 유효성 검증 보완

## Service TDD

### Red (테스트 먼저)

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
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(familyId);
        Family expectedFamily = Family.withId(
            familyId, "가족이름", "설명", "프로필URL",
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );

        // Mocking: 유효한 ID로 Family 조회
        when(findFamilyPort.find(familyId)).thenReturn(Optional.of(expectedFamily));

        // when - Service 클래스가 없어 컴파일 에러
        Family actualFamily = findFamilyService.find(query);

        // then
        assertThat(actualFamily).isEqualTo(expectedFamily);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 FTException이 발생합니다")
    void throw_exception_when_family_not_found() {
        // given
        Long familyId = 999L;
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(familyId);

        // Mocking: 존재하지 않는 Family
        when(findFamilyPort.find(familyId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findFamilyService.find(query))
            .isInstanceOf(FTException.class);
    }
}
```

### Green (최소 구현)

1. UseCase 인터페이스 정의:

```java
public interface FindFamilyUseCase {
    Family find(FindFamilyByIdQuery query);
}
```

2. Port 인터페이스 정의:

```java
public interface FindFamilyPort {
    Optional<Family> find(Long id);
}
```

3. Service 구현:

```java
@Service
@RequiredArgsConstructor
public class FindFamilyService implements FindFamilyUseCase {

    private final FindFamilyPort findFamilyPort;

    @Override
    @Transactional(readOnly = true)
    public Family find(FindFamilyByIdQuery query) {
        Objects.requireNonNull(query, "query는 null일 수 없습니다");

        return findFamilyPort.find(query.id())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
    }
}
```

### Refactor (개선)

- 트랜잭션 설정 확인
- JavaDoc 추가
- 코드 정리

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
User user = UserFixture.createUser();
FamilyMember member = FamilyMemberFixture.createMember(family, user);

// ❌ 잘못된 방식: Fixture 메서드 있는데 직접 생성
Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);
```

## 테스트 작성 규칙

### 클래스 선언

```java
@ExtendWith(MockitoExtension.class)  // Service 테스트
@DisplayName("[Unit Test] {클래스명}Test")
class FindFamilyServiceTest {
    // ...
}
```

### 메서드 명명

- **패턴**: `{행동}_{결과}_{조건}` (snake_case)
- **예시**:
  - `return_family_when_id_is_valid`
  - `throw_exception_when_id_is_null`
  - `save_success_when_user_has_admin_role`

### @DisplayName 작성

- **형식**: "{조건}일 때 {대상}은/는 {결과}합니다"
- **예시**: `@DisplayName("유효한 ID로 조회 시 Family 객체를 반환합니다")`

### Mocking 규칙

- 각 Mocking 코드에 **주석으로 의도 설명**
- Port 인터페이스만 Mocking (도메인 객체는 실제 사용)

```java
// Mocking: 유효한 ID로 Family 조회
when(findFamilyPort.find(familyId)).thenReturn(Optional.of(expectedFamily));
```

## TDD 체크리스트

### Red 단계
- [ ] 테스트 먼저 작성했는가?
- [ ] 컴파일 에러 해결을 위한 최소 stub만 생성했는가?
- [ ] 테스트 실행하여 의도한 실패를 확인했는가?

### Green 단계
- [ ] 테스트를 통과시키는 최소한의 코드인가?
- [ ] 명명 규칙을 준수했는가? (Find/Save/Modify/Delete)
- [ ] 정적 팩토리 메서드 패턴을 사용했는가? (newXxx/withId)

### Refactor 단계
- [ ] 모든 테스트가 통과하는가?
- [ ] 중복 코드가 있는가?
- [ ] 가독성을 개선할 수 있는가?

## 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests "FindFamilyServiceTest"

# 특정 테스트 메서드
./gradlew test --tests "FindFamilyServiceTest.return_family_when_id_is_valid"
```

## 금지 사항

- 여러 실패 테스트 동시 분석 금지
- 첫 번째 실패 테스트만 완전히 해결 후 다음으로 이동
- 에러 메시지 읽지 않고 추측으로 원인 찾기 금지
- 테스트 없이 프로덕션 코드 작성 금지