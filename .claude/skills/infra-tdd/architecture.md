# 인프라 계층 아키텍처

## 아웃바운드 어댑터 계층

**위치**: `core/{도메인명}/adapter/out/`

### 지침
- 아웃바운드 포트 인터페이스 구현
- 도메인 객체를 전달받아 엔티티로 변환 후 작업
- 조회한 엔티티는 도메인 객체로 변환하여 응답
- save, modify 메서드는 ID(Long) 반환

### 예시: Adapter 클래스
```java
@Component
@RequiredArgsConstructor
public class FamilyAdapter implements FindFamilyPort, SaveFamilyPort {

    private final FamilyJpaRepository familyJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Family> find(Long id) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");

        return familyJpaRepository.findById(id)
            .map(FamilyJpaEntity::toFamily);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(Family family) {
        Objects.requireNonNull(family, "family는 null일 수 없습니다");

        FamilyJpaEntity entity = FamilyJpaEntity.from(family);
        FamilyJpaEntity saved = familyJpaRepository.save(entity);
        return saved.getId();
    }
}
```

## JPA 엔티티 설계

### 기본 원칙
- 기본 생성자: `protected` (JPA 필수)
- 그 외 생성자: `private`
- setter 사용 금지
- `from()` 정적 팩토리 메서드로 생성

### 예시: JpaEntity 클래스
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

    private FamilyJpaEntity(
        Long id,
        String name,
        String description,
        String profileUrl,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
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

## JPA Repository

### 예시
```java
public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {
    // 메서드 이름 기반 쿼리 우선 사용
    Optional<FamilyJpaEntity> findByName(String name);
    List<FamilyJpaEntity> findByCreatedBy(Long userId);
}
```

## 성능 최적화

### N+1 문제 방지
```java
@Query("SELECT f FROM family f JOIN FETCH f.members WHERE f.id = :id")
Optional<FamilyJpaEntity> findByIdWithMembers(@Param("id") Long id);

@EntityGraph(attributePaths = {"members"})
Optional<FamilyJpaEntity> findById(Long id);
```

### 지연 로딩 기본
```java
@OneToMany(mappedBy = "family", cascade = CascadeType.ALL,
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<FamilyMemberJpaEntity> members = new ArrayList<>();
```

### 벌크 연산
```java
@Modifying
@Query("UPDATE family f SET f.name = :name WHERE f.id = :id")
int updateName(@Param("id") Long id, @Param("name") String name);
```