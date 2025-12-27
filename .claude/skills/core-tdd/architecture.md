# 코어 계층 아키텍처

## 도메인 모델 계층

**위치**: `core/{도메인명}/domain/`

### 지침
- 모든 도메인 객체는 불변성 유지
- 비즈니스 로직은 도메인 객체 내부에 캡슐화
- 생성자는 private, 정적 팩토리 메서드 활용
- 모든 필드는 final로 선언

### 예시: Domain 객체
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
        if (name.isBlank()) {
            throw new IllegalArgumentException("name은 비어있을 수 없습니다");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static Family newFamily(String name, String description,
                                    String profileUrl, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return new Family(null, name, description, profileUrl, userId, now);
    }

    public static Family withId(Long id, String name, String description,
                                 String profileUrl, Long createdBy, LocalDateTime createdAt) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        return new Family(id, name, description, profileUrl, createdBy, createdAt);
    }
}
```

## 인바운드 포트 계층

**위치**: `core/{도메인명}/application/port/in/`

### 지침
- 각 유스케이스는 단일 메서드를 가진 인터페이스로 정의
- 입력 파라미터는 Command 또는 Query 객체(record)로 캡슐화
- 반환 타입: 조회는 도메인 객체, 생성/수정은 ID(Long)
- Optional 타입 리턴 금지 (분기 처리는 Service에서)

### 예시: UseCase 인터페이스
```java
public interface FindFamilyUseCase {
    /**
     * 지정된 ID의 Family를 조회합니다.
     *
     * @param query Family 조회 쿼리 객체
     * @return 조회된 Family 객체
     * @throws FTException 해당 ID의 Family가 존재하지 않을 경우
     */
    Family find(FindFamilyByIdQuery query);
}
```

### 예시: Query 객체 (record)
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

## 아웃바운드 포트 계층

**위치**: `core/{도메인명}/application/port/out/`

### 지침
- 인터페이스는 최소한의 기능만 정의
- 도메인 객체 타입을 파라미터와 반환 타입으로 사용
- save, modify 메서드는 ID(Long) 반환

### 예시: Port 인터페이스
```java
public interface FindFamilyPort {
    Optional<Family> find(Long id);
}

public interface SaveFamilyPort {
    Long save(Family family);
}
```

## 애플리케이션 서비스 계층

**위치**: `core/{도메인명}/application/service/`

### 지침
- 각 서비스는 단일 유스케이스 인터페이스 구현
- **모든 핵심 비즈니스 로직은 도메인 객체에 위임**
- 서비스는 트랜잭션 관리, 외부 시스템 호출, 흐름 제어만 담당
- public 메서드는 15~20줄을 넘지 않도록 유지
- 예외는 유스케이스에서 발생 (어댑터에서 예외 발생 금지)
- `{@inheritDoc}` 주석 사용

### 예시: Service 클래스
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
    public Family find(FindFamilyByIdQuery query) {
        Objects.requireNonNull(query, "query는 null일 수 없습니다");

        return findFamilyPort.find(query.id())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
    }
}
```