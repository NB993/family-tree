# 커밋 가이드라인

## 커밋 메시지 제목 형식

```
{타입} [{협업구분}] {구현내용}
```

### 타입
- `feat`: 기능 구현
- `test`: 테스트만 작성/수정
- `fix`: 버그 수정
- `docs`: 문서 작성/수정
- `refactor`: 코드 리팩토링

### 협업구분
- `[by-ai]`: AI가 오롯이 구현
- `[with-ai]`: 사람과 AI가 협력하여 구현

### 예시
```
feat [by-ai] 디자인 시스템 v1.0 완성 - 모바일 퍼스트 따뜻한 계열
docs [with-ai] XX 방식 변경 문서화
test [with-ai] 사용자 인증 - 단위 테스트 작성
fix [by-ai] 가족트리 데이터 구조 설계 - 순환 참조 버그 수정
```

## 커밋 메시지 본문 형식

```markdown
- 작업 요약 (3줄 이내)

## 구현된 주요 컴포넌트

### 도메인 객체 (해당시)
- 구현한 Entity, ValueObject, DomainEvent 클래스들 나열

### 애플리케이션 계층 (해당시)
- UseCase, Service, Command/Query, Port 인터페이스 등 나열

### 인프라 계층 (해당시)
- Adapter, Repository, Configuration 등 나열

### 프레젠테이션 계층 (해당시)
- Controller, Request/Response DTO 등 나열

## 테스트 구현 (해당시)
- 작성한 테스트 클래스들 ✅
- 테스트 커버리지 정보

## 해결된 이슈 (해당시)
- 해결한 기술적 문제들 나열

## 다음 단계
- 향후 작업 계획
```

## AI 커밋 작업 절차

### Step 1: VCS 상태 확인
```bash
git status           # 변경된 파일 확인
git diff             # 변경 내용 확인
git log -5 --oneline # 최근 커밋 확인
```

### Step 2: 테스트 통과 확인
```bash
./gradlew test
```

### Step 3: 커밋 메시지 작성
- 위 양식에 따라 상세한 커밋 메시지 작성
- 본문에는 구현된 컴포넌트와 테스트 정보 포함

### Step 4: 커밋 실행
```bash
git add .
git commit -m "$(cat <<'EOF'
feat [with-ai] Family 도메인 구현

- Family 도메인 객체 및 관련 계층 구현 완료

## 구현된 주요 컴포넌트

### 도메인 객체
- Family.java

### 애플리케이션 계층
- FindFamilyUseCase.java
- FindFamilyService.java
- FindFamilyByIdQuery.java

### 인프라 계층
- FamilyJpaEntity.java
- FamilyAdapter.java
- FamilyJpaRepository.java

## 테스트 구현
- FindFamilyServiceTest.java ✅
- FamilyAdapterTest.java ✅
EOF
)"
```

## 금지사항

### 절대 금지
- `git reset --hard` - **데이터 손실 위험**
- `git push --force` - 협업 히스토리 파괴

### 주의사항
- main 브랜치 직접 커밋 금지
- 테스트 실패 상태에서 커밋 금지
- 의미 있는 단위로 커밋 (너무 작거나 크지 않게)

## 커밋 전 체크리스트

- [ ] `./gradlew test` 통과
- [ ] 커밋 메시지 형식 준수
- [ ] 협업구분 `[by-ai]` 또는 `[with-ai]` 포함
- [ ] 본문에 구현된 컴포넌트 나열
- [ ] 테스트 구현 정보 포함