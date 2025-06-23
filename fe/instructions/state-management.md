# 상태 관리 전략

## 상태 관리 철학

효과적인 상태 관리는 애플리케이션의 예측 가능성과 유지보수성을 결정합니다. 우리는 상태를 명확히 분류하고, 각 유형에 적합한 도구를 사용합니다.

## 상태의 분류

### 1. 서버 상태 (Server State)
- **정의**: 서버에서 가져온 데이터
- **특징**: 
  - 비동기적으로 로드
  - 다른 사용자에 의해 변경 가능
  - 캐싱과 동기화 필요
- **관리 도구**: React Query (TanStack Query)

### 2. 전역 UI 상태 (Global UI State)
- **정의**: 여러 컴포넌트에서 공유되는 UI 상태
- **예시**: 사용자 인증 정보, 테마, 언어 설정
- **관리 도구**: Context API + useReducer

### 3. 로컬 UI 상태 (Local UI State)
- **정의**: 단일 컴포넌트에서만 사용되는 상태
- **예시**: 폼 입력값, 토글 상태, 모달 열림/닫힘
- **관리 도구**: useState, useReducer

### 4. URL 상태 (URL State)
- **정의**: URL에 반영되는 상태
- **예시**: 검색 필터, 페이지네이션, 정렬
- **관리 도구**: React Router, URLSearchParams

## React Query를 통한 서버 상태 관리

### 쿼리 키 관리
```typescript
// 쿼리 키 팩토리 패턴
export const familyKeys = {
  all: ['families'] as const,
  lists: () => [...familyKeys.all, 'list'] as const,
  list: (filters: FamilyFilters) => [...familyKeys.lists(), filters] as const,
  details: () => [...familyKeys.all, 'detail'] as const,
  detail: (id: string) => [...familyKeys.details(), id] as const,
  members: (id: string) => [...familyKeys.detail(id), 'members'] as const,
};

// 사용 예시
const { data } = useQuery({
  queryKey: familyKeys.detail(familyId),
  queryFn: () => FamilyService.getInstance().getById(familyId),
});
```

### 쿼리 옵션 표준화
```typescript
// 공통 쿼리 옵션
const defaultQueryOptions = {
  staleTime: 5 * 60 * 1000, // 5분
  gcTime: 10 * 60 * 1000, // 10분 (구 cacheTime)
  retry: 3,
  retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
};

// 도메인별 쿼리 옵션
export const familyQueryOptions = {
  list: (filters?: FamilyFilters) => ({
    queryKey: familyKeys.list(filters),
    queryFn: () => FamilyService.getInstance().getList(filters),
    ...defaultQueryOptions,
    staleTime: 2 * 60 * 1000, // 리스트는 2분
  }),
  
  detail: (id: string) => ({
    queryKey: familyKeys.detail(id),
    queryFn: () => FamilyService.getInstance().getById(id),
    ...defaultQueryOptions,
    staleTime: 10 * 60 * 1000, // 상세는 10분
  }),
};
```

### Mutation과 캐시 무효화
```typescript
export function useCreateFamily() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateFamilyDto) => 
      FamilyService.getInstance().create(data),
      
    onSuccess: (newFamily) => {
      // 리스트 캐시 무효화
      queryClient.invalidateQueries({ 
        queryKey: familyKeys.lists() 
      });
      
      // 새 항목을 캐시에 추가
      queryClient.setQueryData(
        familyKeys.detail(newFamily.id),
        newFamily
      );
    },
    
    onError: (error) => {
      console.error('Failed to create family:', error);
      // 에러 토스트 표시
    },
  });
}
```

### 낙관적 업데이트
```typescript
export function useUpdateFamily() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateFamilyDto }) =>
      FamilyService.getInstance().update(id, data),
      
    onMutate: async ({ id, data }) => {
      // 진행 중인 리페치 취소
      await queryClient.cancelQueries({ 
        queryKey: familyKeys.detail(id) 
      });
      
      // 이전 값 스냅샷
      const previousFamily = queryClient.getQueryData(
        familyKeys.detail(id)
      );
      
      // 낙관적 업데이트
      queryClient.setQueryData(familyKeys.detail(id), (old) => ({
        ...old,
        ...data,
      }));
      
      // 롤백을 위한 컨텍스트 반환
      return { previousFamily };
    },
    
    onError: (err, { id }, context) => {
      // 에러 시 롤백
      if (context?.previousFamily) {
        queryClient.setQueryData(
          familyKeys.detail(id),
          context.previousFamily
        );
      }
    },
    
    onSettled: ({ id }) => {
      // 성공/실패 관계없이 리페치
      queryClient.invalidateQueries({ 
        queryKey: familyKeys.detail(id) 
      });
    },
  });
}
```

### 무한 스크롤
```typescript
export function useFamilyInfiniteList(filters?: FamilyFilters) {
  return useInfiniteQuery({
    queryKey: familyKeys.list(filters),
    queryFn: ({ pageParam }) => 
      FamilyService.getInstance().getList({
        ...filters,
        cursor: pageParam,
        size: 20,
      }),
    initialPageParam: null,
    getNextPageParam: (lastPage) => 
      lastPage.pagination.hasNext ? lastPage.pagination.nextCursor : undefined,
    select: (data) => ({
      pages: data.pages,
      pageParams: data.pageParams,
      items: data.pages.flatMap(page => page.content),
    }),
  });
}

// 컴포넌트에서 사용
const FamilyList: React.FC = () => {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useFamilyInfiniteList();
  
  const { ref } = useInView({
    onChange: (inView) => {
      if (inView && hasNextPage) {
        fetchNextPage();
      }
    },
  });
  
  return (
    <div>
      {data?.items.map(family => (
        <FamilyCard key={family.id} family={family} />
      ))}
      <div ref={ref}>
        {isFetchingNextPage && <LoadingSpinner />}
      </div>
    </div>
  );
};
```

## Context API를 통한 전역 상태 관리

### Context 설계 원칙
1. **단일 책임**: 하나의 Context는 하나의 관심사만 처리
2. **최소 범위**: 필요한 컴포넌트에만 Provider 적용
3. **성능 고려**: 자주 변경되는 값은 분리

### 인증 Context 예시
```typescript
// types/auth.ts
export interface User {
  id: string;
  email: string;
  name: string;
  role: 'user' | 'admin';
}

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  isLoading: boolean;
}

// contexts/AuthContext.tsx
type AuthAction =
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: User }
  | { type: 'LOGIN_FAILURE' }
  | { type: 'LOGOUT' }
  | { type: 'TOKEN_REFRESH'; payload: User };

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'LOGIN_START':
      return { ...state, isLoading: true };
      
    case 'LOGIN_SUCCESS':
      return {
        isAuthenticated: true,
        user: action.payload,
        isLoading: false,
      };
      
    case 'LOGIN_FAILURE':
      return {
        isAuthenticated: false,
        user: null,
        isLoading: false,
      };
      
    case 'LOGOUT':
      return {
        isAuthenticated: false,
        user: null,
        isLoading: false,
      };
      
    case 'TOKEN_REFRESH':
      return {
        ...state,
        user: action.payload,
      };
      
    default:
      return state;
  }
}

interface AuthContextType {
  state: AuthState;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ 
  children 
}) => {
  const [state, dispatch] = useReducer(authReducer, {
    isAuthenticated: false,
    user: null,
    isLoading: true,
  });
  
  // 초기 인증 상태 확인
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const user = await AuthService.getInstance().getCurrentUser();
        dispatch({ type: 'LOGIN_SUCCESS', payload: user });
      } catch {
        dispatch({ type: 'LOGIN_FAILURE' });
      }
    };
    
    checkAuth();
  }, []);
  
  const login = useCallback(async (credentials: LoginCredentials) => {
    dispatch({ type: 'LOGIN_START' });
    try {
      const user = await AuthService.getInstance().login(credentials);
      dispatch({ type: 'LOGIN_SUCCESS', payload: user });
    } catch (error) {
      dispatch({ type: 'LOGIN_FAILURE' });
      throw error;
    }
  }, []);
  
  const logout = useCallback(async () => {
    await AuthService.getInstance().logout();
    dispatch({ type: 'LOGOUT' });
  }, []);
  
  const refreshToken = useCallback(async () => {
    const user = await AuthService.getInstance().refreshToken();
    dispatch({ type: 'TOKEN_REFRESH', payload: user });
  }, []);
  
  const value = useMemo(
    () => ({ state, login, logout, refreshToken }),
    [state, login, logout, refreshToken]
  );
  
  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
```

### 테마 Context 예시
```typescript
type Theme = 'light' | 'dark' | 'system';

interface ThemeContextType {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  effectiveTheme: 'light' | 'dark';
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: ReactNode }> = ({ 
  children 
}) => {
  const [theme, setTheme] = useState<Theme>(() => {
    const saved = localStorage.getItem('theme');
    return (saved as Theme) || 'system';
  });
  
  const effectiveTheme = useMemo(() => {
    if (theme === 'system') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches
        ? 'dark'
        : 'light';
    }
    return theme;
  }, [theme]);
  
  useEffect(() => {
    localStorage.setItem('theme', theme);
    document.documentElement.setAttribute('data-theme', effectiveTheme);
  }, [theme, effectiveTheme]);
  
  const value = useMemo(
    () => ({ theme, setTheme, effectiveTheme }),
    [theme, effectiveTheme]
  );
  
  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
};
```

## 로컬 상태 관리

### useState vs useReducer 선택 기준
```typescript
// useState: 단순한 상태
const [isOpen, setIsOpen] = useState(false);
const [selectedId, setSelectedId] = useState<string | null>(null);

// useReducer: 복잡한 상태 로직
type FormState = {
  values: FormValues;
  errors: FormErrors;
  touched: Set<string>;
  isSubmitting: boolean;
};

type FormAction =
  | { type: 'SET_FIELD'; field: string; value: any }
  | { type: 'SET_ERROR'; field: string; error: string }
  | { type: 'TOUCH_FIELD'; field: string }
  | { type: 'SUBMIT_START' }
  | { type: 'SUBMIT_SUCCESS' }
  | { type: 'SUBMIT_FAILURE'; errors: FormErrors }
  | { type: 'RESET' };

function formReducer(state: FormState, action: FormAction): FormState {
  switch (action.type) {
    case 'SET_FIELD':
      return {
        ...state,
        values: { ...state.values, [action.field]: action.value },
        errors: { ...state.errors, [action.field]: undefined },
      };
      
    case 'SET_ERROR':
      return {
        ...state,
        errors: { ...state.errors, [action.field]: action.error },
      };
      
    case 'TOUCH_FIELD':
      return {
        ...state,
        touched: new Set(state.touched).add(action.field),
      };
      
    case 'SUBMIT_START':
      return { ...state, isSubmitting: true };
      
    case 'SUBMIT_SUCCESS':
      return {
        ...state,
        isSubmitting: false,
        values: initialValues,
        errors: {},
        touched: new Set(),
      };
      
    case 'SUBMIT_FAILURE':
      return {
        ...state,
        isSubmitting: false,
        errors: action.errors,
      };
      
    case 'RESET':
      return initialState;
      
    default:
      return state;
  }
}
```

### 커스텀 훅을 통한 상태 로직 캡슐화
```typescript
// useForm 커스텀 훅
export function useForm<T extends Record<string, any>>({
  initialValues,
  validate,
  onSubmit,
}: UseFormOptions<T>) {
  const [state, dispatch] = useReducer(formReducer, {
    values: initialValues,
    errors: {},
    touched: new Set(),
    isSubmitting: false,
  });
  
  const setFieldValue = useCallback((field: keyof T, value: any) => {
    dispatch({ type: 'SET_FIELD', field: String(field), value });
  }, []);
  
  const setFieldError = useCallback((field: keyof T, error: string) => {
    dispatch({ type: 'SET_ERROR', field: String(field), error });
  }, []);
  
  const touchField = useCallback((field: keyof T) => {
    dispatch({ type: 'TOUCH_FIELD', field: String(field) });
  }, []);
  
  const handleSubmit = useCallback(async (e?: React.FormEvent) => {
    e?.preventDefault();
    
    dispatch({ type: 'SUBMIT_START' });
    
    const errors = validate?.(state.values);
    if (errors && Object.keys(errors).length > 0) {
      dispatch({ type: 'SUBMIT_FAILURE', errors });
      return;
    }
    
    try {
      await onSubmit(state.values);
      dispatch({ type: 'SUBMIT_SUCCESS' });
    } catch (error) {
      dispatch({ type: 'SUBMIT_FAILURE', errors: { 
        submit: 'Submission failed' 
      }});
    }
  }, [state.values, validate, onSubmit]);
  
  return {
    values: state.values,
    errors: state.errors,
    touched: state.touched,
    isSubmitting: state.isSubmitting,
    setFieldValue,
    setFieldError,
    touchField,
    handleSubmit,
    reset: () => dispatch({ type: 'RESET' }),
  };
}
```

## URL 상태 관리

### URL 상태 동기화
```typescript
// hooks/useUrlState.ts
export function useUrlState<T extends Record<string, any>>(
  defaultValues: T
): [T, (updates: Partial<T>) => void] {
  const [searchParams, setSearchParams] = useSearchParams();
  
  const state = useMemo(() => {
    const params: any = {};
    
    for (const [key, defaultValue] of Object.entries(defaultValues)) {
      const value = searchParams.get(key);
      
      if (value !== null) {
        // 타입 변환
        if (typeof defaultValue === 'number') {
          params[key] = Number(value);
        } else if (typeof defaultValue === 'boolean') {
          params[key] = value === 'true';
        } else if (Array.isArray(defaultValue)) {
          params[key] = searchParams.getAll(key);
        } else {
          params[key] = value;
        }
      } else {
        params[key] = defaultValue;
      }
    }
    
    return params as T;
  }, [searchParams, defaultValues]);
  
  const setState = useCallback((updates: Partial<T>) => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      
      for (const [key, value] of Object.entries(updates)) {
        if (value === defaultValues[key] || value === null) {
          next.delete(key);
        } else if (Array.isArray(value)) {
          next.delete(key);
          value.forEach(v => next.append(key, String(v)));
        } else {
          next.set(key, String(value));
        }
      }
      
      return next;
    });
  }, [setSearchParams, defaultValues]);
  
  return [state, setState];
}

// 사용 예시
const FamilyListPage: React.FC = () => {
  const [filters, setFilters] = useUrlState({
    search: '',
    status: 'all',
    page: 1,
    sort: 'name',
  });
  
  const { data } = useFamilyList(filters);
  
  return (
    <div>
      <SearchInput
        value={filters.search}
        onChange={(search) => setFilters({ search, page: 1 })}
      />
      <StatusFilter
        value={filters.status}
        onChange={(status) => setFilters({ status, page: 1 })}
      />
      {/* ... */}
    </div>
  );
};
```

## 상태 관리 베스트 프랙티스

### 1. 상태 최소화
```typescript
// ❌ Bad: 파생 가능한 상태를 저장
const [items, setItems] = useState([]);
const [filteredItems, setFilteredItems] = useState([]);
const [selectedCount, setSelectedCount] = useState(0);

// ✅ Good: 필수 상태만 저장하고 파생
const [items, setItems] = useState([]);
const [filter, setFilter] = useState('');
const [selectedIds, setSelectedIds] = useState(new Set());

const filteredItems = useMemo(
  () => items.filter(item => item.name.includes(filter)),
  [items, filter]
);

const selectedCount = selectedIds.size;
```

### 2. 상태 콜로케이션
```typescript
// ❌ Bad: 불필요하게 상위에 상태 위치
const App = () => {
  const [modalOpen, setModalOpen] = useState(false);
  
  return (
    <div>
      <Header />
      <Main>
        <UserProfile onEdit={() => setModalOpen(true)} />
      </Main>
      <EditModal open={modalOpen} onClose={() => setModalOpen(false)} />
    </div>
  );
};

// ✅ Good: 사용하는 곳에 가깝게 위치
const UserProfile = () => {
  const [editModalOpen, setEditModalOpen] = useState(false);
  
  return (
    <>
      <button onClick={() => setEditModalOpen(true)}>Edit</button>
      <EditModal 
        open={editModalOpen} 
        onClose={() => setEditModalOpen(false)} 
      />
    </>
  );
};
```

### 3. 상태 정규화
```typescript
// ❌ Bad: 중첩된 구조
const [families, setFamilies] = useState([
  {
    id: '1',
    name: 'Smith Family',
    members: [
      { id: '1', name: 'John' },
      { id: '2', name: 'Jane' },
    ],
  },
]);

// ✅ Good: 정규화된 구조
const [families, setFamilies] = useState({
  byId: {
    '1': { id: '1', name: 'Smith Family', memberIds: ['1', '2'] },
  },
  allIds: ['1'],
});

const [members, setMembers] = useState({
  byId: {
    '1': { id: '1', name: 'John', familyId: '1' },
    '2': { id: '2', name: 'Jane', familyId: '1' },
  },
  allIds: ['1', '2'],
});
```

---

최종 수정일: 2025-06-23