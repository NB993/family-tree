# 프레젠테이션 계층 코딩 스타일

## Request/Response DTO

### record 타입 필수
```java
// ✅ 올바른 방식
public record SaveFamilyRequest(
    @NotBlank String name,
    String description
) {}

// ❌ 금지: class 사용
public class SaveFamilyRequest {
    private String name;
    // getter/setter...
}
```

### 유효성 검증 어노테이션
```java
public record SaveFamilyRequest(
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    String name,

    @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
    String description,

    @Pattern(regexp = "^https?://.*", message = "유효한 URL 형식이어야 합니다")
    String profileUrl
) {}
```

## Controller 작성

### 기본 구조
```java
@RequiredArgsConstructor
@RequestMapping("/api/families")
@RestController
public class SaveFamilyController {

    private final SaveFamilyUseCase saveFamilyUseCase;

    /**
     * 새로운 Family를 등록합니다.
     *
     * @param request Family 등록 요청 데이터
     * @return 생성된 Family의 ID
     */
    @PostMapping
    public ResponseEntity<Long> save(
        @Valid @RequestBody SaveFamilyRequest request
    ) {
        // 1. Command 객체 생성
        SaveFamilyCommand command = new SaveFamilyCommand(
            request.name(),
            request.description(),
            request.profileUrl()
        );

        // 2. 유스케이스 실행
        Long savedId = saveFamilyUseCase.save(command);

        // 3. 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(savedId);
    }
}
```

### 응답 상태 코드
| 동작 | 성공 상태 코드 |
|------|---------------|
| 조회 | `200 OK` |
| 등록 | `201 CREATED` |
| 수정 | `200 OK` 또는 `204 NO_CONTENT` |
| 삭제 | `204 NO_CONTENT` |

## 금지 사항

### Controller에서 금지
```java
// ❌ 비즈니스 로직 금지
@GetMapping("/{id}")
public ResponseEntity<FindFamilyResponse> find(@PathVariable Long id) {
    if (id <= 0) {
        throw new IllegalArgumentException("ID는 양수여야 합니다");  // 금지
    }
    // ...
}

// ❌ 직접 Repository 호출 금지
@Autowired
private FamilyJpaRepository familyRepository;  // 금지

// ❌ 트랜잭션 관리 금지
@Transactional  // Controller에서 금지
public ResponseEntity<Long> save(...) { }
```

## PathVariable, RequestParam 처리

```java
// PathVariable
@GetMapping("/{familyId}/members/{memberId}")
public ResponseEntity<FindFamilyMemberResponse> find(
    @PathVariable Long familyId,
    @PathVariable Long memberId
) { }

// RequestParam
@GetMapping
public ResponseEntity<List<FindFamilyResponse>> findAll(
    @RequestParam(required = false) String status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) { }
```

## Response 변환

### 도메인 → Response DTO
```java
Family family = findFamilyUseCase.find(query);

FindFamilyResponse response = new FindFamilyResponse(
    family.getId(),
    family.getName(),
    family.getDescription(),
    family.getProfileUrl(),
    family.getCreatedAt()
);

return ResponseEntity.ok(response);
```

### 목록 조회
```java
List<Family> families = findFamilyUseCase.findAll(query);

List<FindFamilyResponse> responses = families.stream()
    .map(f -> new FindFamilyResponse(
        f.getId(),
        f.getName(),
        f.getDescription(),
        f.getProfileUrl(),
        f.getCreatedAt()
    ))
    .toList();

return ResponseEntity.ok(responses);
```