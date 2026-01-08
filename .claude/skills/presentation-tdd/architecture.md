# 프레젠테이션 계층 아키텍처

## 인바운드 어댑터 계층

**위치**: `core/{도메인명}/adapter/in/`

### 지침
- 모든 API 엔드포인트는 `/api/`로 시작
- **하나의 Controller에는 반드시 하나의 API만 작성**
- 컨트롤러는 Request DTO → Command/Query 변환만 담당
- 비즈니스 로직 처리나 예외 발생은 Service 계층 책임
- 응답은 `ResponseEntity<T>` 사용

### 예시: Controller 클래스
```java
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
        // 1. 쿼리 객체 생성
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(id);

        // 2. 유스케이스 실행
        Family family = findFamilyUseCase.find(query);

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

## DTO 설계

### Request DTO (record)
```java
public record SaveFamilyRequest(
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    String name,

    @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
    String description,

    String profileUrl
) {}
```

### Response DTO (record)
```java
public record FindFamilyResponse(
    Long id,
    String name,
    String description,
    String profileUrl,
    LocalDateTime createdAt
) {}
```

## Controller 역할 정의

### Controller의 책임
1. Request DTO 수신
2. Command/Query 객체로 변환
3. UseCase 호출
4. 도메인 객체 → Response DTO 변환
5. ResponseEntity 반환

### Controller가 하지 않는 것
- 비즈니스 로직 처리
- 예외 발생 (검증 제외)
- 직접 Repository 호출
- 직접 Port(아웃바운드 포트) 호출 ← **UseCase만 의존**
- 트랜잭션 관리

## 어노테이션 사용

```java
@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class SaveFamilyController {

    private final SaveFamilyUseCase saveFamilyUseCase;

    @PostMapping
    public ResponseEntity<Long> save(
        @Valid @RequestBody SaveFamilyRequest request
    ) {
        // ...
    }
}
```