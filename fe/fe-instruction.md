# 프론트엔드 개발 지침서

## 아키텍처 개요

본 프로젝트는 계층화된 아키텍처를 기반으로 관심사 분리 원칙을 엄격히 준수합니다. 코드 추가 및 수정 시 다음 구조를 따라주세요.

## 계층별 구성 및 개발 지침

### 1. API 통신 계층 (ApiClient)
- **파일 위치**: `src/api/client.ts`
- **역할**: HTTP 통신의 중앙화된 처리
- **개발 지침**:
  - `ApiClient` 싱글톤 인스턴스를 통해서만 HTTP 요청을 수행합니다
  - 에러 처리, 헤더 설정 등 공통 로직은 `ApiClient`에 집중시킵니다
  - 새로운 HTTP 메서드가 필요한 경우 `ApiClient` 클래스에 추가합니다
  - API 응답 형식이 변경되면 `ApiClient`의 인터셉터에서 처리합니다

### 2. 도메인 서비스 계층
- **파일 위치**: `src/api/services/{도메인명}.ts`
- **역할**: 도메인별 API 엔드포인트 관리 및 요청 구성
- **개발 지침**:
  - 새로운 도메인은 별도의 서비스 클래스로 분리합니다
  - 모든 서비스 클래스는 싱글톤 패턴을 사용합니다
  - URL은 `BASE_URL` 상수로 관리하고 엔드포인트별 경로는 메서드에서 조합합니다
  - 서비스 메서드는 도메인 타입을 반환하도록 타입 매개변수를 지정합니다
  - 예시:
    ```typescript
    export class NewDomainService {
      private static instance: NewDomainService;
      private apiClient: ApiClient;
      private readonly BASE_URL = "/api/new-domain";

      public static getInstance(): NewDomainService {
        if (!NewDomainService.instance) {
          NewDomainService.instance = new NewDomainService();
        }
        return NewDomainService.instance;
      }

      private constructor() {
        this.apiClient = ApiClient.getInstance();
      }

      // 메서드 구현
    }
    ```

### 3. React Query 훅 계층
- **파일 위치**: `src/hooks/queries/use{도메인명}.ts`
- **역할**: 서버 상태 관리 및 API 요청 추상화
- **개발 지침**:
  - 각 도메인별로 독립된 쿼리 훅 파일을 생성합니다
  - 쿼리 키는 파일 상단에 객체 형태로 정의하고 일관된 구조를 유지합니다
  - CRUD 작업별로 독립된 훅을 생성합니다(useGet, useCreate, useUpdate, useDelete)
  - 성공 시 관련 쿼리를 자동으로 무효화하는 로직을 포함합니다
  - 예시:
    ```typescript
    export const newDomainKeys = {
      all: ["newDomain"] as const,
      lists: () => [...newDomainKeys.all, "list"] as const,
      list: (id: string) => [...newDomainKeys.lists(), id] as const,
      details: () => [...newDomainKeys.all, "detail"] as const,
      detail: (id: string) => [...newDomainKeys.details(), id] as const,
    };

    export function useNewDomainList() {
      return useQuery({
        queryKey: newDomainKeys.lists(),
        queryFn: () => NewDomainService.getInstance().getList(),
      });
    }
    
    export function useCreateNewDomain() {
      const queryClient = useQueryClient();
      
      return useMutation({
        mutationFn: (data: CreateNewDomainDto) => 
          NewDomainService.getInstance().create(data),
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: newDomainKeys.lists() });
        },
      });
    }
    ```

### 4. UI 컴포넌트 계층
- **파일 위치**: `src/pages`, `src/components`
- **역할**: 사용자 인터페이스 렌더링 및 상호작용 처리
- **개발 지침**:
  - 컴포넌트는 가능한 작게 유지하고 단일 책임을 가지도록 설계합니다
  - 페이지 컴포넌트는 React Query 훅을 호출하고 데이터를 하위 컴포넌트에 전달합니다
  - 재사용 가능한 UI 요소는 `src/components`에 별도로 분리합니다
  - 상태 관리는 React Query를 우선 사용하고, UI 상태만 `useState`/`useReducer`로 관리합니다
  - API 에러 처리는 `useApiError` 훅을 사용합니다
  - 예시:
    ```tsx
    function NewDomainPage() {
      // 커스텀 에러 핸들러 정의
      const customHandlers: ErrorHandlers = {
        ND001: (error) => console.error('도메인 오류:', error.message),
      };
      const { handleError } = useApiError(customHandlers);
      
      // API 클라이언트에 에러 핸들러 설정
      useEffect(() => {
        const apiClient = ApiClient.getInstance();
        apiClient.setErrorHandler(handleError);
      }, [handleError]);
      
      // 데이터 페칭
      const { data, isLoading, error } = useNewDomainList();
      
      // UI 로직 및 렌더링
      // ...
    }
    ```

### 5. 타입 정의 계층
- **파일 위치**: `src/types/{도메인명}.ts`
- **역할**: 도메인 모델 및 DTO 타입 정의
- **개발 지침**:
  - 타입은 도메인별로 분리하여 파일 관리합니다
  - API 응답 형식과 일치하도록 인터페이스를 정의합니다
  - 공유 타입(페이지네이션 등)은 `src/types/api.ts`에 정의합니다
  - DTO(Data Transfer Object) 타입은 접미사로 `Dto`를 사용합니다
  - 예시:
    ```typescript
    export interface NewDomainItem {
      id: string;
      name: string;
      description: string;
      createdAt: string;
    }

    export interface CreateNewDomainDto {
      name: string;
      description: string;
    }

    export interface UpdateNewDomainDto {
      id: string;
      name?: string;
      description?: string;
    }
    ```

## 에러 처리 지침

- 모든 API 에러는 `useApiError` 훅을 통해 처리합니다
- 화면별 커스텀 에러 핸들러는 에러 코드별로 정의합니다
- 공통 에러(인증, 권한 등)는 기본 핸들러에서 처리되므로 중복 구현하지 않습니다
- 에러 메시지는 사용자 친화적으로 표시합니다

## React Native 특화 개발 지침

### 플랫폼별 코드 분리
- 플랫폼 특화 코드는 `.ios.tsx`와 `.android.tsx` 확장자 사용
- 공통 인터페이스는 별도 파일로 분리
- 플랫폼 분기가 필요한 로직:
```typescript
import { Platform } from 'react-native';

// 권장 방식
const platformSpecificValue = Platform.select({
  ios: 'iOS 값',
  android: 'Android 값',
  default: '기본 값'
});

// 지양할 방식
if (Platform.OS === 'ios') {
  // iOS 전용 코드
} else {
  // Android 전용 코드
}
```

### 네이티브 모듈 사용
- 네이티브 모듈 래퍼는 `src/native` 디렉토리에 위치
- Promise 기반 인터페이스로 통일
- 비동기 네이티브 모듈 호출은 항상 try-catch로 감싸기

### 성능 최적화 전략
- 모든 목록 렌더링에 `FlatList` 또는 `SectionList` 사용 (절대 `map`으로 컴포넌트 배열 생성 금지)
- 메모이제이션 훅 적용 기준:
  - `useMemo`: 계산 비용이 큰 값 또는 객체 생성 시
  - `useCallback`: 자식 컴포넌트에 props로 전달되는 함수
  - `React.memo`: 렌더링 비용이 크고 빈번한 리렌더링이 발생하는 컴포넌트
- 이미지 최적화:
  - 모든 로컬 이미지는 `@2x`, `@3x` 해상도로 제공
  - 원격 이미지는 `FastImage` 라이브러리 사용
  - 이미지 크기는 사용되는 영역에 맞게 최적화
- 화면 전환 최적화:
  - 무거운 화면은 `React.lazy`와 `Suspense`로 지연 로딩
  - 화면 전환 시 소요되는 작업은 `InteractionManager.runAfterInteractions` 사용

### 네비게이션 구조
- React Navigation 라이브러리 사용
- 네비게이션 구조는 `src/navigation` 디렉토리에 정의
- 스택 구조:
  - AuthStack: 인증 관련 화면 (로그인, 회원가입 등)
  - MainStack: 인증 후 메인 화면
  - ModalStack: 모달 형태로 표시되는 화면
- 탭 네비게이션에는 아이콘과 레이블 모두 제공 (접근성 고려)
- 네비게이션 파라미터 타입 정의:
```typescript
export type RootStackParamList = {
  Home: undefined;
  Profile: { userId: string };
  Settings: undefined;
  // ... 기타 화면
};

// 타입 안전한 네비게이션
const navigation = useNavigation<NavigationProp<RootStackParamList>>();
navigation.navigate('Profile', { userId: '123' });
```

## 상태 관리 세부 전략

### 상태 분류 및 관리 방식
- **서버 상태**: React Query로 관리 (API 응답 데이터)
- **글로벌 UI 상태**: Context API + useReducer로 관리 (테마, 인증 등)
- **로컬 UI 상태**: useState/useReducer로 관리 (폼 입력, 토글 등)

### Context API 사용 기준
- 여러 컴포넌트에서 공유되는 상태만 Context로 관리
- Context는 기능 단위로 분리 (AuthContext, ThemeContext 등)
- Context Provider는 최대한 낮은 레벨에 위치시켜 불필요한 리렌더링 방지
- Context 예시:

```typescript
// src/contexts/AuthContext.tsx
import React, { createContext, useContext, useReducer, ReactNode } from 'react';

// 상태 타입 정의
interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  loading: boolean;
}

// 액션 타입 정의
type AuthAction =
  | { type: 'LOGIN_SUCCESS'; payload: User }
  | { type: 'LOGOUT' }
  | { type: 'SET_LOADING'; payload: boolean };

// 컨텍스트 타입 정의
interface AuthContextType {
  state: AuthState;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => Promise<void>;
}

// 컨텍스트 생성
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// 리듀서 함수
function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        isAuthenticated: true,
        user: action.payload,
        loading: false,
      };
    case 'LOGOUT':
      return {
        ...state,
        isAuthenticated: false,
        user: null,
      };
    case 'SET_LOADING':
      return {
        ...state,
        loading: action.payload,
      };
    default:
      return state;
  }
}

// 프로바이더 컴포넌트
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, {
    isAuthenticated: false,
    user: null,
    loading: false,
  });

  const login = async (credentials: LoginCredentials) => {
    dispatch({ type: 'SET_LOADING', payload: true });
    try {
      const user = await AuthService.getInstance().login(credentials);
      dispatch({ type: 'LOGIN_SUCCESS', payload: user });
    } catch (error) {
      // 에러 처리
    }
  };

  const logout = async () => {
    await AuthService.getInstance().logout();
    dispatch({ type: 'LOGOUT' });
  };

  return (
    <AuthContext.Provider value={{ state, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// 훅
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
```

## React Native 테스트 전략

### 테스트 계층
1. **컴포넌트 단위 테스트**: 개별 UI 컴포넌트 테스트
  - 라이브러리: React Native Testing Library
  - 대상: 재사용 가능한 UI 컴포넌트
  - 파일 위치: `__tests__/components`
  - 포커스: 렌더링 정확성, 사용자 상호작용, 접근성

2. **훅 단위 테스트**: 커스텀 훅 테스트
  - 라이브러리: `@testing-library/react-hooks`
  - 대상: 모든 커스텀 훅
  - 파일 위치: `__tests__/hooks`
  - 포커스: 상태 변화, 사이드 이펙트, 에러 처리

3. **통합 테스트**: 화면 또는 기능 흐름 테스트
  - 대상: 주요 사용자 시나리오
  - 파일 위치: `__tests__/integration`
  - 포커스: 컴포넌트 간 상호작용, 상태 전파, 네비게이션

4. **E2E 테스트**: 전체 앱 흐름 테스트
  - 라이브러리: Detox
  - 대상: 핵심 사용자 경로
  - 파일 위치: `e2e`
  - 포커스: 실제 기기에서의 앱 동작 검증

### 목킹 전략
- **API 호출**: MSW(Mock Service Worker) 사용
- **네이티브 모듈**: Jest mock 사용
- **네비게이션**: React Navigation 테스트 헬퍼 활용
- **상태 관리**: Provider 래퍼 활용

### 테스트 예시
```typescript
// 컴포넌트 테스트 예시
import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import { Button } from '../src/components/Button';

describe('Button 컴포넌트', () => {
  test('given_default_props_when_rendered_then_shows_correct_text', () => {
    const { getByText } = render(<Button label="테스트" onPress={() => {}} />);
    
    expect(getByText('테스트')).toBeTruthy();
  });
  
  test('given_onPress_handler_when_pressed_then_calls_handler', () => {
    const onPressMock = jest.fn();
    const { getByText } = render(<Button label="테스트" onPress={onPressMock} />);
    
    fireEvent.press(getByText('테스트'));
    
    expect(onPressMock).toHaveBeenCalledTimes(1);
  });
  
  test('given_disabled_prop_when_rendered_then_has_disabled_style', () => {
    const { getByTestId } = render(
      <Button label="테스트" onPress={() => {}} disabled testID="button" />
    );
    
    const button = getByTestId('button');
    
    expect(button.props.style).toMatchObject({ opacity: 0.5 });
  });
});
```

## 코드 스타일 지침

- 함수 컴포넌트와 리액트 훅을 사용합니다
- 타입스크립트 인터페이스를 적극 활용합니다
- 불변성을 유지하는 방식으로 상태를 업데이트합니다
- 반복되는 로직은 커스텀 훅으로 분리합니다
- 컴포넌트 props는 명시적으로 타입을 정의합니다
- 접근성을 고려한 UI 컴포넌트 개발 (적절한 `accessibilityLabel` 제공)
- 일관된 코드 포맷팅을 위해 ESLint와 Prettier 설정 준수

## 개발 검증 체크리스트

코드 제출 전 다음 항목을 확인하세요:

1. [ ] API 호출은 ApiClient와 도메인 서비스를 통해서만 이루어집니까?
2. [ ] 서버 상태는 React Query 훅을 통해 관리됩니까?
3. [ ] 에러 처리가 useApiError 훅을 통해 구현되었습니까?
4. [ ] 타입 정의가 명확하게 되어 있습니까?
5. [ ] 컴포넌트가 단일 책임 원칙을 따르고 있습니까?
6. [ ] 기존 아키텍처 패턴을 일관되게 따르고 있습니까?
7. [ ] 플랫폼별 코드가 적절히 분리되어 있습니까?
8. [ ] UI 성능이 최적화되어 있습니까? (불필요한 리렌더링 방지)
9. [ ] 접근성 요소가 적절히 구현되어 있습니까?
10. [ ] 테스트 코드가 작성되어 있습니까?

이 지침을 따르면 프로젝트의 일관성을 유지하고 확장성 있는 코드베이스를 구축할 수 있습니다.
