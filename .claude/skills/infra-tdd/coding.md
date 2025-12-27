# 인프라 계층 코딩 스타일

## JPA 엔티티 작성 규칙

### 생성자 패턴
```java
// ✅ 올바른 방식: from() 정적 메서드 사용
Family domain = Family.newFamily("패밀리명", "설명", "프로필URL", 1L);
FamilyJpaEntity entity = FamilyJpaEntity.from(domain);

// ❌ 금지: setter 사용
FamilyJpaEntity entity = new FamilyJpaEntity();
entity.setName("패밀리명");  // 컴파일 에러 - setter 없음
```

### 접근 제한자
- 기본 생성자: `protected` (JPA 요구사항)
- 필드 초기화 생성자: `private`
- 팩토리 메서드: `public static`

## Repository 메서드 작성

### 우선순위
1. 메서드 이름 기반 쿼리
2. JPQL `@Query` (주석으로 사용 이유 명시)
3. 네이티브 쿼리 (불가피한 경우만)

### 메서드 이름 기반 쿼리
```java
// ✅ 권장
List<FamilyMemberJpaEntity> findByFamilyIdAndStatus(Long familyId, FamilyMemberStatus status);
Optional<FamilyJpaEntity> findByNameContaining(String keyword);
boolean existsByEmail(String email);
```

### JPQL 사용 시
```java
/**
 * 복잡한 조인과 서브쿼리가 필요하여 JPQL로 작성
 */
@Query("SELECT f FROM FamilyJpaEntity f WHERE f.id IN " +
       "(SELECT fm.familyId FROM FamilyMemberJpaEntity fm WHERE fm.userId = :userId)")
List<FamilyJpaEntity> findFamiliesByUserId(@Param("userId") Long userId);
```

## DB 독립적 코드 작성

### 권장
- JPA 표준 어노테이션 사용
- JPQL, Criteria API 우선

### 자제
- 네이티브 쿼리
- DB 특화 타입 (PostgreSQL jsonb, array 등)

## 양방향 관계 처리

### 헬퍼 메서드 제공
```java
@OneToMany(mappedBy = "family", cascade = CascadeType.ALL,
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<FamilyMemberJpaEntity> members = new ArrayList<>();

public void addMember(FamilyMemberJpaEntity member) {
    this.members.add(member);
    member.setFamily(this);
}

public void removeMember(FamilyMemberJpaEntity member) {
    this.members.remove(member);
    member.setFamily(null);
}
```

## 컬렉션 처리

### 방어적 복사
```java
public List<FamilyMemberJpaEntity> getMembers() {
    return Collections.unmodifiableList(new ArrayList<>(this.members));
}
```

### 빈 컬렉션 반환
```java
// ✅ 권장
return Collections.emptyList();

// ❌ 금지
return null;
```

## 트랜잭션 관리

- 변경 작업: `@Transactional`
- 조회 작업: `@Transactional(readOnly = true)`
- 트랜잭션 경계는 Service 계층에서 관리