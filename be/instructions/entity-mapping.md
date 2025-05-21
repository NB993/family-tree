# 엔티티 매핑 및 변환 가이드라인

## JPA 엔티티 설계 원칙

- JPA 엔티티는 `{도메인}JpaEntity` 형식으로 명명합니다
- 모든 JPA 엔티티는 기본 생성자를 `protected` 접근 제한자로 선언합니다
- 엔티티 필드는 직접 접근하지 않고 Getter를 통해 접근합니다
- `@Table`, `@Column` 등의 어노테이션을 명시하여 데이터베이스 스키마와 매핑합니다

## JPA 엔티티 작성 예시

```java
package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.Family;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Family 엔티티를 DB에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
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

    /**
     * FamilyJpaEntity 객체를 생성하는 생성자입니다.
     */
    public FamilyJpaEntity(
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

    /**
     * Family 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param family 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyJpaEntity from(Family family) {
        Objects.requireNonNull(family, "family must not be null");
        
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

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public Family toFamily() {
        return Family.withId(
            id,
            name,
            description,
            profileUrl,
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
```

## JPA Repository 작성 예시

```java
package io.jhchoe.familytree.core.family.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Family JPA 엔티티에 대한 리포지토리 인터페이스입니다.
 */
public interface FamilyJpaRepository extends JpaRepository<FamilyJpaEntity, Long> {
}
```

## 엔티티-도메인 변환 규칙

### 변환 책임
- 엔티티→도메인 변환: JpaEntity 클래스의 `toXxx()` 메서드가 담당합니다
- 도메인→엔티티 변환: JpaEntity 클래스의 정적 `from(xxx)` 메서드가 담당합니다

### 변환 메서드 시그니처
```java
// 도메인 → 엔티티 변환
public static XxxJpaEntity from(Xxx domainObject) {
    Objects.requireNonNull(domainObject, "domainObject must not be null");
    // 변환 로직
    return new XxxJpaEntity(...);
}

// 엔티티 → 도메인 변환
public Xxx toXxx() {
    // 변환 로직
    return Xxx.withId(...);
}
```

## 컬렉션 처리

### 일대다, 다대다 관계 매핑
- 컬렉션은 항상 방어적 복사를 수행합니다
- 빈 컬렉션은 `Collections.emptyList()`로 반환합니다 (null 반환 금지)
- 양방향 관계에서는 일관성을 유지하기 위한 헬퍼 메서드를 제공합니다
- `@OneToMany`, `@ManyToMany` 관계에서는 지연 로딩(`fetch = FetchType.LAZY`)을 기본으로 합니다

```java
@OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<FamilyMemberJpaEntity> members = new ArrayList<>();

// 양방향 관계 헬퍼 메서드
public void addMember(FamilyMemberJpaEntity member) {
    this.members.add(member);
    member.setFamily(this);
}

public void removeMember(FamilyMemberJpaEntity member) {
    this.members.remove(member);
    member.setFamily(null);
}
```

### 순환 참조 처리
- 양방향 관계에서는 한쪽에서만 변환을 수행합니다
- 필요한 경우 ID만 참조하는 가벼운 객체를 사용합니다
- JSON 직렬화 시 `@JsonIgnore` 또는 DTO 변환을 통해 순환 참조를 방지합니다

## 데이터베이스 특화 코드

- 성능 최적화가 필요한 쿼리는 `@Query` 어노테이션을 사용하여 JPQL 또는 네이티브 쿼리로 작성합니다
- 대용량 데이터 처리가 필요한 경우 페이징 처리를 적용합니다
- 복잡한 쿼리는 Querydsl을 사용하여 타입-세이프하게 작성합니다

```java
// JPQL 사용 예시
@Query("SELECT f FROM family f WHERE f.name LIKE %:keyword%")
List<FamilyJpaEntity> findByNameContaining(@Param("keyword") String keyword);

// 페이징 처리 예시
Page<FamilyJpaEntity> findAll(Pageable pageable);
```

## 테스트 시 엔티티 생성

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

## 영속성 관련 주의 사항

### N+1 문제 방지
- 연관 엔티티를 조회할 때 N+1 문제를 방지하기 위해 페치 조인을 사용합니다
- 필요한 경우 `@EntityGraph`를 활용합니다

```java
@Query("SELECT f FROM family f JOIN FETCH f.members WHERE f.id = :id")
Optional<FamilyJpaEntity> findByIdWithMembers(@Param("id") Long id);

@EntityGraph(attributePaths = {"members"})
Optional<FamilyJpaEntity> findById(Long id);
```

### 대량 데이터 처리
- 대량의 데이터를 처리할 때는 벌크 연산을 활용합니다
- 페이징 처리 시 카운트 쿼리를 최적화합니다

```java
@Modifying
@Query("UPDATE family f SET f.name = :name WHERE f.id = :id")
int updateName(@Param("id") Long id, @Param("name") String name);

@Query(value = "SELECT f FROM family f",
       countQuery = "SELECT COUNT(f.id) FROM family f")
Page<FamilyJpaEntity> findAllWithOptimizedCount(Pageable pageable);
```

### 트랜잭션 관리
- 모든 변경 작업은 트랜잭션 내에서 수행합니다
- 조회 작업은 `@Transactional(readOnly = true)`를 사용하여 성능을 최적화합니다
- 트랜잭션 경계는 서비스 계층에서 관리합니다
