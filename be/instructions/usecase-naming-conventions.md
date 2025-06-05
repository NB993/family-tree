# UseCase 및 Query 명명 규칙

## UseCase 인터페이스 명명 규칙

### 1. UseCase 인터페이스명
- **단수형 사용**: `FindFamilyMemberUseCase` (O), `FindFamilyMembersUseCase` (X)
- **패턴**: `{동사}{도메인객체}UseCase`
- **예시**: 
  - `FindFamilyMemberUseCase`
  - `SaveFamilyMemberUseCase`
  - `ModifyFamilyMemberUseCase`

### 2. UseCase 메서드명
- **단건 조회**: `find()`, `save()`, `modify()` 등 단수형 동사
- **복수 조회**: `findAll()`, `findMembers()` 등 복수형 의미가 명확한 메서드명
- **예시**:
```java
public interface FindFamilyMemberUseCase {
    FamilyMember find(FindFamilyMemberQuery query);           // 단건 조회
    List<FamilyMember> findAll(FindAllFamilyMembersQuery query); // 복수 조회
}
```

## Query 클래스 명명 규칙

### 1. 단건 조회용 Query
- **패턴**: `{동사}{도메인객체}Query`
- **예시**: `FindFamilyMemberQuery`, `SaveFamilyMemberQuery`

### 2. 복수 조회용 Query  
- **패턴**: `{동사}All{도메인객체복수형}Query`
- **예시**: `FindAllFamilyMembersQuery`, `FindAllFamilyMemberRelationshipsQuery`

### 3. 특수 조건 Query
- **패턴**: `{동사}{조건}{도메인객체}Query`
- **예시**: `FindActiveFamilyMembersQuery`, `FindSuspendedFamilyMembersQuery`

## 실제 구현 예시

### 기존 구현 (참고용)
```java
// UseCase 인터페이스
public interface FindFamilyMemberRelationshipUseCase {
    FamilyMemberRelationship find(FindFamilyMemberRelationshipQuery query);
    List<FamilyMemberRelationship> findAll(FindAllFamilyMemberRelationshipsQuery query);
}

// 단건 조회용 Query
public class FindFamilyMemberRelationshipQuery {
    private final Long familyId;
    private final Long fromMemberId;
    private final Long toMemberId;
}

// 복수 조회용 Query
public class FindAllFamilyMemberRelationshipsQuery {
    private final Long familyId;
    private final Long fromMemberId;
}
```

### 새로운 구현 (Story-005용)
```java
// UseCase 인터페이스
public interface FindFamilyMemberUseCase {
    FamilyMember find(FindFamilyMemberQuery query);
    List<FamilyMember> findAll(FindAllFamilyMembersQuery query);
}

// 단건 조회용 Query
public class FindFamilyMemberQuery {
    private final Long familyId;
    private final Long currentUserId;
    private final Long targetMemberId;
}

// 복수 조회용 Query
public class FindAllFamilyMembersQuery {
    private final Long familyId;
    private final Long currentUserId;
}
```

## 주의사항

1. **일관성 유지**: 모든 UseCase에서 동일한 패턴 적용
2. **단수형 원칙**: UseCase 인터페이스명은 항상 단수형
3. **메서드명 명확성**: 단건/복수 구분이 명확하게 드러나야 함
4. **Query 클래스 분리**: 단건용과 복수용 Query는 별도 클래스로 구현
5. **Validation**: 각 Query 클래스에서 입력값 검증 필수

## 적용 범위

- 모든 새로운 UseCase 구현 시 적용
- 기존 UseCase 리팩토링 시 이 규칙 적용
- 코드 리뷰 시 명명 규칙 준수 확인

---

**작성일**: 2025-06-05  
**작성자**: 기획자 AI  
**버전**: v1.0  
**적용 시작**: FT-003 Story-005부터 적용
