# PR 생성 가이드라인

## CodeRabbit 최적화 원칙

CodeRabbit은 PR을 커밋 단위로 점진적 리뷰합니다. 효과적인 리뷰를 위해:

1. **명확한 변경 목적** - Why를 먼저 설명
2. **구조화된 변경 내용** - What을 체계적으로 나열
3. **테스트 계획 명시** - 검증 방법 제시
4. **AI 요약 위치 지정** - `@coderabbitai summary` 활용

## PR 제목 형식

```
{타입}: {간결한 설명}
```

### 타입
- `feat`: 새 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `docs`: 문서
- `test`: 테스트
- `chore`: 기타 (빌드, 설정 등)

### 예시
```
feat: 가족 구성원 수동 등록 API 구현
fix: 이미지 로딩 시 깜빡임 현상 수정
refactor: 인증 모듈 헥사고날 아키텍처 적용
```

## PR 본문 템플릿

```markdown
## 변경 목적 (Why)

<!-- 이 PR이 필요한 이유를 설명합니다 -->
-

## 변경 내용 (What)

<!-- @coderabbitai가 이 위치에 자동 요약을 생성합니다 -->
@coderabbitai summary

### 주요 변경사항
-

### 구현된 컴포넌트

#### 도메인 계층 (해당시)
-

#### 애플리케이션 계층 (해당시)
-

#### 인프라 계층 (해당시)
-

#### 프레젠테이션 계층 (해당시)
-

## 테스트 계획

- [ ] 단위 테스트 통과
- [ ] 통합 테스트 통과
- [ ] API 문서 테스트 통과

### 테스트 실행 결과
```
./gradlew test 결과 붙여넣기
```

## 관련 문서

- PRD: <!-- PRD 파일 경로 또는 링크 -->
- 이슈: <!-- 관련 이슈 번호 -->

## 체크리스트

- [ ] 코드가 프로젝트 컨벤션을 따름
- [ ] 테스트가 모두 통과함
- [ ] 문서가 업데이트됨 (필요시)
- [ ] 보안 취약점 검토 완료

## 리뷰어 참고사항

<!-- CodeRabbit이 특별히 주의해서 봐야 할 부분이 있다면 명시 -->
-
```

## AI 작업 절차

### Step 1: 사전 검증

```bash
# 현재 브랜치 확인
git branch --show-current

# 테스트 통과 확인
./gradlew test

# 변경 사항 확인
git status
git log main..HEAD --oneline
```

### Step 2: 변경 분석

```bash
# base 브랜치 대비 전체 변경 확인
git diff main...HEAD --stat

# 커밋 히스토리 분석
git log main..HEAD --pretty=format:"%h %s"
```

### Step 3: PR 본문 작성

1. 커밋 메시지들을 분석하여 변경 목적 추론
2. 변경된 파일들을 계층별로 분류
3. PRD 문서가 있다면 링크 포함
4. 테스트 결과 포함

### Step 4: PR 생성

```bash
# GitHub CLI로 PR 생성
gh pr create \
  --title "feat: 기능 설명" \
  --body "$(cat <<'EOF'
## 변경 목적 (Why)

...본문 내용...

EOF
)"
```

## CodeRabbit 명령어 참고

PR 생성 후 CodeRabbit과 상호작용:

| 명령어 | 설명 |
|--------|------|
| `@coderabbitai review` | 점진적 리뷰 요청 |
| `@coderabbitai full review` | 전체 리뷰 요청 |
| `@coderabbitai summary` | PR 요약 생성 |
| `@coderabbitai ignore` | PR 리뷰 제외 |

## 금지사항

### 절대 금지
- `git push --force` - 협업 히스토리 파괴
- main/master 브랜치에서 직접 작업

### 주의사항
- 테스트 실패 상태에서 PR 생성 금지
- 너무 큰 PR 지양 (500줄 이하 권장)

## PR 생성 전 체크리스트

- [ ] `./gradlew test` 통과
- [ ] 현재 브랜치가 main/master가 아님
- [ ] 원격에 브랜치 push 완료
- [ ] PR 제목 형식 준수
- [ ] 변경 목적(Why) 명시
- [ ] 테스트 계획 포함
- [ ] `@coderabbitai summary` 태그 포함