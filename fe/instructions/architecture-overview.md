# 프론트엔드 아키텍처 개요

## 아키텍처 설계 원칙

본 프로젝트는 **계층화된 아키텍처(Layered Architecture)**를 채택하여 관심사를 명확히 분리하고, 각 계층의 책임을 명확히 정의합니다.

### 핵심 설계 원칙
1. **단방향 데이터 흐름**: UI → Service → API → Server
2. **관심사 분리**: 각 계층은 고유한 책임을 가짐
3. **의존성 역전**: 상위 계층은 하위 계층의 추상화에 의존
4. **재사용성**: 컴포넌트와 로직의 최대 재사용

## 계층 구조

```
┌─────────────────────────────────────────────────┐
│                 UI Components                   │
│              (Pages & Components)               │
├─────────────────────────────────────────────────┤
│                 Custom Hooks                    │
│            (React Query & Business)             │
├─────────────────────────────────────────────────┤
│               Service Layer                     │
│            (Domain Services)                    │
├─────────────────────────────────────────────────┤
│               API Client                        │
│           (HTTP Communication)                  │
├─────────────────────────────────────────────────┤
│                  Types                          │
│            (Domain Models & DTOs)               │
└─────────────────────────────────────────────────┘
```

## 계층별 상세 설명

### 1. UI Components 계층

#### 역할
- 사용자 인터페이스 렌더링
- 사용자 상호작용 처리
- 프레젠테이션 로직 구현

#### 구성 요소
- **Pages**: 라우트별 최상위 컴포넌트
- **Components**: 재사용 가능한 UI 요소
- **Design System**: 공통 UI 컴포넌트

#### 책임과 제약
- 비즈니스 로직 포함 금지
- API 직접 호출 금지
- Custom Hook을 통한 데이터 접근
- UI 상태만 관리

### 2. Custom Hooks 계층

#### 역할
- 서버 상태 관리 (React Query)
- 비즈니스 로직 캡슐화
- 컴포넌트와 서비스 계층 연결

#### 구성 요소
- **Query Hooks**: 데이터 조회 (`useQuery`)
- **Mutation Hooks**: 데이터 변경 (`useMutation`)
- **Business Hooks**: 비즈니스 로직 (`useAuth`, `usePermission` 등)

#### 책임과 제약
- 쿼리 키 관리
- 캐시 무효화 전략
- 낙관적 업데이트
- 에러 처리 및 재시도 로직

### 3. Service Layer 계층

#### 역할
- 도메인별 API 엔드포인트 관리
- 요청/응답 데이터 변환
- 비즈니스 규칙 적용

#### 구성 요소
- **Domain Services**: 도메인별 서비스 클래스
- **Service Interfaces**: 서비스 추상화

#### 책임과 제약
- API 엔드포인트 구성
- DTO ↔ Domain Model 변환
- 요청 전 유효성 검증
- 싱글톤 패턴 적용

### 4. API Client 계층

#### 역할
- HTTP 통신 중앙화
- 공통 설정 관리
- 에러 처리 표준화

#### 구성 요소
- **ApiClient**: HTTP 클라이언트 래퍼
- **Interceptors**: 요청/응답 인터셉터
- **Error Handlers**: 에러 처리 로직

#### 책임과 제약
- 토큰 관리 및 갱신
- 요청/응답 로깅
- 타임아웃 처리
- 에러 변환 및 전파

### 5. Types 계층

#### 역할
- 타입 정의 중앙화
- 도메인 모델 정의
- API 계약 정의

#### 구성 요소
- **Domain Models**: 비즈니스 엔티티
- **DTOs**: 데이터 전송 객체
- **Common Types**: 공통 타입 정의

#### 책임과 제약
- 엄격한 타입 정의
- 선택적 속성 최소화
- 유니온 타입 활용
- 타입 가드 함수 제공

## 데이터 흐름

### 조회 흐름
```
Component → useQuery Hook → Service → ApiClient → Server
    ↑                                                 ↓
    └─────────────── Response ←─────────────────────┘
```

### 변경 흐름
```
Component → useMutation Hook → Service → ApiClient → Server
    ↑                                                  ↓
    ├── Optimistic Update                             ↓
    └─────────────── Response ←──────────────────────┘
```

## 폴더 구조

```
src/
├── api/
│   ├── client.ts           # API 클라이언트
│   └── services/           # 도메인 서비스
│       ├── auth.ts
│       ├── family.ts
│       └── user.ts
├── components/             # 재사용 컴포넌트
│   ├── common/
│   ├── family/
│   └── user/
├── design-system/          # 디자인 시스템
│   ├── colors.ts
│   ├── components/
│   └── typography.ts
├── hooks/                  # 커스텀 훅
│   ├── queries/           # React Query 훅
│   └── useAuth.ts         # 비즈니스 훅
├── pages/                  # 페이지 컴포넌트
│   ├── HomePage.tsx
│   ├── FamilyPage.tsx
│   └── LoginPage.tsx
├── types/                  # 타입 정의
│   ├── api.ts             # API 공통 타입
│   ├── auth.ts
│   ├── family.ts
│   └── user.ts
└── utils/                  # 유틸리티 함수
    ├── constants.ts
    ├── format.ts
    └── validation.ts
```

## 의존성 규칙

### 허용되는 의존성
- UI Components → Hooks, Types, Utils
- Hooks → Services, Types, Utils
- Services → API Client, Types, Utils
- API Client → Types, Utils
- Types → (없음)
- Utils → Types

### 금지된 의존성
- 하위 계층 → 상위 계층
- Services → Hooks
- API Client → Services
- 순환 의존성

## 확장성 고려사항

### 새로운 도메인 추가 시
1. Types에 도메인 타입 정의
2. Service 클래스 생성
3. Query/Mutation Hook 생성
4. UI Component 구현

### 새로운 기능 추가 시
1. 기존 아키텍처 패턴 준수
2. 계층별 책임 명확히 구분
3. 재사용 가능한 단위로 분리
4. 테스트 코드 함께 작성

## 성능 최적화 전략

### 렌더링 최적화
- React.memo를 통한 불필요한 리렌더링 방지
- useMemo/useCallback 적절히 활용
- 컴포넌트 분할을 통한 렌더링 범위 최소화

### 번들 최적화
- 코드 스플리팅 (React.lazy)
- Tree Shaking
- Dynamic Import

### 네트워크 최적화
- React Query 캐싱 전략
- 요청 중복 제거
- 프리페칭 활용

---

최종 수정일: 2025-06-23