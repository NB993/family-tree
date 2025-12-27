# 리팩토링 지침

## 기본 원칙

### 1. 테스트 먼저 확인

```bash
# 리팩토링 전 테스트 실행
./gradlew test

# 모든 테스트 통과 확인 후 리팩토링 시작
```

### 2. 작은 단위로 진행

- 한 번에 하나의 리팩토링만 수행
- 각 리팩토링 후 테스트 실행
- 실패 시 즉시 롤백

### 3. 동작 변경 금지

- 외부에서 관찰 가능한 동작은 유지
- 내부 구현만 개선

## 리팩토링 기법

### 중복 제거

```java
// Before: 중복 코드
public Family find(FindFamilyByIdQuery query) {
    Objects.requireNonNull(query, "query는 null일 수 없습니다");
    return findFamilyPort.find(query.id())
        .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
}

public Family find(FindFamilyByNameQuery query) {
    Objects.requireNonNull(query, "query는 null일 수 없습니다");
    return findFamilyPort.findByName(query.name())
        .orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
}

// After: 공통 로직 추출
private void validateQuery(Object query) {
    Objects.requireNonNull(query, "query는 null일 수 없습니다");
}

private Family findOrThrow(Optional<Family> family) {
    return family.orElseThrow(() -> new FTException(FamilyExceptionCode.FAMILY_NOT_FOUND));
}
```

### 명확성 향상

```java
// Before: 의미 불명확
if (member.getRole().getValue() >= Role.ADMIN.getValue()) {
    // ...
}

// After: 의도 명확
if (member.hasAdminOrHigherRole()) {
    // ...
}
```

### 메서드 추출

```java
// Before: 긴 메서드
public void processJoinRequest(ProcessJoinRequestCommand command) {
    // 20줄 이상의 코드...
}

// After: 메서드 분리
public void processJoinRequest(ProcessJoinRequestCommand command) {
    validateCommand(command);
    JoinRequest request = findRequest(command);
    processRequest(request, command);
    notifyUser(request);
}
```

## 계층별 리팩토링 포인트

### Domain 계층

| 대상 | 리팩토링 포인트 |
|------|----------------|
| 필드 | 불변성 확보 (final) |
| 생성자 | private으로 은닉 |
| 팩토리 메서드 | newXxx/withId 패턴 |
| 비즈니스 로직 | Tell, Don't Ask 적용 |

### Service 계층

| 대상 | 리팩토링 포인트 |
|------|----------------|
| 메서드 크기 | 15~20줄 이내 |
| 책임 | 오케스트레이션만 담당 |
| 트랜잭션 | @Transactional 적절성 |
| 예외 처리 | 명확한 예외 메시지 |

### Adapter 계층

| 대상 | 리팩토링 포인트 |
|------|----------------|
| 변환 로직 | from/toXxx 메서드 사용 |
| 쿼리 최적화 | N+1 문제 해결 |
| null 처리 | Optional 활용 |

### Controller 계층

| 대상 | 리팩토링 포인트 |
|------|----------------|
| 책임 | 변환만 담당 |
| 응답 형식 | ResponseEntity 사용 |
| 검증 | @Valid 적용 |

## 금지 사항

- [ ] 테스트 없이 리팩토링 금지
- [ ] 동작 변경과 리팩토링 동시 진행 금지
- [ ] 여러 리팩토링 동시 진행 금지
- [ ] 명명 규칙 위반 금지 (Find/Save/Modify/Delete)

## 리팩토링 완료 체크리스트

```markdown
## 리팩토링 완료 확인

- [ ] 모든 테스트 통과
- [ ] 명명 규칙 준수
- [ ] 메서드 크기 적절 (15~20줄 이내)
- [ ] 중복 코드 제거
- [ ] 가독성 향상
- [ ] 성능 저하 없음

## 변경 사항 요약
- 변경 전: {변경 전 상태}
- 변경 후: {변경 후 상태}
- 개선 효과: {개선 효과}
```