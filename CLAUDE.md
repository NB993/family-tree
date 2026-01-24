# Family Tree Project - Claude Code Configuration

## 프로젝트 개요

| 구분 | 기술 스택 |
|------|----------|
| Backend | Spring Boot 3.4.2, Java 21, Spring Security + OAuth2 |
| Frontend | React (`/fe`) |
| Mobile | Android (`/android`) |
| Database | PostgreSQL (로컬: Docker, 운영: PostgreSQL) |
| Test | JUnit 5, TestContainers, REST Docs, Rest Assured |
| Architecture | 헥사고날 아키텍처 (상세: Skills 참조) |

### 주요 명령어

```bash
cd be && ./gradlew test      # 테스트 실행
cd be && ./gradlew build     # 빌드 (API 문서 포함)
```

## TDD 기반 스킬 협업

이 프로젝트는 TDD 기반 Claude Agent Skills를 사용합니다.
작업 맥락에 따라 관련 스킬이 자동으로 로드됩니다.

### 스킬 사용 원칙

1. 작업 요청 시 **스킬 사용이 적절한지 먼저 판단**
2. 스킬을 사용한다면 **스킬 지침을 먼저 읽고** 작업 진행
3. CLAUDE.md의 규칙은 **요약**이며, 상세 규칙은 각 스킬 지침에 정의됨

### 사용 가능한 스킬 (8개)

| 스킬 | 용도 |
|------|------|
| `write-prd` | PRD 작성 (시니어 전문가 검토) |
| `prd-to-test` | PRD에서 테스트 케이스 도출 |
| `core-tdd` | Domain, UseCase, Service, Command/Query TDD 개발 |
| `infra-tdd` | JpaEntity, Adapter, Repository TDD 개발 |
| `presentation-tdd` | Controller, Request/Response DTO TDD 개발 |
| `refactor` | 리팩토링 전용 |
| `commit` | 커밋 메시지 작성 |
| `create-pr` | CodeRabbit 최적화 PR 생성 |

## TDD 워크플로우

```
write-prd → [/clear] → 코드베이스 탐색 → prd-to-test → core-tdd → infra-tdd → presentation-tdd → commit → create-pr
    │                        │                │             │           │              │            │           │
    ▼                        ▼                ▼             ▼           ▼              ▼            ▼           ▼
PRD 작성               PRD 기반으로      테스트 케이스   Red→Green   Red→Green    Red→Green    커밋 작성    PR 생성
(전문가검토)           관련 코드 파악       도출        →Refactor   →Refactor    →Refactor               (CodeRabbit)
```

### TDD 순서

1. **PRD 작성** (write-prd): 기능 아이디어 → PRD (시니어 전문가 검토)
2. **코드베이스 탐색** (수동): PRD 문서를 읽고 관련 코드 구조 파악 (`/clear` 후 컨텍스트 복구용)
3. **PRD 분석** (prd-to-test): 요구사항 → 테스트 케이스 도출
4. **코어 계층** (core-tdd): 테스트 → Domain/Service 구현
5. **인프라 계층** (infra-tdd): 테스트 → Adapter 구현
6. **프레젠테이션 계층** (presentation-tdd): 테스트 → Controller 구현
7. **커밋** (commit): 커밋 메시지 작성
8. **PR 생성** (create-pr): CodeRabbit 최적화 PR 생성

> **Note**: `/clear` 실행 후에는 PRD 문서 경로를 알려주고, 관련 코드베이스를 먼저 탐색하도록 요청하세요.

## 필수 규칙 요약

### 명명 규칙
| 동작 | 접두사 | 금지 |
|------|--------|------|
| 조회 | Find | Get, Query, Retrieve |
| 등록 | Save | Create, Add, Insert |
| 수정 | Modify | Update, Edit, Change |
| 삭제 | Delete | Remove, Erase |

### 정적 팩토리 메서드
- 신규 생성: `newXxx()`
- 복원: `withId()`
- 금지: `of()`, `create()`, `from()` (from은 JpaEntity 전용)

## Code Review Guidelines

PR 리뷰 시 `/code-review` 플러그인이 이 가이드라인을 참조합니다.

### 리뷰 관점
- **시니어 백엔드 개발자**: API 설계, 에러 핸들링, 트랜잭션 관리
- **시니어 프론트엔드 개발자**: 컴포넌트 구조, 상태 관리, 재사용성
- **시니어 UI/UX 디자이너**: 접근성, 일관성, 사용자 경험
- **시니어 DevOps 엔지니어**: CI/CD, 보안, 인프라 영향
- **비용 최적화 전문가**: 불필요한 연산, N+1 쿼리, 메모리 누수

### 피드백 우선순위
| 라벨 | 의미 | 예시 |
|------|------|------|
| `[P0-CRITICAL]` | 반드시 수정 필요 | 컴파일 에러, 보안 취약점, 데이터 손실 |
| `[P1-MAJOR]` | 수정 강력 권장 | 로직 오류, 성능 이슈, 규칙 위반 |
| `[P2-MINOR]` | 개선 제안 | 가독성, 네이밍, 중복 코드 |
| `[P3-NITPICK]` | 사소한 제안 | 스타일, 주석, 포맷팅 |

### 리뷰 제외 대상
- 린터가 잡는 이슈 (ESLint, Checkstyle 등)
- 이미 존재하던 문제 (PR에서 도입한 것만 검토)
- 주관적인 스타일 선호

## 금지사항

- `git reset --hard` **절대 금지**
- `@Builder` 패턴 사용 금지
- JpaEntity에서 setter 사용 금지
- `be/instructions/` 수정 시 승인 필요

## 레거시 지침 (참고용)

상세 지침은 스킬에서 자동 로드됩니다.
레거시 참조가 필요한 경우: `be/instructions/index.md`

## important-instruction-reminders

Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files.
