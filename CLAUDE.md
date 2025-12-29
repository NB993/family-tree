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

### 사용 가능한 스킬 (7개)

| 스킬 | 용도 |
|------|------|
| `write-prd` | PRD 작성 (시니어 전문가 검토) |
| `prd-to-test` | PRD에서 테스트 케이스 도출 |
| `core-tdd` | Domain, UseCase, Service, Command/Query TDD 개발 |
| `infra-tdd` | JpaEntity, Adapter, Repository TDD 개발 |
| `presentation-tdd` | Controller, Request/Response DTO TDD 개발 |
| `refactor` | 리팩토링 전용 |
| `commit` | 커밋 메시지 작성 |

## TDD 워크플로우

```
write-prd → prd-to-test → core-tdd → infra-tdd → presentation-tdd → commit
    │            │             │           │              │
    ▼            ▼             ▼           ▼              ▼
PRD 작성    테스트 케이스   Red→Green   Red→Green    Red→Green
(전문가검토)    도출       →Refactor   →Refactor    →Refactor
```

### TDD 순서

1. **PRD 작성** (write-prd): 기능 아이디어 → PRD (시니어 전문가 검토)
2. **PRD 분석** (prd-to-test): 요구사항 → 테스트 케이스 도출
3. **코어 계층** (core-tdd): 테스트 → Domain/Service 구현
4. **인프라 계층** (infra-tdd): 테스트 → Adapter 구현
5. **프레젠테이션 계층** (presentation-tdd): 테스트 → Controller 구현
6. **커밋** (commit): 커밋 메시지 작성

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
