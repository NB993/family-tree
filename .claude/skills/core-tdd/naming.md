# 코어 계층 명명 규칙

## 필수 명명 규칙

| 동작 유형 | 사용할 접두사 | 잘못된 예시 |
|---------|------------|------------|
| 조회 | **Find** | Get, Retrieve, Query |
| 등록 | **Save** | Create, Add, Insert |
| 수정 | **Modify** | Update, Change, Edit |
| 삭제 | **Delete** | Remove, Erase |

## UseCase 인터페이스 명명

### 인터페이스명
- **단수형 사용**: `FindFamilyMemberUseCase` (O), `FindFamilyMembersUseCase` (X)
- **패턴**: `{동사}{도메인객체}UseCase`

### 메서드명 (엄격 통일)
- **단건 조회**: `find()` - Query 객체 타입으로 의도 구분
- **복수 조회**: `findAll()`
- **저장**: `save()`
- **수정**: `modify()`
- **삭제**: `delete()`

**올바른 예시**:
```java
public interface FindFamilyMemberUseCase {
    FamilyMember find(FindFamilyMemberByIdQuery query);
    FamilyMember find(FindFamilyMemberByEmailQuery query);
    List<FamilyMember> findAll(FindAllFamilyMembersQuery query);
}
```

**금지된 예시**:
```java
// ❌ 금지: 메서드명으로 조회 의도 구분
FamilyMember findById(Long id);
FamilyMember findByEmail(String email);
```

## Query/Command 객체 명명

### Query 객체 (record 타입 필수)
```java
// 단일 필드 기준 조회
FindFamilyMemberByIdQuery
FindUserByEmailQuery

// 복합 조건 조회
FindActiveFamilyMembersByFamilyIdQuery
FindFamilyMembersByRoleQuery
```

### Command 객체 (record 타입 필수)
```java
SaveFamilyCommand
ModifyFamilyCommand
DeleteFamilyCommand
```

## 도메인 객체 정적 팩토리 메서드

### 신규 생성: `newXxx`
```java
public static FamilyMember newMember(String name, FamilyRole role, LocalDate birthDate) {
    return new FamilyMember(null, name, role, birthDate);
}
```

### 기존 데이터 복원: `withId`
```java
public static FamilyMember withId(Long id, String name, FamilyRole role, LocalDate birthDate) {
    Objects.requireNonNull(id, "id는 null일 수 없습니다");
    return new FamilyMember(id, name, role, birthDate);
}
```

### 금지된 메서드명
```java
// ❌ 절대 사용 금지
public static FamilyMember of(...) { }
public static FamilyMember create(...) { }
public static FamilyMember from(...) { }  // JPA 엔티티 전용
```

## 계층별 클래스 명명

| 계층 | 패턴 | 예시 |
|-----|-----|------|
| 인바운드 포트 | `{Find/Save/Modify/Delete}{도메인}UseCase` | `FindFamilyUseCase` |
| 아웃바운드 포트 | `{Find/Save/Modify/Delete}{도메인}Port` | `FindFamilyPort` |
| 서비스 | `{Find/Save/Modify/Delete}{도메인}Service` | `FindFamilyService` |
| 커맨드/쿼리 | `{Find/Save/Modify/Delete}{도메인}Command/Query` | `SaveFamilyCommand` |