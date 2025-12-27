# 백엔드 개발 지침 목차

이 문서는 백엔드 개발 지침서의 목차입니다. AI는 개발을 시작하기 전, 반드시 각 문서의 내용을 모두 읽고 개발을 시작하세요.

## 개발 지침 문서 목록

### 필수 문서 (6개)

1. [명명 규칙 지침서](naming-conventions.md)
   - 필수 명명 규칙 (Find/Save/Modify/Delete)
   - 계층별 명명 규칙
   - 일반 명명 규칙
   - 정적 팩토리 메서드 규칙 (newXxx, withId)

2. [아키텍처 개요](architecture-overview.md)
   - 헥사고날 아키텍처 설계 원칙
   - 기술 스택 및 버전
   - 핵심 계층 구조 및 예시 코드
   - JPA 엔티티 설계 및 성능 최적화
   - 개발 순서 (코어 → 인프라 → 프레젠테이션)

3. [코드 작성 스타일 가이드라인](coding-standards.md)
   - 기본 원칙 및 코드 스타일
   - 예외 처리 규칙
   - 컬렉션 처리 및 관계 매핑
   - JPA 엔티티 작성 규칙
   - null 체크 규칙

4. [테스트 코드 작성 가이드라인](testing-guidelines.md)
   - 테스트 분류 (단위, 인수, API 문서)
   - 테스트 작성 시점 및 방법
   - 테스트 시 엔티티 생성 규칙
   - 테스트 실패 디버깅
   - 테스트 실행 방법

5. [커밋 가이드라인](commit-guidelines.md)
   - 커밋 메시지 작성 규칙
   - 커밋 작업 절차

6. [AI 협업 가이드라인](ai-collaboration-guidelines.md) *(선택적)*
   - 코드 품질 기준
   - Git 작업 주의사항
   - 예외 상황 처리

## 개발 프로세스 요약

백엔드 개발 프로세스는 다음과 같은 순서로 진행됩니다:

1. **코어 계층 (application)**
   - Domain → UseCase → Service → Command/Query
   - 단위 테스트 작성

2. **인프라 계층 (adapter/out)**
   - JpaEntity → Adapter → Repository
   - 어댑터 테스트 작성

3. **프레젠테이션 계층 (adapter/in)**
   - Controller → Request/Response DTO
   - 인수 테스트 작성

## 주요 명명 규칙 요약

| 동작 유형 | 사용할 접두사 | 잘못된 예시 |
|---------|------------|------------|
| 조회 | **Find** | Get, Retrieve, Query |
| 등록 | **Save** | Create, Add, Insert |
| 수정 | **Modify** | Update, Change, Edit |
| 삭제 | **Delete** | Remove, Erase |

모든 인바운드 어댑터, 인바운드 포트, 아웃바운드 포트는 반드시 위 접두사를 사용해야 합니다.