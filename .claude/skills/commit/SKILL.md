---
name: commit
description: Git 커밋 메시지 작성 지침. 커밋 타입(feat/fix/test/docs/refactor), 협업구분([by-ai]/[with-ai]), 본문 형식 준수. 테스트 통과 후에만 커밋. git reset --hard 절대 금지.
---

# 커밋 작성 지침

## 커밋 메시지 형식

```
{타입} [{협업구분}] {구현내용}
```

### 타입
| 타입 | 설명 |
|------|------|
| `feat` | 기능 구현 |
| `test` | 테스트만 작성/수정 |
| `fix` | 버그 수정 |
| `docs` | 문서 작성/수정 |
| `refactor` | 코드 리팩토링 |

### 협업구분
| 구분 | 설명 |
|------|------|
| `[by-ai]` | AI가 오롯이 구현 |
| `[with-ai]` | 사람과 AI가 협력하여 구현 |

## 금지사항

- `git reset --hard` **절대 금지**
- `git push --force` 사용 금지
- main 브랜치 직접 커밋 금지
- 테스트 통과 전 커밋 금지

## 상세 지침

- **커밋 가이드라인**: [guidelines.md](guidelines.md)
