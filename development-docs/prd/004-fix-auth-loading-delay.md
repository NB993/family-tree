# PRD-004: 인증 완료 후 홈화면 로딩 지연 개선

## 문서 정보
- **작성일**: 2025-01-05
- **상태**: 🚧 진행 중
- **우선순위**: 중간
- **영향 범위**: 프론트엔드 (fe)

---

## 1. 배경 및 문제점

### 1.1 현재 상황
- OAuth 인증 성공 후 홈화면으로 이동
- 홈화면에서 목록이 **비어있다가 갑자기 나타남** (깜빡임 현상)

### 1.2 원인 분석

```
OAuth2CallbackPage:
  await authService.getCurrentUser()  // API 성공, 쿠키 설정됨
  navigate('/home')                   // 바로 이동
                                      // ❌ AuthContext는 아직 모름

AuthProvider:
  location.pathname 변경 감지
  → checkAuthStatus() 호출
  → API 호출 중... (isLoading=true)

HomePage:
  마운트됨 (AuthProvider보다 먼저)
  → useMyFamilies() 호출
  → 401 에러 또는 빈 응답
  → 빈 목록 표시

  ... API 재시도 성공 후 ...
  → 목록 빵 나타남
```

**핵심**: OAuth 콜백이 AuthContext에 "인증 완료"를 직접 알려주지 않음

### 1.3 설계 원칙
- 데이터 페칭 레이어는 인증 여부를 모름 (책임 분리)
- "인증이 끝난 후 들어온다"는 전제가 실제로 보장되어야 함
- httpOnly 쿠키 유지 (보안 다운그레이드 금지)

---

## 2. 목표

1. **인증 완료 시점 명확화**: OAuth 콜백에서 AuthContext 상태를 직접 확정
2. **깜빡임 현상 제거**: 홈화면 진입 시 이미 인증된 상태
3. **중복 API 호출 방지**: 이미 인증된 상태면 checkAuthStatus 스킵

---

## 3. 유스케이스

### UC-1: OAuth 인증 후 홈화면 진입

- **전제조건**: 사용자가 카카오 로그인 완료, OAuth 콜백 페이지 도착
- **기본 흐름**:
  1. OAuth2CallbackPage에서 `authService.getCurrentUser()` 호출
  2. 성공 시 `confirmAuthentication(userInfo)` 호출하여 AuthContext 상태 확정
  3. `navigate('/home')` 실행
  4. HomePage 마운트 시 이미 `isAuthenticated=true` 상태
  5. `useMyFamilies()` 정상 호출, 목록 즉시 표시
- **사후조건**: 깜빡임 없이 목록 표시

### UC-2: 인증된 상태에서 페이지 이동

- **전제조건**: 이미 `isAuthenticated=true` 상태
- **기본 흐름**:
  1. 사용자가 다른 페이지로 이동
  2. `location.pathname` 변경으로 AuthProvider useEffect 트리거
  3. `isAuthenticated=true`이므로 checkAuthStatus 스킵
  4. 불필요한 API 호출 없음
- **사후조건**: 빠른 페이지 전환

### UC-3: 토큰 만료/무효 상태에서 API 호출

- **전제조건**: `isAuthenticated=true`이지만 실제 토큰이 무효
- **기본 흐름**:
  1. 데이터 페칭 API 호출
  2. 서버에서 401 에러 반환
  3. 기존 에러 핸들러(A001, A002, A006)가 처리
  4. 로그인 페이지로 리다이렉트
- **사후조건**: 재로그인 유도

---

## 4. 변경 범위

### 4.1 AuthContext.tsx

| 항목 | 현재 | 변경 |
|------|------|------|
| confirmAuthentication | 없음 | **추가** |
| checkAuthStatus | 항상 실행 | isAuthenticated=true면 **스킵** |
| AuthContextType | logout만 | confirmAuthentication **추가** |

### 4.2 OAuth2CallbackPage.tsx

| 항목 | 현재 | 변경 |
|------|------|------|
| 인증 성공 처리 | navigate만 호출 | confirmAuthentication 후 navigate |

---

## 5. 상세 설계

### 5.1 confirmAuthentication 메서드

```typescript
// AuthContext.tsx
const confirmAuthentication = useCallback((userData: UserInfo) => {
  const authService = AuthService.getInstance();
  authService.saveUserInfo(userData);
  setUserInfo(userData);
  setIsAuthenticated(true);
  setIsLoading(false);
}, []);
```

### 5.2 checkAuthStatus 수정

```typescript
const checkAuthStatus = async () => {
  if (isPublicPath(location.pathname)) {
    setIsLoading(false);
    return;
  }

  // 이미 인증된 상태면 스킵
  if (isAuthenticated) {
    setIsLoading(false);
    return;
  }

  // ... 기존 인증 체크 로직
};
```

### 5.3 OAuth2CallbackPage 수정

```typescript
const { confirmAuthentication } = useAuth();

const handleOAuth2Success = async () => {
  const authService = AuthService.getInstance();
  const userInfo = await authService.getCurrentUser();

  confirmAuthentication(userInfo);  // 상태 확정
  navigate('/home');
};
```

---

## 6. 예외 케이스

| 상황 | 처리 |
|------|------|
| confirmAuthentication 후 API 401 | 기존 에러 핸들러가 로그인으로 리다이렉트 |
| OAuth 콜백에서 getCurrentUser 실패 | 기존 로직대로 로그인 페이지로 이동 |
| 새로고침 시 | checkAuthStatus 정상 실행 (isAuthenticated=false 상태) |

---

## 7. 영향 범위

### 7.1 수정 파일

| 파일 | 변경 내용 |
|------|----------|
| `fe/src/contexts/AuthContext.tsx` | confirmAuthentication 추가, checkAuthStatus 수정 |
| `fe/src/pages/OAuth2CallbackPage.tsx` | confirmAuthentication 호출 추가 |

### 7.2 테스트 영향

- AuthContext 단위 테스트 추가 필요
- OAuth 콜백 플로우 E2E 테스트 검증

---

## 8. 구현 순서

1. AuthContext에 confirmAuthentication 메서드 추가
2. checkAuthStatus에 isAuthenticated 체크 추가
3. OAuth2CallbackPage에서 confirmAuthentication 호출
4. 테스트 및 검증
