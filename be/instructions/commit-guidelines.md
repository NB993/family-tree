# 커밋 가이드라인

## 문서 정보
- **목적**: 개발자 AI를 위한 Git 커밋 메시지 작성 규칙
- **버전**: v1.0
- **작성일**: 2025-05-27

---

## 1. 커밋 메시지 작성 규칙

### 1.1 티켓 채번 시스템 (2025-06-10 변경)
```
프로젝트 코드:
- PM (Project Management): 프로젝트 관리, 인프라, 워크플로우 개선 작업
- FT (Family Tree): 실제 사용자 기능 개발 작업 → GitHub 이슈 번호 사용

채번 방식:
- PM-001, PM-002, PM-003 (프로젝트 관리 - 기존 방식 유지)
- FT-017, FT-018, FT-019 (GitHub Issue #17, #18, #19 번호 사용)

⚠️ 중요: FT 코드는 2025-06-10부터 GitHub 이슈 번호와 동일하게 사용
```

### 1.2 커밋 메시지 제목 형식
```
{타입} [{협업구분}] {코드}-{번호} {기능명} - {단계}

코드:
- PM: 프로젝트 관리/인프라/워크플로우 작업 (PM-001, PM-002, ...)
- FT: Family Tree 기능 개발 작업 (GitHub 이슈 번호 사용: FT-017, FT-018, ...)

타입:
- feat: 기능 구현
- test: 테스트만 작성/수정
- fix: 버그 수정
- docs: 문서 작성/수정
- refactor: 코드 리팩토링

협업구분:
- [by-ai]: AI가 오롯이 구현
- [with-ai]: 사람과 AI가 협력하여 구현

단계 (FT 작업 시에만):
- 1단계 코어 계층
- 2단계 인프라 계층  
- 3단계 프레젠테이션 계층
```

### 1.3 커밋 메시지 본문 형식 (마크다운)
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

### 특화 사항 (해당시)
- 모바일 최적화
- 성능 고려사항
- 보안 강화
- 접근성 개선

## 테스트 구현 (해당시)
- 작성한 테스트 클래스들 ✅
- 테스트 커버리지 정보
- 특별한 테스트 전략

## 해결된 이슈 (해당시)
- 해결한 기술적 문제들 나열
- 예외 처리 개선사항

## 워크플로우 개선 (해당시)
- 개발 프로세스 개선사항
- 문서 개선사항

## 다음 단계
- 향후 작업 계획
- 의존성이 있는 후속 작업
```

---

## 2. 커밋 메시지 예시

### 2.1 기능 구현 예시 (FT 코드 - GitHub 이슈 번호)
```
feat [by-ai] FT-017 디자인 시스템 v1.0 완성 - 모바일 퍼스트 따뜻한 계열
```

### 2.2 프로젝트 관리 작업 예시 (PM 코드)
```
docs [with-ai] PM-024 채번 방식 변경 문서화
```

### 2.3 테스트만 작성하는 경우
```
test [with-ai] FT-003 사용자 인증 - 단위 테스트 작성
```

### 2.4 버그 수정 예시
```
fix [by-ai] FT-002 가족트리 데이터 구조 설계 - 순환 참조 버그 수정
```

### 2.5 파일을 이용한 커밋 예시 (긴 메시지)
```bash
# commit-message.txt 파일 생성
cat > commit-message.txt << 'EOF'
feat [by-ai] FT-003 Story-005 Family 구성원 조회 UseCase 구현 완료

- 기존 구현된 FindFamilyMemberPort를 활용하여 구성원 조회 기능 구현
- UseCase 명명 규칙에 따라 단수형 인터페이스와 분리된 Query 클래스 설계

## 구현된 주요 컴포넌트

### 애플리케이션 계층
- FindFamilyMemberUseCase: 단건/복수 조회를 지원하는 UseCase 인터페이스
- FindFamilyMemberService: UseCase 구현체 (기존 FindFamilyMemberPort 활용)

## 테스트 구현
- FindFamilyMemberServiceTest: UseCase 구현체 단위 테스트 ✅
- 전체 테스트 통과 확인

## 다음 단계
- Story-006: 프레젠테이션 계층 구현
EOF

# 파일을 이용한 커밋
git commit -F commit-message.txt --no-pager

# 커밋 메시지 파일 삭제
rm -f commit-message.txt
```

---

## 3. 개발자 AI의 커밋 작업 절차

### 3.1 Story 개발 완료 시 필수 절차

#### Step 1: 변경사항 확인
```bash
# VCS 상태 확인
get_project_vcs_status 도구 사용
```

#### Step 2: 커밋 메시지 작성
- 위의 양식에 따라 상세한 커밋 메시지 작성
- 티켓 코드(PM/FT), 번호, 단계, 구현 내용을 정확히 반영
- 본문에는 구현된 컴포넌트와 테스트 정보 포함

#### Step 3: Git 커밋 실행
```bash
# --no-pager 옵션 필수 사용
execute_terminal_command: git add . --no-pager

# 짧은 커밋 메시지인 경우
execute_terminal_command: git commit -m "커밋 메시지" --no-pager

# 긴 커밋 메시지인 경우 (권장)
# 1. 커밋 메시지 파일 생성
create_new_file_with_text: commit-message.txt 에 상세한 커밋 메시지 작성
# 2. 파일을 이용한 커밋
execute_terminal_command: git commit -F commit-message.txt --no-pager
# 3. 커밋 메시지 파일 삭제
execute_terminal_command: rm -f commit-message.txt
```

#### Step 4: 기획자 AI에게 보고
- 커밋 완료 상황 보고
- 다음 단계 진행 승인 요청

### 3.2 주의사항

#### ✅ 반드시 지켜야 할 것
- 모든 git 명령어에 `--no-pager` 옵션 사용
- Story 단위로 커밋 (세분화된 커밋 지양)
- 테스트 통과 후에만 커밋
- 커밋 메시지 양식 엄격 준수
- 긴 커밋 메시지는 파일(`commit-message.txt`)을 이용하여 `git commit -F` 사용 (권장)

#### ❌ 금지사항
- 티켓 번호 없는 커밋 (PM/FT 코드 필수)
- 양식에 맞지 않는 커밋 메시지
- 테스트 실패 상태에서 커밋
- git 명령어에서 --no-pager 옵션 누락

---

## 4. 기획자 AI와의 협업

### 4.1 커밋 메시지 검토 프로세스
```
1. 개발자 AI: 커밋 메시지 초안 작성
2. 개발자 AI → 기획자 AI: 커밋 메시지 검토 요청
3. 기획자 AI: 양식 준수 및 내용 정확성 검토
4. 기획자 AI → 개발자 AI: 승인 또는 수정 요청
5. 개발자 AI: 최종 커밋 실행
```

### 4.2 검토 기준
- **양식 준수**: 제목 및 본문 형식 정확성
- **내용 정확성**: 실제 구현 내용과 일치 여부
- **완성도**: 누락된 정보 없이 상세한 설명
- **일관성**: 프로젝트 전체 커밋 스타일과 일치

---

## 5. 참고사항

### 5.1 관련 문서
- `be/instructions/workflow/ai-collaboration-workflow.md`: 전체 협업 워크플로우
- `be/instructions/development-process.md`: 개발 프로세스 가이드라인
- `be/instructions/workflow/ticket-numbering-migration.md`: 채번 방식 변경 내역

### 5.2 티켓 관리 (2025-06-10 변경)
- PM 코드: `be/instructions/ticket-numbers.json` 파일에서 관리
- FT 코드: GitHub Issues에서 관리 (이슈 번호 = FT 번호)
- Epic/Story 구조는 GitHub Issues에서 관리
- 커밋 메시지에는 반드시 티켓 번호 포함

---

## 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| v1.0.0 | 2025-05-27 | 커밋 가이드라인 초기 작성 (워크플로우에서 분리) | Claude AI |
| v1.1.0 | 2025-06-10 | FT 코드를 GitHub 이슈 번호 기반으로 변경 | 기획자 AI |
```
