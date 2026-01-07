# 프레젠테이션 계층 TDD 사이클

## TDD 순서

```
1. 인수 테스트 TDD (Outside-In)
   └─ Red → Green → Refactor

2. API 문서 테스트
   └─ 성공/실패 케이스 문서화
```

## 특이사항

- **Outside-In TDD**: 인수 테스트부터 시작
- **베이스 클래스**: `AcceptanceTestBase` / `ApiDocsTestBase` 상속
- **보안**: `@WithMockOAuth2User` 필수
- **CSRF**: POST/PUT/DELETE에 `.postProcessors(csrf())` 필수

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

## 인수 테스트 TDD

### Red (테스트 먼저)

```java
@DisplayName("[Acceptance Test] FindFamilyControllerTest")
class FindFamilyAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하는 ID로 가족 조회 시 200 OK와 가족 정보를 반환합니다")
    void find_returns_200_and_family_when_exists() {
        // given
        Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedEntity.getId();

        // when & then - Controller가 없어 404 또는 에러 발생
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

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 ID로 조회 시 404 Not Found를 반환합니다")
    void find_returns_404_when_not_exists() {
        // given
        Long nonExistentId = 999L;

        // when & then
        given()
            .when()
            .get("/api/families/{id}", nonExistentId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
```

### Green (최소 구현)

1. Response DTO 정의:

```java
public record FindFamilyResponse(
    Long id,
    String name,
    String description,
    String profileUrl,
    LocalDateTime createdAt
) {
    public static FindFamilyResponse from(Family family) {
        return new FindFamilyResponse(
            family.getId(),
            family.getName(),
            family.getDescription(),
            family.getProfileUrl(),
            family.getCreatedAt()
        );
    }
}
```

2. Controller 구현:

```java
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class FindFamilyController {

    private final FindFamilyUseCase findFamilyUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FindFamilyResponse>> find(@PathVariable Long id) {
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(id);
        Family family = findFamilyUseCase.find(query);
        FindFamilyResponse response = FindFamilyResponse.from(family);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
```

### Refactor (개선)

- 예외 케이스 테스트 추가
- 응답 형식 통일
- 코드 정리

## POST/PUT/DELETE 인수 테스트

### CSRF 처리 필수

```java
@Test
@WithMockOAuth2User
@DisplayName("Family 등록 시 201 Created를 반환합니다")
void save_returns_201_when_success() {
    // given
    String requestBody = """
        {
            "name": "새 가족",
            "description": "설명",
            "profileUrl": "http://example.com/profile.jpg"
        }
        """;

    // when & then
    given()
        .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())  // CSRF 필수
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post("/api/families")
        .then()
        .statusCode(HttpStatus.CREATED.value());
}
```

## API 문서 테스트

### 성공 케이스 문서화

```java
@DisplayName("[Docs Test] FindFamilyControllerDocsTest")
class FindFamilyDocsTest extends ApiDocsTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Test
    @WithMockOAuth2User
    @DisplayName("ID로 Family 조회 API 문서화")
    void document_find_family_by_id() {
        // given
        Family family = Family.newFamily("가족이름", "설명", "프로필URL", 1L);
        FamilyJpaEntity savedEntity = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedEntity.getId();

        // when & then
        given()
            .when()
            .get("/api/families/{id}", familyId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply(document("find-family",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("조회할 Family의 ID")
                ),
                responseFields(
                    fieldWithPath("data.id").description("Family ID"),
                    fieldWithPath("data.name").description("Family 이름"),
                    fieldWithPath("data.description").description("Family 설명"),
                    fieldWithPath("data.profileUrl").description("프로필 이미지 URL"),
                    fieldWithPath("data.createdAt").description("생성일시")
                )
            ));
    }
}
```

### 에러 케이스 문서화

```java
@Test
@WithMockOAuth2User
@DisplayName("존재하지 않는 ID로 조회 시 404 에러 문서화")
void document_find_family_not_found() {
    // given
    Long nonExistentId = 999L;

    // when & then
    given()
        .when()
        .get("/api/families/{id}", nonExistentId)
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .apply(document("find-family-not-found",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("id").description("조회할 Family의 ID")
            ),
            responseFields(
                fieldWithPath("error.code").description("에러 코드"),
                fieldWithPath("error.message").description("에러 메시지")
            )
        ));
}
```

## 문서화 필수 요소

### 공통 설정
```java
preprocessRequest(prettyPrint()),
preprocessResponse(prettyPrint()),
```

### Path Variable (있는 경우)
```java
pathParameters(
    parameterWithName("id").description("조회할 Family의 ID")
)
```

### Query Parameter (있는 경우)
```java
queryParameters(
    parameterWithName("page").description("페이지 번호 (0부터 시작)"),
    parameterWithName("size").description("페이지 크기")
)
```

### Request Body (있는 경우)
```java
requestFields(
    fieldWithPath("name").description("Family 이름"),
    fieldWithPath("description").description("Family 설명").optional()
)
```

### Response Body (모든 필드 문서화)
```java
responseFields(
    fieldWithPath("data.id").description("Family ID"),
    fieldWithPath("data.name").description("Family 이름")
)
```

## document 명명 규칙

| 케이스 | document 이름 |
|-------|--------------|
| 성공 | `find-family` |
| 리소스 없음 | `find-family-not-found` |
| Path Variable 검증 실패 | `find-family-invalid-path-variable` |
| Request Parameter 검증 실패 | `find-family-invalid-request-param` |
| Request DTO 검증 실패 | `find-family-invalid-request` |

## TDD 체크리스트

### Red 단계
- [ ] 인수 테스트 먼저 작성했는가?
- [ ] `@WithMockOAuth2User` 추가했는가?
- [ ] 테스트 실행하여 의도한 실패를 확인했는가?

### Green 단계
- [ ] Controller가 올바르게 구현되었는가?
- [ ] DTO가 올바르게 정의되었는가?
- [ ] 테스트가 통과하는가?

### Refactor 단계
- [ ] 예외 케이스 테스트를 추가했는가?
- [ ] API 문서 테스트를 작성했는가?
- [ ] 모든 응답 필드가 문서화되었는가?

## 테스트 실행

```bash
# 전체 인수 테스트
./gradlew test --tests "*AcceptanceTest"

# 전체 문서 테스트
./gradlew test --tests "*DocsTest"

# 문서 생성
./gradlew asciidoctor
```

## 금지 사항

- `@BeforeEach`에서 테스트 데이터 생성 금지
- POST/PUT/DELETE에서 CSRF 누락 금지
- `@WithMockOAuth2User` 누락 금지
- 응답 필드 문서화 누락 금지
