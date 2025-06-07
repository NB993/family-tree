# 기획서: 프론트엔드 아키텍처 및 SEO 전략 수립

## 문서 정보
- **프로젝트명**: 프론트엔드 아키텍처 및 SEO 전략 수립
- **티켓 번호**: PM-024
- **작성일**: 2025-06-07
- **버전**: v1.0
- **작성자**: 기획자 AI

---

## 1. 목표 및 배경 (Why)

### 1.1 프로젝트 목적
Family Tree 서비스의 프론트엔드 아키텍처를 확정하고, SEO가 필요한 영역과 전략을 명확히 정의하여 개발 방향성을 수립하는 것이 목표

### 1.2 핵심 배경 및 문제 인식

#### 🎯 **학습 목적 우선**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
현재 상황: React 학습을 통한 프론트엔드 역량 강화가 주요 목적
문제점: Next.js 도입 시 학습 복잡도 급증 및 React 순수 학습 방해
해결 필요: React 기본기 학습에 집중할 수 있는 단순한 구조 필요
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### 🔍 **SEO 요구사항의 현실적 분석**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
Family Tree 서비스 특성:
- 프라이빗 가족 서비스 (공개적 검색 대상 아님)
- 로그인 필수 기능 (대부분 콘텐츠가 인증 후 접근)
- 가족 간 직접 초대가 주요 유입 경로 (바이럴 > SEO)

실제 SEO 필요 영역:
- 랜딩페이지: 서비스 소개 및 첫 인상
- 회원가입/로그인 페이지: 신규 유입 지원
- 기능 소개 페이지: 사용법 및 가치 전달
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### ⚡ **개발 효율성 및 복잡도 관리**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
현재 과제:
- OAuth2 JWT 인증 시스템 구축 필요
- React와 Spring Boot 연동 이슈 해결
- CORS 설정 및 프론트-백 통신 최적화

복잡도 증가 요인:
- Next.js 도입 시 SSR/SSG 학습 부담
- 3개 프로젝트(Next.js 랜딩 + React 앱 + Spring Boot) 관리
- 배포 복잡도 및 인프라 관리 포인트 증가
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

---

## 2. 의사결정 과정 및 대안 분석

### 2.1 검토된 아키텍처 옵션들

#### Option 1: Next.js 완전 마이그레이션 ❌
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
구조: Next.js 하나로 모든 것 해결
장점: 완벽한 SEO 지원, 하이브리드 렌더링, 확장성
단점: 
- React 학습 방해 (Next.js 문법, SSR/SSG 개념 추가)
- 기존 React 코드 마이그레이션 작업 필요
- 학습 목적과 상충
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### Option 2: 3개 프로젝트 분리 ❌
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
구조: Next.js 랜딩 + React 앱 + Spring Boot
장점: 각 기술의 장점 최대 활용
단점:
- 관리 복잡도 극대화 (3개 포트, 3개 배포)
- 개발 환경 설정 복잡
- CORS 및 도메인 관리 이슈
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### Option 3: 하이브리드 아키텍처 (선택) ✅
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
구조: Spring Boot(랜딩+API) + React(SPA)
장점:
- React 학습에 최적화된 환경
- SEO 필요 영역만 선택적 최적화
- 관심사 분리를 통한 개발 효율성
- 단순한 배포 및 관리 구조
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 2.2 최종 선택 근거

#### ✅ **React 학습 목적 달성**
- CRA 기반 순수 React 환경 유지
- Next.js 복잡성 배제로 React 기본기 집중
- 기존 개발 환경 그대로 활용 가능

#### ✅ **실용적 SEO 전략**
- 실제 필요한 영역(랜딩페이지)만 SEO 최적화
- 과도한 SEO 투자 방지 (ROI 고려)
- 점진적 확장 가능성 확보

#### ✅ **개발 및 운영 효율성**
- 2개 프로젝트로 복잡도 최소화
- 명확한 역할 분리 (백엔드: 랜딩+API, 프론트: SPA)
- CORS 설정만으로 간단한 연동

---

## 3. 확정된 아키텍처 설계

### 3.1 전체 시스템 구조

#### 🏗️ **개발 환경**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
포트 8080: Spring Boot
├── / → SEO 최적화 랜딩페이지 (Thymeleaf)
├── /about → 서비스 소개 페이지
├── /login-guide → 로그인 안내 페이지
└── /api/** → REST API

포트 3000: React 개발 서버
└── /** → Family Tree SPA (OAuth2 JWT 인증)
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### 🌐 **프로덕션 환경**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
familytree.com: Spring Boot
├── / → 랜딩페이지 (완벽 SEO)
├── /about → 서비스 소개
└── /api/** → API

app.familytree.com: React 빌드 (정적 호스팅)
└── /** → Family Tree SPA
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 3.2 기술 스택 정의

#### 🎯 **백엔드 (Spring Boot)**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
랜딩페이지 렌더링:
- Thymeleaf: 서버 사이드 템플릿 엔진
- Bootstrap/TailwindCSS: 반응형 디자인
- SEO 메타태그: 완벽한 검색엔진 최적화

API 서버:
- Spring Security: OAuth2 JWT 인증
- REST API: Family Tree 비즈니스 로직
- CORS 설정: React 앱과의 통신 지원
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### ⚛️ **프론트엔드 (React)**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
SPA 구조:
- Create React App: 순수 React 환경
- TypeScript: 타입 안전성
- React Query: 서버 상태 관리
- React Router: 클라이언트 사이드 라우팅

인증 시스템:
- JWT 토큰 기반 인증
- Axios 인터셉터: 자동 토큰 헤더 첨부
- Protected Route: 인증 기반 접근 제어
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 3.3 SEO 전략 세부사항

#### 🔍 **SEO 최적화 대상 페이지**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
백엔드에서 서버 렌더링:
1. 랜딩페이지 (/) - 서비스 핵심 가치 전달
2. 서비스 소개 (/about) - 기능 및 특징 설명
3. 로그인 안내 (/login-guide) - 시작 방법 가이드
4. 도움말/FAQ (/help) - 검색 유입 대응

SEO 최적화 요소:
- 메타 타이틀/디스크립션 최적화
- Open Graph 태그 (소셜 미디어 공유)
- 구조화 데이터 (Schema.org)
- 사이트맵 및 robots.txt
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### ❌ **SEO 제외 영역**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
React SPA에서 클라이언트 렌더링:
- Family 홈 대시보드 (로그인 필수)
- 가족 구성원 관리 (프라이빗 데이터)
- 설정 및 프로필 관리 (개인 정보)
- 모든 인증 필요 기능

제외 근거:
- 로그인 없이 접근 불가 (검색엔진 크롤링 불가)
- 프라이빗 데이터 (공개 검색 대상 아님)
- 사용자별 개인화 콘텐츠 (SEO 의미 없음)
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

---

## 4. 구현 계획 및 단계

### 4.1 Phase 1: 백엔드 랜딩페이지 구축

#### Story-1: Thymeleaf 랜딩페이지 시스템 구현
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
목표: SEO 최적화된 랜딩페이지 서빙 시스템 구축

구현 내용:
- Thymeleaf 의존성 추가 및 설정
- 랜딩페이지 컨트롤러 구현
- SEO 메타태그 템플릿 시스템
- 반응형 디자인 적용

완료 조건:
- / 경로에서 완벽한 HTML 서빙
- 검색엔진 크롤링 테스트 통과
- 소셜 미디어 OG 태그 검증
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### Story-2: 핵심 랜딩 페이지 콘텐츠 작성
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
목표: Family Tree 서비스 가치를 효과적으로 전달하는 콘텐츠 구성

페이지 구성:
- 홈페이지: 핵심 가치 제안 및 CTA
- 서비스 소개: 주요 기능 및 특징
- 시작하기: 회원가입 유도 및 안내

SEO 키워드:
- 가족트리, 가족관계, 가족구성원
- 디지털 가족앨범, 가족 소통
- 가족 관리 서비스
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 4.2 Phase 2: React OAuth2 JWT 인증 시스템

#### Story-3: JWT 기반 인증 시스템 구현
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
목표: React 앱에서 Spring Boot OAuth2와 연동하는 JWT 인증 구현

백엔드 개발:
- OAuth2 성공 시 JWT 토큰 발급 API
- JWT 토큰 검증 필터 구현
- 토큰 갱신(Refresh) 로직

프론트엔드 개발:
- 로그인 플로우 구현 (OAuth2 리다이렉트)
- JWT 토큰 저장 및 관리
- API 요청 인터셉터 (자동 헤더 첨부)
- Protected Route 컴포넌트
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### Story-4: React 앱 인증 상태 관리
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
목표: 사용자 인증 상태를 효율적으로 관리하는 시스템 구현

구현 내용:
- Context API 기반 인증 상태 관리
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 및 토큰 만료 처리
- 인증 에러 핸들링
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 4.3 Phase 3: 통합 및 배포 최적화

#### Story-5: 랜딩페이지 ↔ React 앱 연결
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
목표: 랜딩페이지에서 React 앱으로의 자연스러운 사용자 플로우 구현

연결 플로우:
1. 랜딩페이지에서 "시작하기" 클릭
2. React 앱 로그인 페이지로 리다이렉트
3. OAuth2 로그인 후 Family 홈으로 이동

기술 구현:
- 랜딩페이지 CTA 버튼 최적화
- React 앱 진입점 URL 설정
- 사용자 플로우 추적 및 분석
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### Story-6: CORS 및 도메인 설정
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
목표: 개발/운영 환경에서 안정적인 프론트-백 통신 환경 구축

개발 환경:
- localhost:8080 ↔ localhost:3000 CORS 설정
- 개발용 환경변수 및 설정 분리

운영 환경:
- familytree.com ↔ app.familytree.com 도메인 설정
- HTTPS 인증서 및 보안 설정
- CDN 및 정적 호스팅 최적화
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

---

## 5. 기술적 고려사항

### 5.1 성능 최적화

#### 🚀 **랜딩페이지 성능**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
최적화 전략:
- 이미지 최적화 (WebP, 적절한 크기)
- CSS/JS 번들링 및 압축
- 브라우저 캐싱 헤더 설정
- CDN 활용 (정적 자원)

측정 목표:
- Lighthouse 성능 점수 90+ 
- 첫 페이지 로딩 시간 < 2초
- Core Web Vitals 기준 충족
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### ⚛️ **React 앱 성능**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
최적화 전략:
- Code Splitting (React.lazy)
- 번들 크기 최적화
- React Query 캐싱 전략
- 이미지 지연 로딩

측정 목표:
- 초기 번들 크기 < 500KB
- 라우트별 청크 로딩 < 1초
- API 응답 캐싱 효율성
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 5.2 보안 고려사항

#### 🔐 **JWT 토큰 보안**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
보안 전략:
- HttpOnly 쿠키 vs localStorage 선택
- 토큰 만료 시간 최적화 (1시간)
- Refresh Token 로테이션
- XSS/CSRF 공격 방지

추가 보안:
- API Rate Limiting
- 로그인 시도 제한
- 의심스러운 접근 감지
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### 🌐 **CORS 및 도메인 보안**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
보안 설정:
- 허용 오리진 명시적 설정
- Preflight 요청 최적화
- 쿠키 SameSite 설정
- HSTS 헤더 적용
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 5.3 위험 요소 및 대응 방안

#### ⚠️ **기술적 리스크**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
리스크: React-Spring Boot JWT 연동 복잡성
확률: 중간
영향: 높음
대응: OAuth2 + JWT 튜토리얼 사전 학습, 단계별 구현

리스크: CORS 설정 이슈
확률: 높음  
영향: 중간
대응: 개발 환경 사전 테스트, 명확한 설정 문서화

리스크: SEO 효과 미달
확률: 낮음
영향: 낮음
대응: Family Tree 특성상 SEO 의존도 낮음, 문제시 Next.js 마이그레이션
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### 📚 **학습 리스크**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
리스크: React 학습 부족으로 인한 개발 지연
확률: 중간
영향: 중간  
대응: 기본기 우선 학습, 복잡한 기능 단계적 구현

리스크: JWT 인증 시스템 이해 부족
확률: 중간
영향: 높음
대응: 인증 시스템 사전 스터디, 샘플 프로젝트 구현
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

### 5.4 확장성 및 마이그레이션 계획

#### 🔄 **향후 Next.js 마이그레이션 시나리오**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
마이그레이션 조건:
- React 기본기 학습 완료 (6개월 후)
- SEO 요구사항 확대 (검색 유입 증가 시)
- 서비스 규모 확장 (사용자 1만명+)

점진적 마이그레이션 전략:
Phase 1: 랜딩페이지만 Next.js 변경
Phase 2: 공개 페이지 확장  
Phase 3: 전체 통합
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

#### 📱 **모바일 앱 확장 고려사항**
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.
현재 아키텍처의 장점:
- JWT 기반 인증 (모바일 완벽 호환)
- REST API 구조 (크로스 플랫폼 지원)
- 명확한 백엔드 분리 (API 재사용 가능)
```

---

## 부록

### A. 용어 정의
```
- 하이브리드 아키텍처: 백엔드 렌더링과 클라이언트 렌더링을 혼합한 구조
- SEO 선택적 최적화: 공개 페이지만 검색엔진 최적화 적용
- JWT 기반 인증: JSON Web Token을 활용한 Stateless 인증 방식
- Protected Route: 인증된 사용자만 접근 가능한 React 라우트
- CTA (Call To Action): 사용자 행동 유도 버튼 (시작하기, 가입하기 등)
```

### B. 참고 자료
```
기술 문서:
- React 공식 문서: https://react.dev
- Spring Security OAuth2: https://spring.io/projects/spring-security
- JWT 공식 사이트: https://jwt.io
- Thymeleaf 공식 문서: https://www.thymeleaf.org

SEO 가이드:
- Google SEO 가이드: https://developers.google.com/search/docs
- 구조화 데이터: https://schema.org
- Core Web Vitals: https://web.dev/vitals

성능 최적화:
- Lighthouse: https://developers.google.com/web/tools/lighthouse
- React 성능 최적화: https://react.dev/learn/render-and-commit
- Web Vitals: https://web.dev/articles/vitals
```

### C. 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 변경 사유 | 영향도 | 작성자 |
|------|------|-----------|-----------|--------|--------|
| v1.0.0 | 2025-06-07 | 초기 기획서 작성 - 프론트엔드 아키텍처 확정 | React 학습 목적 + SEO 선택적 최적화 전략 수립 | 신규 아키텍처 결정으로 개발 방향 확정 | 기획자 AI |
```

**🎉 프론트엔드 아키텍처 및 SEO 전략 수립 기획 완료!**

React 학습 목적과 실용적 SEO 요구사항을 모두 만족하는 하이브리드 아키텍처 방향이 확정되었습니다.

## 📋 OAuth2 JWT 인증 시스템 개발 계획 (수정됨)

### 🎯 **Epic FT-006: OAuth2 JWT 인증 시스템 구현**

#### Phase 1: 백엔드 JWT 토큰 시스템 (1주차)

**Story FT-007: OAuth2 성공 핸들러 및 JWT 토큰 발급**
```
📋 작업 범위:
- OAuth2AuthenticationSuccessHandler 구현
- JWT 토큰 생성 유틸리티 (JwtTokenProvider)
- 토큰 발급 후 React 앱 리다이렉트 (쿠키에 토큰 포함)
- application.yml JWT 설정 추가

🔧 구현 단위:
1. JwtTokenProvider 클래스 작성 (토큰 생성/검증)
2. CustomOAuth2SuccessHandler 구현 (토큰 발급 + 리다이렉트)
3. JWT 설정 프로퍼티 추가
4. 단위 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: OAuth2 로그인 성공 시 JWT 토큰이 쿠키에 설정되고 React 앱으로 리다이렉트
```

**Story FT-008: JWT Security Filter 및 ME API**
```
📋 작업 범위:
- JwtAuthenticationFilter 구현 (모든 API 요청 토큰 검증)
- /api/me 엔드포인트 구현 (인증된 사용자 정보 조회)
- Spring Security 설정 업데이트
- 토큰 만료/무효 처리

🔧 구현 단위:
1. JwtAuthenticationFilter 클래스 작성
2. SecurityConfig JWT 필터 체인 설정
3. MeController 및 GetMeUseCase 구현
4. 토큰 에러 핸들링 (401, 403)
5. 통합 테스트 작성

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 유효한 JWT로 /api/me 호출 시 사용자 정보 반환, 무효 토큰 시 401 응답
```

**Story FT-009: CORS 설정 및 개발 환경 구성**
```
📋 작업 범위:
- Spring Boot CORS 설정 (localhost:3000 허용)
- 쿠키 SameSite, Secure 설정
- 개발/운영 환경별 설정 분리

🔧 구현 단위:
1. WebMvcConfigurer CORS 설정
2. 쿠키 옵션 설정 (SameSite=Lax, HttpOnly)
3. 환경별 프로퍼티 설정 (dev/prod)
4. CORS 동작 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: React 개발서버(3000)에서 Spring Boot(8080) API 호출 성공
```

#### Phase 2: React 인증 클라이언트 (2주차)

**Story FT-010: React JWT 인증 상태 관리**
```
📋 작업 범위:
- AuthContext 및 AuthProvider 구현
- 쿠키에서 JWT 토큰 추출 로직
- 자동 로그인 (토큰 유효성 체크)
- 로그아웃 처리

🔧 구현 단위:
1. AuthContext 생성 (React Context API)
2. 쿠키 유틸리티 함수 (js-cookie 라이브러리)
3. useAuth 커스텀 훅 구현
4. 자동 로그인 useEffect 처리
5. 로그아웃 함수 (쿠키 삭제 + 상태 초기화)

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 페이지 새로고침 시 자동 로그인, 로그아웃 시 인증 상태 해제
```

**Story FT-011: API 클라이언트 및 인터셉터**
```
📋 작업 범위:
- Axios 인스턴스 생성 및 설정
- 요청 인터셉터 (JWT 헤더 자동 첨부)
- 응답 인터셉터 (401 에러 처리)
- API 클라이언트 함수들

🔧 구현 단위:
1. apiClient.js 파일 생성 (Axios 설정)
2. 요청 인터셉터 (Authorization: Bearer 헤더)
3. 응답 인터셉터 (401 시 로그인 페이지 리다이렉트)
4. getMeInfo API 함수
5. 에러 핸들링 테스트

⏱️ 예상 소요시간: 1일
✅ 완료 조건: 모든 API 요청에 JWT 자동 첨부, 토큰 만료 시 자동 로그아웃
```

**Story FT-012: Protected Route 및 인증 가드**
```
📋 작업 범위:
- ProtectedRoute 컴포넌트 구현
- 라우팅 설정 업데이트
- 로딩 상태 처리
- 인증 필요 페이지들 보호

🔧 구현 단위:
1. ProtectedRoute 컴포넌트 작성
2. React Router 설정 업데이트
3. 인증 로딩 스피너 컴포넌트
4. 로그인 페이지 구현 (OAuth2 링크)
5. 인증 플로우 전체 테스트

⏱️ 예상 소요시간: 2일
✅ 완료 조건: 비인증 사용자 접근 시 로그인 페이지 리다이렉트, 인증 후 원래 페이지 복귀
```

#### Phase 3: 통합 테스트 및 최적화 (3주차)

**Story FT-013: 전체 인증 플로우 통합 테스트**
```
📋 작업 범위:
- E2E 인증 플로우 테스트
- 토큰 만료 시나리오 테스트
- 브라우저 호환성 테스트
- 성능 최적화

🔧 구현 단위:
1. 전체 인증 플로우 시나리오 테스트
2. 토큰 만료/갱신 테스트 케이스
3. 멀티 탭/브라우저 동기화 테스트
4. 메모리 누수 및 성능 체크
5. 문서화 (인증 플로우 다이어그램)

⏱️ 예상 소요시간: 3일
✅ 완료 조건: 모든 인증 시나리오 정상 동작, 성능 기준 충족
```

### 📊 **개발 일정 요약**

| Story | 담당 | 예상기간 | 의존성 |
|-------|------|----------|--------|
| FT-007 | 백엔드 | 2일 | 없음 |
| FT-008 | 백엔드 | 2일 | FT-007 |
| FT-009 | 백엔드 | 1일 | FT-008 |
| FT-010 | 프론트엔드 | 2일 | FT-009 |
| FT-011 | 프론트엔드 | 1일 | FT-010 |
| FT-012 | 프론트엔드 | 2일 | FT-011 |
| FT-013 | 풀스택 | 3일 | FT-012 |

**총 예상 기간: 13일 (약 2.5주)**

### 🔄 **변경 사유**
React 학습 목적에 집중하고, OAuth2 JWT 인증이 모든 프론트엔드 개발의 전제 조건이므로 최우선 진행. 각 Story를 독립적으로 개발 가능하도록 의존성을 명확히 정의.

---

<!-- 2/3 완료 -->
