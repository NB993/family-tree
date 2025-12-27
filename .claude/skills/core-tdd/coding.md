# 코어 계층 코딩 스타일

## 기본 원칙

- Java 21 이상의 기능 활용
- 불변 객체와 OOP 스타일 권장
- 메서드 매개변수는 `final` 키워드 사용
- 모든 클래스와 메서드에 JavaDoc 작성
- 인터페이스 구현 시 `{@inheritDoc}` 사용

## 코드 구조

### 클래스 구조 순서
1. 필드 선언
2. 생성자
3. 정적 메서드
4. 인스턴스 메서드

### 코드 레이아웃
- 들여쓰기: 4칸 공백
- 메서드 인자 3개 이상: 첫 번째 인자부터 줄바꿈
- 한 줄 최대 120자

## Lombok 사용 규칙

### 허용
- `@Getter`
- `@RequiredArgsConstructor`

### 금지
- `@Builder` - **절대 사용 금지**
- `@Setter` - 도메인 객체에서 금지

## 예외 처리

### 기본 원칙
- **예외 메시지는 한글로 작성**
- 예외는 유스케이스에서 발생, 어댑터에서 발생 금지
- FTException 사용, 적절한 예외 코드 지정

### 코어 계층 null 체크 규칙
```java
// ✅ 올바른 예시: NPE 발생 (개발자 실수 검증)
@Override
@Transactional(readOnly = true)
public FamilyMember find(FindFamilyMemberByIdQuery query) {
    Objects.requireNonNull(query, "query는 null일 수 없습니다");
    // ...
}

// ❌ 잘못된 예시: IllegalArgumentException 사용 금지
if (query == null) {
    throw new IllegalArgumentException("query는 null일 수 없습니다");
}
```

### 계층별 책임 분리
- **Query/Command 생성자**: 사용자 입력 검증 → `IllegalArgumentException`
- **코어 계층**: 개발자 실수 검증 → `NullPointerException`

## OOP 설계 원칙

### Tell, Don't Ask
```java
// ❌ 잘못된 예시 (Asking)
FamilyMember member = memberPort.find(memberId);
if (member.getRole() == Role.ADMIN || member.isOwner()) {
    // 관리자일 때의 로직...
}

// ✅ 올바른 예시 (Telling)
FamilyMember member = memberPort.find(memberId);
member.doAdminAction();  // member 객체가 스스로 역할 확인
```

### Rich Domain Model
```java
// ❌ Anemic Domain Model (금지)
public class Family {
    private List<Member> members;
    // getter/setter만 존재
}

// ✅ Rich Domain Model (권장)
public class Family {
    private static final int MAX_MEMBERS = 5;
    private List<Member> members;

    public void addMember(Member newMember) {
        if (this.members.size() >= MAX_MEMBERS) {
            throw new FamilyCapacityExceededException();
        }
        validateNewMember(newMember);
        this.members.add(newMember);
    }
}
```

## Command/Query 객체

### record 타입 필수
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

## 컬렉션 처리

- 빈 컬렉션은 `Collections.emptyList()` 반환 (null 금지)
- 방어적 복사를 통해 불변성 유지
- Stream API 적극 활용

```java
public List<Member> getMembers() {
    return Collections.unmodifiableList(new ArrayList<>(this.members));
}
```