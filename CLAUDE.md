# Family Tree Project - Claude Code Configuration

## 스킬 기반 협업

이 프로젝트는 Claude Agent Skills를 사용합니다.
작업 맥락에 따라 관련 스킬이 자동으로 로드됩니다.

### 사용 가능한 스킬 (8개)

| 스킬 | 용도 |
|------|------|
| `core-develop` | Domain, UseCase, Service, Command/Query 개발 |
| `infra-develop` | JpaEntity, Adapter, Repository 개발 |
| `presentation-develop` | Controller, Request/Response DTO 개발 |
| `core-unit-test` | 코어 계층 단위 테스트 |
| `infra-unit-test` | 인프라 계층 단위 테스트 |
| `acceptance-test` | 인수 테스트 |
| `api-docs-test` | API 문서 테스트 |
| `commit` | 커밋 메시지 작성 |

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

## 개발 순서

1. **코어**: Domain → UseCase → Service → Command/Query
2. **인프라**: JpaEntity → Adapter → Repository
3. **프레젠테이션**: Controller → Request/Response DTO

## 레거시 지침 (참고용)

상세 지침은 스킬에서 자동 로드됩니다.
레거시 참조가 필요한 경우: `be/instructions/index.md`

## important-instruction-reminders

Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files.
