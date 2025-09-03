# Family Tree Project - Claude Code Configuration

## 🚨 세션 시작 시 필수 실행 (Compact 후에도 재실행)

다음 명령어를 순서대로 실행하여 모든 지침을 읽어주세요:

  ```bash
  # 1. 워크플로우 지침
  cat be/instructions/workflow/ai-collaboration-workflow.md

  # 2. 코딩 컨벤션
  cat be/instructions/coding-conventions.md

  # 3. 커밋 가이드라인
  cat be/instructions/commit-guidelines.md

  # 4. 테스트 작성 규칙
  cat be/instructions/test-conventions.md

  # 5. 아키텍처 규칙
  cat be/instructions/architecture-rules.md

  # 6. API 설계 규칙
  cat be/instructions/api-design.md

  # 7. 현재 작업 상태 확인
  todo read

  지침 요약

  - git 명령어는 항상 --no-pager 사용
  - 커밋은 -F 옵션으로 파일 커밋 후 삭제
  - git reset --hard 절대 금지
  - be/instructions/ 수정 시 승인 필요
