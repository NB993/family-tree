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
- 요청 Body 데이터는 Multiline Strings(`"""`)를 이용
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
- 요청 Body 데이터는 Multiline Strings(`"""`)를 이용
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
