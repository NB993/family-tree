# Family Tree Frontend

가족 관계를 따뜻하게 기록하고 관리하는 모바일 퍼스트 웹 애플리케이션입니다.

## 🎨 디자인 시스템

이 프로젝트는 **design-system-v1.md**를 기반으로 한 모바일 퍼스트 디자인 시스템을 사용합니다.

### 핵심 가치
- **따뜻함**: 오렌지 계열의 따뜻한 색상 팔레트
- **가독성**: Pretendard 폰트와 모바일 최적화된 타이포그래피
- **모바일 최적화**: 390px 최대 너비의 모바일 퍼스트 설계
- **장식적 우아함**: 부드러운 그라데이션과 깔끔한 인터페이스

### 색상 팔레트
- **Primary**: #F97316 (오렌지)
- **Secondary**: #F59E0B (앰버)
- **배경**: #FFFBF5 (따뜻한 화이트)

## 🏗️ 프로젝트 구조

```
src/
├── api/                    # API 클라이언트 및 서비스
│   ├── client.ts          # Axios 기반 HTTP 클라이언트
│   └── services/          # 도메인별 API 서비스
├── components/            # 재사용 가능한 컴포넌트
│   ├── common/           # 공통 UI 컴포넌트
│   ├── family/           # 가족 관련 컴포넌트
│   └── layout/           # 레이아웃 컴포넌트
├── design-system/        # 디자인 시스템
│   ├── tokens.ts         # 디자인 토큰 정의
│   ├── utils.ts          # 유틸리티 함수
│   ├── global.css        # 글로벌 스타일
│   └── index.ts          # 메인 export
├── hooks/                # 커스텀 훅
│   ├── queries/          # React Query 훅
│   └── useApiError.ts    # API 에러 처리 훅
├── pages/                # 페이지 컴포넌트
├── types/                # TypeScript 타입 정의
└── styles/               # 스타일 파일
```

## 🧩 컴포넌트 라이브러리

### 공통 컴포넌트
- **Button**: 다양한 variant와 size를 지원하는 버튼
- **Card**: 컨텐츠를 담는 카드 컨테이너
- **AppLayout**: 애플리케이션 전체 레이아웃

### 가족 컴포넌트
- **FamilyMemberCard**: 가족 구성원 정보를 표시하는 카드

## 📡 API 통합

### HTTP 클라이언트
- **ApiClient**: 싱글톤 패턴의 Axios 기반 클라이언트
- 중앙집중식 에러 처리
- 요청/응답 인터셉터

### 서비스 레이어
- **FamilyService**: 가족 관련 API 서비스
- **FamilyTreeService**: 가족 트리 시각화 API

### React Query 통합
- 서버 상태 관리
- 캐싱 및 동기화
- 낙관적 업데이트

## 🚀 시작하기

### 전제 조건
- Node.js 16+
- npm 또는 yarn

### 설치 및 실행
```bash
# 의존성 설치
npm install

# React Router DOM 추가 (리팩토링 후 필요)
npm install react-router-dom

# 개발 서버 시작
npm start
```

### 환경 변수
```env
REACT_APP_API_URL=http://localhost:8080
```

## 📱 페이지 구조

- **HomePage** (`/home`): 가족 목록 및 메인 대시보드
- **FamilyPage** (`/families/:familyId`): 가족 상세 정보
- **FamilyMembersPage** (`/families/:familyId/members`): 가족 구성원 목록
- **NotFoundPage** (`/404`): 404 에러 페이지

## 🎨 스타일링

### CSS 변수 기반
전체 디자인 시스템이 CSS 변수로 정의되어 일관성 있는 스타일링이 가능합니다.

```css
:root {
  --color-primary-500: #F97316;
  --font-family-sans: 'Pretendard', sans-serif;
  --spacing-4: 16px;
  /* ... */
}
```

### 모바일 퍼스트
모든 컴포넌트는 390px 기준으로 설계되었으며, 태블릿/데스크톱으로 확장됩니다.

## 🔗 백엔드 연동

헥사고날 아키텍처 기반의 백엔드 API와 연동:
- **Find/Save/Modify/Delete** 패턴
- **Family 권한 기반 접근 제어**
- **JWT 인증** (향후 추가 예정)

## 📋 향후 개발 계획

1. **모달 컴포넌트 완성**
    - 구성원 상세 정보 모달
    - 관계 설정 모달
    - 가족 생성/수정 모달

2. **인증 시스템 통합**
    - OAuth2/JWT 연동
    - 보호된 라우트

3. **성능 최적화**
    - 코드 스플리팅
    - 이미지 최적화
    - React.memo 적용

4. **접근성 개선**
    - ARIA 속성 추가
    - 키보드 네비게이션
    - 색상 대비 개선

5. **테스트 코드 추가**
    - 단위 테스트
    - 통합 테스트
    - E2E 테스트

## 🤝 기여하기

이 프로젝트는 다음 가이드라인을 따릅니다:
- **be/instructions/** 디렉토리의 개발 가이드라인
- **명명 규칙**: Find/Save/Modify/Delete 패턴
- **코딩 표준**: TypeScript strict 모드, ESLint

## Available Scripts

### `npm start`
개발 서버를 시작합니다. [http://localhost:3000](http://localhost:3000)에서 확인할 수 있습니다.

### `npm test`
테스트를 실행합니다.

### `npm run build`
프로덕션용 빌드를 생성합니다.

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다.
