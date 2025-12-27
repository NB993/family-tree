# 프레젠테이션 계층 명명 규칙

## 클래스 명명

| 계층 | 패턴 | 예시 |
|-----|-----|------|
| Controller | `{Find/Save/Modify/Delete}{도메인}Controller` | `FindFamilyController` |
| Request DTO | `{Find/Save/Modify/Delete}{도메인}Request` | `SaveFamilyRequest` |
| Response DTO | `{Find/Save/Modify/Delete}{도메인}Response` | `FindFamilyResponse` |

## 잘못된 예시

```java
// ❌ 금지
GetFamilyController      // Get 대신 Find
CreateFamilyRequest      // Create 대신 Save
UpdateFamilyRequest      // Update 대신 Modify
FamilyDTO                // DTO 접미사 금지
```

## Request/Response DTO

### record 타입 필수
```java
// Request DTO
public record SaveFamilyRequest(
    @NotBlank String name,
    String description,
    String profileUrl
) {}

// Response DTO
public record FindFamilyResponse(
    Long id,
    String name,
    String description,
    String profileUrl,
    LocalDateTime createdAt
) {}
```

## API 엔드포인트 패턴

| HTTP Method | 동작 | URL 패턴 | 예시 |
|-------------|------|---------|------|
| GET | 조회 | `/api/{도메인s}/{id}` | `GET /api/families/1` |
| GET | 목록 조회 | `/api/{도메인s}` | `GET /api/families` |
| POST | 등록 | `/api/{도메인s}` | `POST /api/families` |
| PUT | 수정 | `/api/{도메인s}/{id}` | `PUT /api/families/1` |
| DELETE | 삭제 | `/api/{도메인s}/{id}` | `DELETE /api/families/1` |

## 메서드 명명

Controller 메서드명은 동작을 명확히 표현:

```java
// 조회
public ResponseEntity<FindFamilyResponse> find(@PathVariable Long id)

// 목록 조회
public ResponseEntity<List<FindFamilyResponse>> findAll()

// 등록
public ResponseEntity<Long> save(@Valid @RequestBody SaveFamilyRequest request)

// 수정
public ResponseEntity<Void> modify(@PathVariable Long id, @Valid @RequestBody ModifyFamilyRequest request)

// 삭제
public ResponseEntity<Void> delete(@PathVariable Long id)
```