# 인프라 계층 명명 규칙

## 클래스 명명

| 계층 | 패턴 | 예시 |
|-----|-----|------|
| JPA 엔티티 | `{도메인}JpaEntity` | `FamilyJpaEntity` |
| JPA Repository | `{도메인}JpaRepository` | `FamilyJpaRepository` |
| 아웃바운드 어댑터 | `{도메인}Adapter` | `FamilyAdapter` |

## 엔티티-도메인 변환 메서드

### 도메인 → 엔티티 변환: `from()`
```java
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
```

### 엔티티 → 도메인 변환: `toXxx()`
```java
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
```

## Repository 메서드 명명

### 메서드 이름 기반 쿼리 우선
```java
// ✅ 권장: 메서드 이름 기반 쿼리
List<FamilyMemberJpaEntity> findByFamilyIdAndStatus(Long familyId, FamilyMemberStatus status);
Optional<FamilyJpaEntity> findByName(String name);
```

### JPQL 사용 시 주석 필수
```java
/**
 * 복잡한 조인이 필요하여 JPQL로 작성
 */
@Query("SELECT f FROM FamilyJpaEntity f WHERE f.id IN (SELECT fm.familyId FROM FamilyMemberJpaEntity fm WHERE fm.userId = :userId)")
List<FamilyJpaEntity> findFamiliesByUserId(@Param("userId") Long userId);
```

## 혼동 방지

| 메서드 | 사용 위치 | 용도 |
|-------|----------|------|
| `from()` | JpaEntity | 도메인 → JPA 엔티티 변환 |
| `toXxx()` | JpaEntity | JPA 엔티티 → 도메인 변환 |
| `newXxx()` | Domain | 새 도메인 객체 생성 |
| `withId()` | Domain | 기존 데이터 복원 |