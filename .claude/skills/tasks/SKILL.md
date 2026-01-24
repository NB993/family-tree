---
name: tasks
description: 현재 작업 상태를 파악하고, GitHub 이슈/PR을 분석하여 업무 우선순위와 추천 작업을 제안합니다
---

# Tasks - 업무 분석 스킬

## 실행 절차

### 1단계: 현재 작업 상태 파악 (최우선)

```bash
# 현재 브랜치
git branch --show-current

# 미커밋 변경사항
git status --short

# 최근 커밋 (현재 브랜치, 5개)
git log -5 --oneline

# 현재 브랜치가 main과 얼마나 다른지
git log main..HEAD --oneline 2>/dev/null || echo "main 브랜치 없음"
```

### 2단계: GitHub 데이터 수집

```bash
# 이슈 목록
gh issue list --state open --json number,title,labels,assignees,milestone,createdAt,updatedAt --limit 100

# PR 목록
gh pr list --state open --json number,title,headRefName,reviewDecision,isDraft,labels --limit 50
```

### 3단계: 리포트 생성

**[필수] 아래 가이드라인을 따라 분석 리포트를 작성하세요:**

- **가이드라인**: [guidelines.md](guidelines.md)

## 출력 형식

리포트는 다음 순서로 출력합니다:

1. **현재 작업 상태** - 지금 무엇을 하고 있었는지 (최우선)
2. **긴급 처리 필요 (P0)** - 버그, 보안, PR 변경 요청
3. **진행 중인 작업** - PR 리뷰 대기
4. **추천 작업 (Top 5)** - 우선순위 점수 기반
5. **카테고리별 분류** - frontend, backend, epic 등
6. **마일스톤 현황** - 스프린트 진행률 (있는 경우)
7. **오래된 이슈** - 30일 이상 방치된 이슈
8. **다음 액션 제안** - 구체적인 다음 행동
