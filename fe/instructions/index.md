# 프론트엔드 개발 지침서

## 개요

본 문서는 Family Tree 프로젝트의 프론트엔드 개발을 위한 종합 가이드입니다. 일관된 코드 품질과 개발 효율성을 위해 모든 개발자는 이 지침을 준수해야 합니다.

## 주요 지침 문서

### 1. 아키텍처 및 설계
- [architecture-overview.md](./architecture-overview.md) - 프론트엔드 아키텍처 개요
- [component-design.md](./component-design.md) - 컴포넌트 설계 원칙
- [state-management.md](./state-management.md) - 상태 관리 전략

### 2. 개발 표준
- [coding-standards.md](./coding-standards.md) - 코드 작성 스타일 가이드
- [naming-conventions.md](./naming-conventions.md) - 명명 규칙
- [api-integration.md](./api-integration.md) - API 통합 가이드

### 3. 테스트 및 품질
- [testing-guidelines.md](./testing-guidelines.md) - 테스트 작성 가이드
- [performance-guidelines.md](./performance-guidelines.md) - 성능 최적화 가이드

### 4. 개발 프로세스
- [development-process.md](./development-process.md) - 개발 워크플로우
- [folder-structure.md](./folder-structure.md) - 폴더 구조 가이드

## 기술 스택

### 핵심 라이브러리
- **React**: 18.3.1
- **TypeScript**: 5.7.2
- **React Query (TanStack Query)**: 5.64.2
- **React Router DOM**: 7.1.1
- **Tailwind CSS**: 3.4.17

### 개발 도구
- **Vite**: 6.0.5
- **ESLint**: 9.17.0
- **Prettier**: 3.4.2
- **Vitest**: 2.1.8
- **React Testing Library**: 16.1.0

## 개발 환경 설정

### 필수 도구
1. Node.js 18.x 이상
2. npm 또는 yarn
3. Visual Studio Code (권장 IDE)

### 권장 VS Code 확장
- ESLint
- Prettier - Code formatter
- TypeScript and JavaScript Language Features
- Tailwind CSS IntelliSense
- Auto Rename Tag
- Path Intellisense

### 프로젝트 시작
```bash
cd fe
npm install
npm run dev
```

## 개발 원칙

### 1. 컴포넌트 우선 개발
- 재사용 가능한 컴포넌트 단위로 개발
- 컴포넌트의 단일 책임 원칙 준수
- Props 타입 명시적 정의

### 2. 타입 안정성
- TypeScript 엄격 모드 사용
- any 타입 사용 금지
- 모든 함수와 컴포넌트에 타입 명시

### 3. 성능 최적화
- 불필요한 리렌더링 방지
- React.memo, useMemo, useCallback 적절히 활용
- 코드 스플리팅과 지연 로딩 적용

### 4. 접근성
- 시맨틱 HTML 사용
- ARIA 속성 적절히 활용
- 키보드 네비게이션 지원

## 코드 리뷰 체크리스트

- [ ] TypeScript 타입이 명확하게 정의되었는가?
- [ ] 컴포넌트가 단일 책임을 가지고 있는가?
- [ ] 적절한 에러 처리가 구현되었는가?
- [ ] 성능 최적화가 고려되었는가?
- [ ] 접근성 요구사항을 충족하는가?
- [ ] 테스트 코드가 작성되었는가?
- [ ] 코드 스타일 가이드를 준수하는가?

## 문서 관리

본 지침서는 프로젝트와 함께 지속적으로 업데이트됩니다. 
- 새로운 패턴이나 관례가 도입될 때마다 문서화
- 기존 지침의 개선사항은 팀 논의 후 반영
- 모든 변경사항은 버전 이력에 기록

---

최종 수정일: 2025-06-23