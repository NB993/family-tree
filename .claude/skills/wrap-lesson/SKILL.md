---
name: wrap-lesson
description: 세션/커밋 학습 회고. 작업 내용을 깊이있게 분석하고 docs/lessons/에 학습 문서 생성.
---

# 세션 학습 회고 스킬

## 목적

안드로이드 개발 입문자를 위해, 작업 내용을 **아기에게 설명하듯** 깊이있게 분석합니다.

> "이건 뭐야?" → "그럼 이건 왜 그래?" → "그건 또 왜 그러는데?"
>
> 꼬리에 꼬리를 무는 질문에 답하는 엄마가 되어 설명합니다.

## 사용법

### 1. 현재 세션 분석 (기본)
```
/wrap-lesson
```

### 2. 특정 커밋 분석
```
/wrap-lesson 0cc7657                    # 커밋 SHA (짧은/긴 형식 모두 가능)
/wrap-lesson abc1234..def5678           # 커밋 범위
/wrap-lesson "feat: 홈 화면 구현"        # 커밋 메시지 검색
/wrap-lesson WAVER-123                  # 지라 티켓번호로 검색
```

## 상세 지침

**[필수] 아래 참조 문서를 읽은 후 작업을 시작하세요:**

- **작성 가이드라인**: [guidelines.md](guidelines.md)

## 출력 위치

- `docs/lessons/YYYY-MM-DD-{주제}.md` 형식으로 저장
