# 테스트 코드 작성 가이드라인

## 테스트 실행 방법

### IDE Run Configuration 사용 (필수)
- **필수 규칙**: gradle 명령어가 아닌 IDE의 run configuration을 사용하여 테스트 실행
- **이유**: 
  - 프로젝트 지침 준수
  - 일관된 테스트 환경 유지
  - 토큰 사용량 효율성 (gradle 출력 로그 최소화)
- **방법**: 
  1. `get_run_configurations` 도구로 사용 가능한 configuration 확인
  2. `run_configuration` 도구로 적절한 configuration 실행
- **금지사항**: 
  - `./gradlew test` 등의 gradle 명령어 직접 사용 금지
  - `execute_terminal_command`로 gradle 테스트 실행 금지

### 테스트 실행 절차
1. 먼저 `get_run_configurations`로 사용 가능한 설정 확인
2. 적절한 run configuration 선택하여 실행
3. 전체 테스트 실행 시: "Tests in '{$root}.test'" 사용
4. 특정 테스트만 실행하고 싶은 경우 별도 configuration이 있는지 확인

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
- DB 데이터 생성을 위해 도메인JpaEntity 생성 시 기본생성자 사용 금지
- 도메인 엔티티의 신규 엔티티 생성용 정적 메서드를 이용하여 도메인 엔티티를 생성한 뒤 도메인JpaEntity.from 메서드를 호출하여 생성
- 절대 `@BeforeEach`에서 테스트용 데이터 생성 금지. 각 테스트 메서드의 given 영역에서 데이터 생성
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
- DB 데이터 생성을 위해 도메인JpaEntity 생성 시 기본생성자 사용 금지
- 도메인 엔티티의 신규 엔티티 생성용 정적 메서드를 이용하여 도메인 엔티티를 생성한 뒤 도메인JpaEntity.from 메서드를 호출하여 생성
- 절대 `@BeforeEach`에서 테스트용 데이터 생성 금지. 각 테스트 메서드의 given 영역에서 데이터 생성

#### RestAssuredMockMvc + REST Docs 전용 규칙
- `given()` → `when()` → `then()` → `apply(document(...))` 패턴 사용
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
