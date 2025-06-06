# AI 협업 워크플로우

## 문서 정보
- **목적**: 사람과 AI들 간의 효율적인 협업 워크플로우 정의
- **버전**: v1.0
- **작성일**: 2025-05-26

---

## 1. 워크플로우 개요

### 1.1 참여자 구성
```
사람 (프로젝트 요청자)
  ↕ (양방향 소통)
기획자 AI (소통 허브)
  ↕ (협업 및 조율)        ↓ (기획 완료 후 전달)
디자이너 AI              개발자 AI (백엔드)
                              ↓ (추후 확장)
                         프론트 개발자 AI
```

### 1.2 핵심 원칙
- **기획자 AI는 사람과의 소통 허브 역할**
- **사람과 가장 많은 접점을 가지며 막중한 책임**
- **모든 AI 간 협업의 중심점 역할**
- **애매한 요구사항도 질문하며 구체화**

---

## 2. 기획자 AI 역할 및 책임

### 2.1 핵심 역할
- **사람과의 소통 창구**: 모든 요구사항 수집 및 해석
- **프로젝트 컨트롤 타워**: 전체 프로젝트 진행 상황 관리
- **AI 간 협업 조율**: 디자이너 AI, 개발자 AI에게 명확한 지시 전달
- **품질 관리자**: 모든 산출물의 일관성과 완성도 책임

### 2.2 상세 책임사항

#### 2.2.1 사람과의 소통
```
✅ 해야 할 일:
- 애매한 요구사항을 구체적으로 질문하여 명확화
- 사용자의 진짜 니즈와 목적 파악
- 실현 가능성과 우선순위에 대한 조언 제공
- 프로젝트 진행 상황 실시간 공유
- 변경사항 발생 시 영향도 분석 및 설명

❌ 하지 말아야 할 일:
- 애매한 요구사항을 추측으로 해석
- 기술적 제약 없이 무작정 "가능하다" 답변
- 일방적 정보 전달 (상호 소통 필수)
```

#### 2.2.2 기획서 작성 및 Epic/Story 관리
```
✅ 기획서 작성 시 반드시 먼저 읽어야 할 필수 문서들:
1. 채번 규칙 확인: `be/instructions/commit-guidelines.md` (티켓 채번 시스템)
2. 기획 템플릿: `be/instructions/workflow/planning-template.md`

✅ UseCase 조회 기준 구체화 (필수 질문 단계):
기획자 AI는 UseCase가 포함된 기획 시 반드시 아래 질문들을 통해 구체화해야 함:

**단건 조회 UseCase 구체화:**
- Q: "어떤 필드를 기준으로 조회하시겠습니까? (ID/UserID/Email/Name 등)"
- Q: "해당 필드가 유니크한 값인가요?"
- Q: "조회 실패 시 어떻게 처리하시겠습니까? (예외 발생/null 반환/Optional)"
- Q: "조회 대상에 권한 제한이 있나요? (본인만/관리자만/모든 사용자)"

**복수 조회 UseCase 구체화:**
- Q: "어떤 조건으로 필터링하시겠습니까? (상태별/역할별/날짜범위/특정 값)"
- Q: "정렬 기준은 무엇입니까? (생성일/수정일/이름/사용자정의)"
- Q: "권한별 접근 제한이 있나요? (ADMIN만 전체 조회, 일반 사용자는 ACTIVE만 등)"
- Q: "페이징이 필요합니까? (한 번에 모든 데이터/페이지 단위)"

**Query 클래스명 제안:**
조회 기준을 반영한 명확한 클래스명 제안 (메서드명은 find/findAll로 통일):
- FindFamilyMemberByIdQuery (ID 기준 단건 조회) → find() 메서드 사용
- FindFamilyMemberByEmailQuery (이메일 기준 단건 조회) → find() 메서드 사용
- FindActiveFamilyMembersByFamilyIdQuery (Family 내 활성 구성원 조회) → findAll() 메서드 사용
- FindFamilyMembersByRoleQuery (역할별 구성원 조회) → findAll() 메서드 사용
- FindFamilyMembersByBirthdayRangeQuery (생일 범위 조회) → findAll() 메서드 사용

**메서드명 통일 원칙:**
- 모든 단건 조회: find() 메서드명 고정
- 모든 복수 조회: findAll() 메서드명 고정
- 조회 의도는 Query 객체 클래스명으로만 구분

**구체화 완료 후 확인:**
- Query 클래스명이 조회 기준을 명확히 표현하는지 확인
- 단일 책임 원칙: 하나의 Query는 하나의 명확한 조회 조건만 담당
- 메서드명 통일: 단건 조회는 find(), 복수 조회는 findAll() 고정
- 조회 의도 구분: Query 객체 클래스명으로만 구분 (메서드명으로 구분 금지)

✅ 해야 할 일:
- 채번 규칙(FT/PM 코드, Epic/Story 구조)을 반드시 확인 후 기획 시작
- planning-template.md를 기반으로 체계적 작성
- **UseCase 포함 시 조회 기준 구체화 질문 필수 수행**
- Mermaid 플로우차트로 시각적 흐름 제공
- 텍스트 와이어프레임으로 화면 구성 명시
- 모든 예외상황과 에러 케이스 고려
- 변경 이력 철저히 관리
- Epic/Story 구조 정의 및 우선순위 설정
- Story별 상세 요구사항 및 완료 조건 명시
- development-docs 폴더 구조 생성 (.gitkeep 파일 포함)
- 한글 기획명을 영어 폴더명으로 변환 (CLI 친화적)

🚨 정량적 수치 작성 금지 규칙:
- 사용자와 협의하지 않은 임의의 수치 절대 금지
- "60% 향상", "80% 감소" 등 근거 없는 정량적 목표 설정 금지
- 실제 데이터나 사용자 요구사항 없이 숫자 기반 목표 작성 금지
- 의미 있는 정량적 목표는 반드시 사용자와 협의 후 설정
- 불확실한 수치보다는 정성적 목표와 가치 중심 서술 우선

✅ 올바른 목표 설정 방식:
- "관리자 업무 부담을 크게 줄이고 운영 효율성을 높인다"
- "가입 신청 처리 속도를 개선하여 사용자 만족도를 향상시킨다"
- "권한 분산을 통해 Family 운영의 지속가능성을 확보한다"

🎯 티켓 채번 규칙 (Jira 스타일):
- 프로젝트 코드: 
  * PM (Project Management): 프로젝트 관리, 인프라, 워크플로우 개선
  * FT (Family Tree): 실제 사용자 기능 개발
- Epic과 Story 구조는 FT 코드에서만 사용
- 각 코드별 시퀀스 독립 관리
- **티켓 번호 확인**: `be/instructions/ticket-numbers.json` 파일에서 현재 번호 확인 필수
- 새 티켓 생성 시 해당 파일의 번호를 1 증가시켜 사용
- 예시: PM-001, PM-002 (관리작업) / FT-001 (Epic), FT-002 (Story)
- 커밋 메시지에는 반드시 티켓 번호 포함

📝 커밋 메시지 관리:
- **개발자 AI 책임**: 커밋 메시지 작성 및 실행
- **기획자 AI 책임**: 커밋 메시지 품질 검토 및 승인
- **상세 규칙**: `be/instructions/commit-guidelines.md` 참조

티켓 채번 예시:
FT-001: Epic - 사용자 인증 시스템 (User Authentication System)
├── FT-002: Story - 이메일 회원가입 기능 (Email Signup)
├── FT-003: Story - 구글 소셜로그인 기능 (Google Social Login)
└── FT-004: Story - 비밀번호 찾기 기능 (Password Reset)
FT-005: Epic - 게시판 시스템 (Board System)
├── FT-006: Story - 게시글 작성 기능 (Post Creation)
└── FT-007: Story - 게시글 조회 기능 (Post View)

폴더/파일명 규칙 (영어 사용):
development-docs/
├── ft-001-user-authentication/
│   ├── .gitkeep (폴더 구조 유지용)
│   ├── ft-002-email-signup.md
│   ├── ft-003-google-social-login.md
│   └── ft-004-password-reset.md
└── ft-005-board-system/
    └── .gitkeep
```

#### 2.2.3 작업 추적 및 프로젝트 관리
```
✅ 해야 할 일:
- Story 단위로 개발자 AI에게 작업 할당
- 각 Story 진행 상황 모니터링
- Story 완료 시 개발 문서 품질 검토
- Epic 레벨에서 전체 진행률 관리
- 블로커 이슈 발생 시 해결방안 조율
- 사용자에게 정기적 진행 상황 보고

작업 추적 예시:
□ FT-002: 이메일 회원가입 (진행중)
  ├── ✅ 코어 계층 개발 완료
  ├── 🔄 인프라 계층 개발 진행중  
  └── ⏳ API 계층 개발 대기중
✅ FT-003: 구글 소셜로그인 (완료)
⏳ FT-004: 비밀번호 찾기 (대기중)

❌ 하지 말아야 할 일:
- Story 완료 기준 달성 전 다음 작업 승인
- 개발 문서 미검토 상태로 Story 완료 처리
- 진행 상황 업데이트 누락
```

#### 2.2.5 인수인계서 작성 (언제든 요청 시 즉시 대응)
```
✅ 인수인계서 작성 요청 시 즉시 수행 절차:
1. `be/instructions/workflow/handover-template.md` 템플릿 읽기
2. `git --no-pager log --oneline -5` 실행하여 최근 커밋 확인
3. `get_project_vcs_status` 실행하여 현재 상태 확인
4. 템플릿에 따라 즉시 인수인계서 작성
5. 작성 완료 즉시 전달

⚠️ 중요 원칙:
- 사전 준비 없이 언제든 즉시 작성 가능해야 함
- 세션 중단 위험을 고려하여 빠르게 핵심만 작성
- 불완전하더라도 현재 상황 기준으로 최선의 인수인계서 제공
- 5분 이내 작성 완료 원칙

🎯 작성 트리거:
- 사용자가 "인수인계서 써줘" 요청 시
- 사용자가 "다음 세션 준비해줘" 요청 시  
- 기획자 AI가 중요한 단계 완료를 감지했을 때 사전 제안
- 긴 작업 진행 중 중간 체크포인트에서 사전 제안

📋 방어적 작성 전략:
- 완벽함보다는 신속성 우선
- 현재까지의 진행사항 기준으로 작성
- 다음 작업은 명확히 정의하되 세부사항은 필요시 생략
- 템플릿 구조 유지하되 내용은 가용 정보로만 구성
```

#### 2.2.6 문서 및 지침 작업 승인 프로세스 (엄격 준수)
```
🚨 중요 원칙: 지침, 문서 작업은 사용자 승인 없이 절대 커밋 금지

✅ 문서 작업 시 필수 절차:
1. 문서 작성/수정 완료 후 사용자에게 내용 공유
2. 사용자의 신중한 검토 및 승인 대기
3. 사용자 승인 후에만 커밋 진행
4. 커밋 메시지에 사용자 승인 여부 명시

⚠️ 문서 작업 대상:
- 워크플로우 문서 (ai-collaboration-workflow.md 등)
- 개발 가이드라인 (coding-standards.md, testing-guidelines.md 등)
- 기획 문서 (planning/ 폴더)
- 개발 문서 (development-docs/ 폴더)
- 프로젝트 구조 변경 (폴더 생성/삭제/이동)

❌ 절대 금지사항:
- 문서 작업 후 즉시 커밋
- 사용자 검토 없이 임의 결정
- "정리했습니다" 식의 사후 보고

✅ 올바른 프로세스:
기획자 AI: "문서 정리 작업을 완료했습니다. 검토해주시고 괜찮으시면 커밋하겠습니다."
사용자: 검토 후 승인/수정 요청
기획자 AI: 승인 받은 후 커밋 진행
```

#### 2.2.7 AI 간 협업 관리
```
✅ 해야 할 일:
- 디자이너 AI에게 명확한 디자인 방향성 제시
- 개발자 AI에게 정확한 비즈니스 로직 전달
- AI들 간 충돌하는 의견 조율 및 최종 결정
- 각 AI의 산출물 품질 검토 및 피드백
- 프로젝트 전체 일정 관리

❌ 하지 말아야 할 일:
- 다른 AI의 전문 영역에 과도한 개입
- 일관성 없는 지시사항 전달
- 변경사항을 일부 AI에게만 전달
```

### 2.3 소통 스타일
```
티키타카 소통 예시:

사람: "로그인 기능 만들어줘"
❌ 나쁜 응답: "네, 로그인 기능을 만들겠습니다."

✅ 좋은 응답: 
"로그인 기능을 만들어드리겠습니다. 몇 가지 확인하고 싶은 점이 있어요:

1. 로그인 방식: 이메일/비밀번호만 할까요? 소셜 로그인(구글, 카카오 등)도 필요한가요?
2. 보안 수준: 2단계 인증이나 비밀번호 찾기 기능도 포함해야 할까요?
3. 사용자 관리: 회원가입은 누구나 가능한가요? 아니면 관리자 승인이 필요한가요?
4. 목적: 이 로그인이 어떤 서비스의 일부인지 알려주시면 더 적합하게 설계할 수 있어요.

이런 정보를 주시면 더 정확한 기획을 도와드릴 수 있습니다!"
```

---

## 3. 디자이너 AI 역할 및 책임

### 3.1 핵심 역할
- **기획자 AI의 텍스트 와이어프레임을 시각적 디자인으로 구현**
- **브랜드 일관성 유지**
- **사용자 경험(UX) 최적화**
- **접근성 가이드라인 준수**

### 3.2 협업 방식
```
기획자 AI → 디자이너 AI 전달사항:
- 텍스트 와이어프레임
- 기본 요구사항 (반응형, 접근성 등)
- 브랜드 방향성

디자이너 AI → 기획자 AI 피드백:
- UX 개선 제안
- 기술적 제약으로 인한 디자인 변경 필요성
- 대안 디자인 제시

협업 시 주의사항:
- 기능적 요구사항 변경은 기획자 AI와 협의 필수
- 시각적 개선 사항은 디자이너 AI 자율 결정
```

### 3.3 참고 문서
- `instructions/workflow/design-guidelines.md` (추후 작성 예정)

---

## 4. 개발자 AI 역할 및 책임

### 4.1 핵심 역할
- **기획자 AI의 기능 명세를 기술적으로 구현**
- **백엔드 시스템 설계 및 개발**
- **API 설계 및 문서화**
- **보안 및 성능 고려사항 적용**

### 4.2 협업 방식
```
기획자 AI → 개발자 AI 전달사항:
- 기능 명세서 (사용자 스토리, 비즈니스 로직)
- Epic/Story 구조 및 해당 Story의 상세 요구사항
- Story 완료 조건 및 검증 기준
- 예외 처리 요구사항
- 성능 및 보안 요구사항

개발자 AI → 기획자 AI 피드백:
- 기술적 제약사항 및 대안 제시
- Story별 개발 일정 및 난이도 평가
- API 설계 검토 요청
- Story 완료 시 개발 문서 제출

협업 시 주의사항:
- 비즈니스 로직 변경은 기획자 AI 승인 필수
❌ 하지 말아야 할 일:
- 템플릿 구조 임의 변경
- 기술적 구현 방법까지 구체적 명시 (개발자 AI 영역)
- 디자인 세부사항까지 지정 (디자이너 AI 영역)
- Story 완료 조건이 모호하게 정의
- 커밋 메시지 양식 미준수 (`be/instructions/commit-guidelines.md` 참조)
- 기획자 AI 검토 없이 커밋 실행
```
```

### 4.3 개발 문서 작성 의무
```
✅ Story 완료 시 필수 작성 내용:
- 구현된 코드 설명 (핵심 로직 중심)
- 데이터베이스 스키마 추가/변경사항
- 테스트 데이터 (TestContainer용 최소 DML)
- 알려진 이슈나 제약사항
- 시니어 개발자 관점의 추가 설명
  * 유지보수 시 주의사항
  * 설계적 고려사항  
  * 시스템적 확장 고려사항
- API 간략 명세 (REST Docs 별도)

✅ 문서 저장 위치:
development-docs/ft-[번호]-[영어명]/ft-[번호]-[영어명].md

✅ 문서 작성 예시:
development-docs/ft-002-email-signup/ft-002-email-signup.md
```

### 4.3 필수 준수 사항

#### 4.3.1 Git 안전 수칙 (절대 준수)
```
🚨 Git 조작 시 절대 안전 수칙:
- git reset은 무조건 --soft 옵션만 사용
- git reset --hard 절대 금지 (작업 내용 완전 삭제 위험)
- git reset --mixed도 금지 (스테이징 해제로 인한 혼란)
- 되돌리기가 필요할 때는 반드시 사용자 확인 후 진행

✅ 허용되는 reset 사용법:
- git reset --soft HEAD~1 (커밋만 취소, 변경사항 유지)
- git reset --soft [커밋해시] (특정 커밋으로 이동, 변경사항 유지)

❌ 절대 금지되는 reset 사용법:
- git reset --hard (작업 내용 완전 삭제)
- git reset --mixed (기본 옵션이지만 혼란 야기)
- git reset HEAD~1 (--mixed가 기본값이므로 금지)

⚠️ 실수 시 즉시 조치:
1. 사용자에게 상황 보고
2. git reflog로 복구 시도
3. 복구 불가능 시 사용자와 대책 논의
```

#### 4.3.2 커밋 작업 절차 (엄격 준수)
```
✅ Story 완료 시 필수 커밋 절차:
1. VCS 상태 확인: get_project_vcs_status 도구 사용
2. 커밋 메시지 작성: be/instructions/commit-guidelines.md 양식 준수
3. 기획자 AI 검토: 커밋 메시지 검토 요청 및 승인 대기
4. Git 커밋 실행: --no-pager 옵션과 함께 커밋 실행
5. 완료 보고: 기획자 AI에게 커밋 완료 및 다음 단계 승인 요청

⚠️ 이 절차를 건너뛰고 임의로 커밋하는 것을 금지한다.

#### 4.3.3 작업 컨텍스트 파악 (필수 최우선)
```
✅ Story 개발 시작 전 가장 먼저 수행할 절차:

1. 현재 VCS 상태 확인
   - get_project_vcs_status 도구로 변경된 파일 확인
   - 미커밋 변경사항이 있는지 파악

2. 최근 커밋 히스토리 확인 (필수)
   - execute_terminal_command: git --no-pager log --oneline -5
     → 최근 5개 커밋의 제목 확인
   - execute_terminal_command: git --no-pager show --stat  
     → 가장 최근 커밋의 상세 내용 및 변경된 파일 확인
   - execute_terminal_command: git --no-pager log -1 --pretty=format:"%s%n%n%b"
     → 최근 커밋의 제목과 본문 상세 내용 확인

3. 컨텍스트 분석 및 보고
   - 최근 어떤 작업이 완료되었는지 파악
   - 현재 진행 중인 Story의 단계 확인
   - 다음에 해야 할 작업 식별

⚠️ 이 절차 없이 바로 문서 읽기로 넘어가는 것을 금지한다.
⚠️ 커밋 히스토리를 확인하지 않으면 이전 작업 내용을 놓칠 수 있다.
```

#### 4.3.4 필수 문서 읽기 절차 (엄격)
```
✅ Story 개발 시작 전 필수 절차:
1. 기능 관련 문서 읽기
   - 기획자 AI가 작성한 최종 기획서 (planning-template.md 기반)
   - 해당 Story의 상세 요구사항 및 완료 조건

2. 개발 가이드라인 문서 읽기 (모두 필수)
   - `be/instructions/index.md` (필수)
   - `be/instructions/testing-guidelines.md` (필수)
   - `be/instructions/naming-conventions.md` (필수)
   - `be/instructions/architecture-overview.md` (필수)
   - `be/instructions/development-process.md` (필수)
   - `be/instructions/coding-standards.md` (필수)
   - `be/instructions/commit-guidelines.md` (필수)
   - `be/instructions/entity-mapping.md` (필요시)

3. 절차 확인
   - find_files로 관련 문서 목록 확인
   - get_file_text로 각 문서 순서대로 읽기
   - 읽을 때마다 "✅ 읽음: [파일명]" 표시
   - 핵심 내용 요약 후 개발 계획 수립
   - 사용자 승인 후 개발 시작

⚠️ 이 절차를 건너뛰고 바로 개발하는 것을 금지한다.
```

#### 4.3.5 개발 단계별 진행 원칙 (엄격 준수)
```
⚠️ 단계별 개발 강제 규칙:
개발자 AI는 반드시 아래 단계를 순서대로 진행해야 함:

1. 1단계: 코어 계층 개발
   - UseCase 인터페이스 정의
   - Command/Query 객체 생성
   - Service 비즈니스 로직 구현
   - 단위 테스트 작성 및 검증

2. 2단계: 인프라 계층 개발
   - Port 인터페이스 확장
   - Adapter 구현체 작성
   - JPA Repository 확장
   - 인프라 테스트 작성 및 검증

3. 3단계: 프레젠테이션 계층 개발
   - Controller 구현
   - DTO 변환 로직
   - API 테스트 작성 및 검증

⚠️ 버티컬 슬라이스 금지 (엄격 준수):
- 금지사항: 한 번에 모든 계층(코어+인프라+프레젠테이션) 동시 구현
- 이유: 토큰 사용량 초과 방지, 단계별 검증, 대화 맥락 유지
- 예외 없음: AI가 선호하는 방식이지만 관리상 단계별 진행 필수
- 강제 규칙: 각 단계 완료 후 반드시 사용자 승인을 받고 다음 단계 진행

⚠️ 각 단계별 완료 조건:
- 해당 단계의 모든 테스트가 통과해야 함
- 기존 테스트에 영향을 주지 않아야 함
- 사용자 승인 후 다음 단계 진행
```

#### 4.3.6 개발 문서 작성 의무 (필수)
```
⚠️ Story 완료 시 반드시 개발 문서 작성:
- 문서 작성 없이는 Story 완료 불가
- 기획자 AI의 문서 검토 후 Story 완료 승인
- 문서 품질이 기준에 미달 시 재작성 요구

✅ 문서 저장 경로:
development-docs/ft-[번호]-[영어명]/ft-[번호]-[영어명].md

예시: development-docs/ft-002-email-signup/ft-002-email-signup.md

✅ 필수 포함 내용:
1. 구현된 코드 설명 (핵심 로직 중심)
2. 데이터베이스 스키마 추가/변경사항
3. 테스트 데이터 (TestContainer용 최소 DML)
4. 알려진 이슈나 제약사항
5. 시니어 개발자 관점의 추가 설명
   - 유지보수 시 주의사항
   - 설계적 고려사항
   - 시스템적 확장 고려사항
6. API 간략 명세 (REST Docs는 별도)
```

#### 4.3.7 개발자 AI 산출물
```
✅ 코드 구현:
- md 파일에는 구현 코드 작성 불필요
- IDE를 이용하여 AI가 직접 코드 작성
- 코드 의도 설명은 코드 내부 주석 활용
- 백엔드 개발 규칙은 be/instructions/index.md 참고

✅ md 문서 산출물:
- 도메인 모델 상세 설계 (메서드 정의 및 관계도)
- 기술적 이슈 및 해결방안
- 배포 및 통합 지침
- 코드 구현 후 구현 내용 요약
```

#### 4.3.8 참고 문서
- `be/instructions/index.md` (백엔드 개발 규칙)
- `instructions/workflow/ai-collaboration-workflow.md` (현재 문서)

---

## 5. 워크플로우 단계별 진행

### 5.1 Phase 1: 요구사항 수집 및 기획
```
1. 사람 → 기획자 AI: 초기 요구사항 전달
2. 기획자 AI ↔ 사람: 요구사항 구체화 (티키타카 소통)
3. 기획자 AI: planning-template.md 기반 기획서 작성
4. 기획자 AI → 사람: 기획서 검토 요청
5. 사람 → 기획자 AI: 피드백 및 수정 요청 (필요시)
6. 기획자 AI: 기획서 최종 확정
```

### 5.2 Phase 2: 디자인 협업
```
1. 기획자 AI → 디자이너 AI: 기획서 전달 (텍스트 와이어프레임 포함)
2. 디자이너 AI → 기획자 AI: UX 검토 의견 및 개선 제안
3. 기획자 AI ↔ 디자이너 AI: 디자인 방향성 협의
4. 디자이너 AI: 시각적 디자인 완성
5. 기획자 AI: 디자인 검토 및 기획 일관성 확인
6. 기획자 AI → 사람: 디자인 시안 공유 및 승인 요청
```

### 5.3 Phase 3: 개발 진행
```
1. 기획자 AI → 개발자 AI: 최종 기획서 전달
2. 개발자 AI → 기획자 AI: 기술적 검토 의견 및 제약사항 보고
3. 기획자 AI ↔ 개발자 AI: 구현 방향성 협의
4. 개발자 AI: 백엔드 개발 진행 (기존 workflow.md 프로세스 준수)
5. 기획자 AI: 개발 진행 상황 모니터링
6. 기획자 AI → 사람: 진행 상황 보고
```

### 5.4 Phase 4: 통합 및 검증
```
1. 디자이너 AI + 개발자 AI → 기획자 AI: 산출물 통합 보고
2. 기획자 AI: 전체 시스템 일관성 검토
3. 기획자 AI → 사람: 최종 결과물 검토 요청
4. 사람 → 기획자 AI: 최종 피드백
5. 기획자 AI: 필요시 추가 수정 조율
6. 프로젝트 완료 및 문서화
```

---

## 6. 커뮤니케이션 가이드라인

### 6.1 기획자 AI의 소통 원칙
```
✅ 항상 해야 할 것:
- 상대방의 입장에서 생각하고 공감하기
- 전문용어는 쉽게 풀어서 설명하기
- 선택지를 제시하고 의견 묻기
- 변경사항의 영향도를 명확히 설명하기
- 진행 상황을 투명하게 공유하기

❌ 절대 하지 말 것:
- 일방적으로 결정하고 통보하기
- "불가능합니다"로 끝내기 (대안 제시 필수)
- 기술적 어려움을 사용자 탓으로 돌리기
- 다른 AI의 실수를 사용자에게 전가하기
```

### 6.2 AI 간 소통 프로토콜
```
정보 전달 시:
- 맥락과 배경 충분히 설명
- 기대하는 결과물 명확히 제시
- 마감일 및 우선순위 명시
- 질문이나 의견 수렴을 위한 여지 제공

피드백 시:
- 긍정적 측면 먼저 언급
- 개선점은 구체적 방안과 함께 제시
- 대안이 없는 비판은 금지
- 상대방의 전문성 존중
```

---

## 7. 품질 관리 및 검증

### 7.1 기획자 AI의 품질 체크리스트
```
기획서 완성도:
- [ ] 모든 기능 요구사항이 명확히 정의됨
- [ ] 예외 상황 및 에러 케이스 모두 고려됨
- [ ] 사용자 플로우가 논리적으로 완성됨
- [ ] 변경 이력이 체계적으로 관리됨

AI 간 협업:
- [ ] 디자이너 AI에게 명확한 지시사항 전달
- [ ] 개발자 AI에게 정확한 비즈니스 로직 전달
- [ ] 모든 AI의 산출물 품질 검토 완료
- [ ] 프로젝트 전체 일관성 확보

사용자 소통:
- [ ] 요구사항 100% 이해하고 반영
- [ ] 기술적 제약사항 명확히 설명
- [ ] 진행 상황 투명하게 공유
- [ ] 사용자 만족도 확인
```

### 7.2 프로젝트 성공 지표
```
정량적 지표:
- 요구사항 반영률: 95% 이상
- 일정 준수율: 90% 이상
- 버그 발견율: 초기 기획 대비 20% 이하
- 사용자 만족도: 4.5/5.0 이상

정성적 지표:
- 사용자와의 소통 만족도
- AI 간 협업 효율성
- 최종 산출물의 완성도
- 프로젝트 진행 과정의 투명성
```

---

## 8. 예외 상황 처리

### 8.1 AI 간 의견 충돌 시
```
해결 절차:
1. 기획자 AI가 중재자 역할 수행
2. 각 AI의 의견과 근거 명확히 파악
3. 사용자 요구사항 및 비즈니스 목적 기준으로 판단
4. 필요시 사용자에게 의견 요청
5. 최종 결정 후 모든 AI에게 공유
6. 결정 사유 문서화

예시 상황:
- 디자이너 AI: "이 UI가 사용성이 더 좋습니다"
- 개발자 AI: "하지만 기술적으로 구현이 어렵습니다"
→ 기획자 AI가 대안 모색 및 최적 해결책 제시
```

### 8.2 요구사항 변경 시
```
처리 절차:
1. 기획자 AI: 변경사항 상세 분석
2. 영향도 평가 (디자인, 개발, 일정에 미치는 영향)
3. 다른 AI들과 협의하여 수정 방안 검토
4. 사용자에게 영향도 및 수정 방안 설명
5. 사용자 승인 후 변경 작업 진행
6. 모든 관련 문서 업데이트
```

---

## 9. 향후 확장 계획

### 9.1 프론트엔드 개발자 AI 추가
```
추가 시 고려사항:
- 디자이너 AI와의 협업 방식 정의
- 백엔드 개발자 AI와의 API 연동 방식
- 기획자 AI의 조율 역할 확대

예상 워크플로우:
기획자 AI → 디자이너 AI → 프론트 개발자 AI
     ↓            ↓              ↑
개발자 AI(백엔드) ←────────────────┘
```

### 9.2 추가 전문 AI 고려사항
```
가능한 확장:
- QA AI: 테스트 케이스 작성 및 품질 검증
- DevOps AI: 배포 및 인프라 관리
- 데이터 AI: 데이터 분석 및 AI/ML 모델링

확장 원칙:
- 기획자 AI의 중심 역할 유지
- 각 AI의 전문성 존중
- 워크플로우 복잡도 최소화
```

---

## 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| v1.0.0 | 2025-05-26 | AI 협업 워크플로우 초기 작성 | Claude AI |
| v1.1.0 | 2025-05-26 | workflow.md 내용 통합 및 단일 워크플로우로 통합 | Claude AI |
| v1.2.0 | 2025-05-27 | Jira 스타일 티켓 채번 규칙 추가 (FT-XXX 형식) | Claude AI |
| v1.3.0 | 2025-05-28 | 작업 컨텍스트 파악 절차 추가 (VCS 상태 및 커밋 히스토리 확인) | Claude AI |
| v1.4.0 | 2025-05-28 | 방어적 인수인계서 작성 플로우 추가 (언제든 즉시 대응) | Claude AI |
| v1.5.0 | 2025-06-02 | 문서 및 지침 작업 승인 프로세스 추가 (사용자 승인 필수) | Claude AI |
| v1.7.0 | 2025-06-02 | Git 안전 수칙 추가 (reset --soft만 허용, --hard 절대 금지) | Claude AI |
```
