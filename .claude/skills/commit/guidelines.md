# 커밋 가이드라인

## 커밋 메시지 형식

```
{타입} {간결한 설명}

{본문}

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
```

## 타입

- `feat`: 기능 구현
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `test`: 테스트 작성/수정
- `docs`: 문서 작성/수정
- `chore`: 기타 (빌드, 설정 등)

## 본문 작성 가이드

### feat (기능 구현)
구현한 내용을 간결하게 나열:
```
feat 태그 관리 기능 구현

- 노션 스타일 인라인 태그 관리 UX 적용
- 멤버 상세 바텀 시트에서 태그 CRUD 가능
- React Query로 서버 상태 관리
```

### fix, refactor (수정/개선)
**왜 변경했는지를 먼저** 작성:
```
fix 태그 API 응답 형식 불일치 수정

FE에서 { tags: [], totalCount } 형식을 기대했으나
BE는 Tag[] 배열을 직접 반환하고 있었음

- FE 타입 정의를 BE 응답 형식에 맞게 수정
- TagSelector, TagManagement 컴포넌트 수정
```

```
refactor API 서비스 계층 간소화

중복된 에러 핸들링 로직이 각 서비스에 산재해 있어
유지보수가 어려웠음

- 공통 에러 핸들링을 ApiClient로 통합
- 각 서비스에서 중복 코드 제거
```

## 테스트 확인

커밋 전 반드시 테스트 통과 확인:
```bash
# 백엔드
cd be && ./gradlew test

# 프론트엔드
cd fe && npm test
```

## 금지사항

- `git reset --hard` **절대 금지**
- `git push --force` 사용 금지
- 테스트 실패 상태에서 커밋 금지