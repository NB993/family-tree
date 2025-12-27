# Family Tree Project - Claude Code Configuration

## 🚨 필수 규칙 요약

### 필수 명명 규칙
| 동작 | 접두사 | 금지 |
|------|--------|------|
| 조회 | Find | Get, Query, Retrieve |
| 등록 | Save | Create, Add, Insert |
| 수정 | Modify | Update, Edit, Change |
| 삭제 | Delete | Remove, Erase |

### 정적 팩토리 메서드
- 신규 생성: `newXxx()` (예: `Family.newFamily(...)`)
- 복원: `withId()` (예: `Family.withId(...)`)
- 금지: `of()`, `create()`, `from()` (from은 JpaEntity 전용)

### UseCase 메서드명
- 단건 조회: `find(Query query)` - Query 클래스명으로 의도 구분
- 복수 조회: `findAll(Query query)`
- 금지: `findById()`, `findByEmail()` 등 메서드명으로 구분

### Query/Command 객체
- 반드시 `record` 타입으로 작성
- 생성자에서 유효성 검증 수행
- 예외 메시지는 한글로 작성

## 🚫 금지사항
- `git reset --hard` 절대 금지
- `be/instructions/` 수정 시 승인 필요
- `@Builder` 패턴 사용 금지
- JpaEntity에서 setter 사용 금지

## 📋 개발 순서
1. **코어**: Domain → UseCase → Service → Command/Query
2. **인프라**: JpaEntity → Adapter → Repository
3. **프레젠테이션**: Controller → Request/Response DTO

## 📚 상세 지침 (필요시 Read)
- **명명 규칙**: `be/instructions/naming-conventions.md`
- **아키텍처**: `be/instructions/architecture-overview.md`
- **코딩 스타일**: `be/instructions/coding-standards.md`
- **테스트**: `be/instructions/testing-guidelines.md`
- **커밋**: `be/instructions/commit-guidelines.md`
- **지침 목차**: `be/instructions/index.md`

## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files.
