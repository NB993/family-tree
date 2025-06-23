# API 통합 가이드

## API 통합 아키텍처

### 계층 구조
```
React Components
       ↓
Custom Hooks (React Query)
       ↓  
Domain Services
       ↓
API Client
       ↓
HTTP Library (fetch/axios)
```

## API Client 구현

### 1. 기본 API Client
```typescript
// api/client.ts
export interface ApiError {
  code: string;
  message: string;
  traceId: string;
  validations?: FieldError[];
  status: number;
}

export interface FieldError {
  field: string;
  value: any;
  message: string;
}

export type ErrorHandler = (error: ApiError) => void;

export class ApiClient {
  private static instance: ApiClient;
  private baseURL: string;
  private defaultHeaders: Record<string, string>;
  private errorHandler?: ErrorHandler;

  private constructor() {
    this.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    this.defaultHeaders = {
      'Content-Type': 'application/json',
    };
  }

  public static getInstance(): ApiClient {
    if (!ApiClient.instance) {
      ApiClient.instance = new ApiClient();
    }
    return ApiClient.instance;
  }

  public setErrorHandler(handler: ErrorHandler): void {
    this.errorHandler = handler;
  }

  // HttpOnly 쿠키 기반 인증으로 변경됨
  // 토큰 관리는 서버에서 자동 처리
  public setAuthToken(token: string): void {
    // 마이그레이션 기간 동안 백업용
    this.defaultHeaders['Authorization'] = `Bearer ${token}`;
  }

  public removeAuthToken(): void {
    delete this.defaultHeaders['Authorization'];
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseURL}${endpoint}`;
    
    const config: RequestInit = {
      ...options,
      headers: {
        ...this.defaultHeaders,
        ...options.headers,
      },
      credentials: 'include', // HttpOnly 쿠키 포함
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        await this.handleHttpError(response);
      }

      // ResponseEntity<T>의 body를 직접 반환
      const data = await response.json();
      return data;
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Network error occurred');
    }
  }

  private async handleHttpError(response: Response): Promise<never> {
    let errorData: any;
    
    try {
      errorData = await response.json();
    } catch {
      errorData = { 
        error: {
          code: `HTTP_${response.status}`,
          message: response.statusText,
          traceId: 'unknown'
        }
      };
    }

    const apiError: ApiError = {
      code: errorData.error?.code || `HTTP_${response.status}`,
      message: errorData.error?.message || response.statusText,
      traceId: errorData.error?.traceId || 'unknown',
      validations: errorData.error?.validations,
      status: response.status,
    };

    if (this.errorHandler) {
      this.errorHandler(apiError);
    }

    throw new Error(apiError.message);
  }

  // HTTP 메서드들
  public async get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
    const searchParams = new URLSearchParams();
    
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined) {
          searchParams.append(key, String(value));
        }
      });
    }

    const url = searchParams.toString() 
      ? `${endpoint}?${searchParams.toString()}`
      : endpoint;

    return this.request<T>(url, { method: 'GET' });
  }

  public async post<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  public async put<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  public async patch<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PATCH',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  public async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }

  // FormData 업로드 (기존 upload 메서드 제거)
  // 이제 post 메서드로 FormData 처리
}
```

### 2. 도메인 서비스 구현

#### 기본 서비스 클래스
```typescript
// api/services/base.ts
export abstract class BaseService {
  protected apiClient: ApiClient;
  protected abstract readonly baseUrl: string;

  constructor() {
    this.apiClient = ApiClient.getInstance();
  }

  protected buildUrl(path: string): string {
    return `${this.baseUrl}${path}`;
  }
}
```

#### Family 서비스 예시
```typescript
// api/services/family.ts
import { BaseService } from './base';
import type { Family, CreateFamilyDto, UpdateFamilyDto } from '@/types/family';

export interface FamilyListParams {
  search?: string;
  status?: string;
  page?: number;
  size?: number;
  sort?: string;
}

// 백엔드의 CursorPageResponse 구조와 일치
export interface PaginatedResponse<T> {
  content: T[];           // 비즈니스 데이터
  pagination: {
    nextCursor: string;
    hasNext: boolean;
    size: number;
  };
}

export class FamilyService extends BaseService {
  private static instance: FamilyService;
  protected readonly baseUrl = '/api/families';

  public static getInstance(): FamilyService {
    if (!FamilyService.instance) {
      FamilyService.instance = new FamilyService();
    }
    return FamilyService.instance;
  }

  private constructor() {
    super();
  }

  async getList(params: FamilyListParams = {}): Promise<PaginatedResponse<Family>> {
    return this.apiClient.get<PaginatedResponse<Family>>(this.baseUrl, params);
  }

  async getById(id: string): Promise<Family> {
    return this.apiClient.get<Family>(this.buildUrl(`/${id}`));
  }

  async create(data: CreateFamilyDto): Promise<Family> {
    return this.apiClient.post<Family>(this.baseUrl, data);
  }

  async update(id: string, data: UpdateFamilyDto): Promise<Family> {
    return this.apiClient.put<Family>(this.buildUrl(`/${id}`), data);
  }

  async delete(id: string): Promise<void> {
    return this.apiClient.delete<void>(this.buildUrl(`/${id}`));
  }

  async uploadProfileImage(id: string, file: File): Promise<{ profileUrl: string }> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.apiClient.post<{ profileUrl: string }>(
      this.buildUrl(`/${id}/profile-image`),
      formData
    );
  }

  // 검색 자동완성
  async searchSuggestions(query: string): Promise<string[]> {
    return this.apiClient.get<string[]>(this.buildUrl('/search/suggestions'), {
      q: query,
      limit: 10,
    });
  }

  // 통계 데이터
  async getStatistics(id: string): Promise<FamilyStatistics> {
    return this.apiClient.get<FamilyStatistics>(this.buildUrl(`/${id}/statistics`));
  }
}
```

## React Query 통합

### 1. 쿼리 키 관리
```typescript
// hooks/queries/keys.ts
export const createQueryKeys = <T extends string>(domain: T) => ({
  all: [domain] as const,
  lists: () => [...createQueryKeys(domain).all, 'list'] as const,
  list: (params: Record<string, any>) => [...createQueryKeys(domain).lists(), params] as const,
  details: () => [...createQueryKeys(domain).all, 'detail'] as const,
  detail: (id: string) => [...createQueryKeys(domain).details(), id] as const,
  infinite: (params: Record<string, any>) => [...createQueryKeys(domain).all, 'infinite', params] as const,
});

// 도메인별 키 팩토리
export const familyKeys = createQueryKeys('families');
export const userKeys = createQueryKeys('users');
export const memberKeys = createQueryKeys('members');
```

### 2. 쿼리 훅 구현
```typescript
// hooks/queries/useFamilyQueries.ts
import { useQuery, useMutation, useQueryClient, useInfiniteQuery } from '@tanstack/react-query';
import { FamilyService } from '@/api/services/family';
import { familyKeys } from './keys';
import type { Family, CreateFamilyDto, UpdateFamilyDto, FamilyListParams } from '@/types';

// 리스트 조회
export function useFamilyList(params: FamilyListParams = {}) {
  return useQuery({
    queryKey: familyKeys.list(params),
    queryFn: () => FamilyService.getInstance().getList(params),
    staleTime: 2 * 60 * 1000, // 2분
    gcTime: 5 * 60 * 1000, // 5분
  });
}

// 상세 조회
export function useFamilyDetail(id: string) {
  return useQuery({
    queryKey: familyKeys.detail(id),
    queryFn: () => FamilyService.getInstance().getById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5분
    gcTime: 10 * 60 * 1000, // 10분
  });
}

// 무한 스크롤
export function useFamilyInfiniteList(params: Omit<FamilyListParams, 'page'> = {}) {
  return useInfiniteQuery({
    queryKey: familyKeys.infinite(params),
    queryFn: ({ pageParam = 1 }) => 
      FamilyService.getInstance().getList({ ...params, page: pageParam }),
    initialPageParam: 1,
    getNextPageParam: (lastPage) => 
      lastPage.hasNext ? lastPage.pageNumber + 1 : undefined,
    select: (data) => ({
      pages: data.pages,
      pageParams: data.pageParams,
      items: data.pages.flatMap(page => page.items),
      totalCount: data.pages[0]?.totalCount ?? 0,
    }),
  });
}

// 생성
export function useCreateFamily() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFamilyDto) => 
      FamilyService.getInstance().create(data),
    onSuccess: (newFamily) => {
      // 리스트 캐시 무효화
      queryClient.invalidateQueries({ queryKey: familyKeys.lists() });
      
      // 새 항목을 캐시에 추가
      queryClient.setQueryData(familyKeys.detail(newFamily.id), newFamily);
    },
  });
}

// 수정
export function useUpdateFamily() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateFamilyDto }) =>
      FamilyService.getInstance().update(id, data),
    onMutate: async ({ id, data }) => {
      // 낙관적 업데이트
      await queryClient.cancelQueries({ queryKey: familyKeys.detail(id) });
      
      const previousFamily = queryClient.getQueryData(familyKeys.detail(id));
      
      queryClient.setQueryData(familyKeys.detail(id), (old: Family) => ({
        ...old,
        ...data,
      }));
      
      return { previousFamily };
    },
    onError: (err, { id }, context) => {
      if (context?.previousFamily) {
        queryClient.setQueryData(familyKeys.detail(id), context.previousFamily);
      }
    },
    onSettled: (_, __, { id }) => {
      queryClient.invalidateQueries({ queryKey: familyKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: familyKeys.lists() });
    },
  });
}

// 삭제
export function useDeleteFamily() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => FamilyService.getInstance().delete(id),
    onSuccess: (_, deletedId) => {
      // 캐시에서 제거
      queryClient.removeQueries({ queryKey: familyKeys.detail(deletedId) });
      
      // 리스트에서 제거
      queryClient.setQueriesData(
        { queryKey: familyKeys.lists() },
        (oldData: any) => {
          if (!oldData) return oldData;
          
          return {
            ...oldData,
            items: oldData.items.filter((family: Family) => family.id !== deletedId),
            totalCount: oldData.totalCount - 1,
          };
        }
      );
    },
  });
}

// 프로필 이미지 업로드
export function useFamilyProfileImageUpload() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, file }: { id: string; file: File }) =>
      FamilyService.getInstance().uploadProfileImage(id, file),
    onSuccess: (result, { id }) => {
      // 해당 family의 profileUrl 업데이트
      queryClient.setQueryData(
        familyKeys.detail(id),
        (old: Family) => old ? { ...old, profileUrl: result.profileUrl } : old
      );
      
      // 리스트도 업데이트
      queryClient.invalidateQueries({ queryKey: familyKeys.lists() });
    },
  });
}
```

### 3. 에러 처리 훅
```typescript
// hooks/useApiError.ts
export interface ErrorHandlers {
  [errorCode: string]: (error: ApiError) => void;
}

const defaultErrorHandlers: ErrorHandlers = {
  'AUTH_001': () => {
    // 인증 토큰 만료
    AuthService.getInstance().logout();
    window.location.href = '/login';
  },
  'AUTH_002': () => {
    // 권한 없음
    alert('접근 권한이 없습니다.');
  },
  'NETWORK_ERROR': () => {
    alert('네트워크 연결을 확인해주세요.');
  },
  'SERVER_ERROR': () => {
    alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
  },
};

export function useApiError(customHandlers: ErrorHandlers = {}) {
  const handleError = useCallback((error: ApiError) => {
    const handler = customHandlers[error.code] || defaultErrorHandlers[error.code];
    
    if (handler) {
      handler(error);
    } else {
      // 기본 에러 처리
      console.error('Unhandled API error:', error);
      alert(error.message || '알 수 없는 오류가 발생했습니다.');
    }
  }, [customHandlers]);

  return { handleError };
}
```

## 인증 및 토큰 관리

### 1. 인증 서비스
```typescript
// api/services/auth.ts
export interface LoginCredentials {
  email: string;
  password: string;
}

export interface LoginResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
}

export class AuthService extends BaseService {
  private static instance: AuthService;
  protected readonly baseUrl = '/api/auth';

  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  async login(credentials: LoginCredentials): Promise<LoginResponse> {
    const response = await this.apiClient.post<LoginResponse>(
      this.buildUrl('/login'),
      credentials
    );

    // 토큰 저장 및 헤더 설정
    this.storeTokens(response.accessToken, response.refreshToken);
    this.apiClient.setAuthToken(response.accessToken);

    return response;
  }

  async logout(): Promise<void> {
    try {
      await this.apiClient.post<void>(this.buildUrl('/logout'));
    } finally {
      // 에러가 발생해도 로컬 정리는 수행
      this.clearTokens();
      this.apiClient.removeAuthToken();
    }
  }

  async refreshToken(): Promise<LoginResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await this.apiClient.post<LoginResponse>(
      this.buildUrl('/refresh'),
      { refreshToken }
    );

    this.storeTokens(response.accessToken, response.refreshToken);
    this.apiClient.setAuthToken(response.accessToken);

    return response;
  }

  async getCurrentUser(): Promise<User> {
    return this.apiClient.get<User>(this.buildUrl('/me'));
  }

  private storeTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  private getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  private clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  // 앱 시작 시 토큰 복원
  public initializeAuth(): void {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      this.apiClient.setAuthToken(accessToken);
    }
  }
}
```

### 2. 자동 토큰 갱신
```typescript
// utils/tokenRefresh.ts
export class TokenRefreshManager {
  private static instance: TokenRefreshManager;
  private refreshPromise: Promise<any> | null = null;

  public static getInstance(): TokenRefreshManager {
    if (!TokenRefreshManager.instance) {
      TokenRefreshManager.instance = new TokenRefreshManager();
    }
    return TokenRefreshManager.instance;
  }

  async refreshTokenIfNeeded(): Promise<void> {
    // 이미 갱신 중인 경우 동일한 Promise 반환
    if (this.refreshPromise) {
      return this.refreshPromise;
    }

    const token = localStorage.getItem('accessToken');
    if (!token || !this.isTokenExpiringSoon(token)) {
      return;
    }

    this.refreshPromise = this.performRefresh();
    
    try {
      await this.refreshPromise;
    } finally {
      this.refreshPromise = null;
    }
  }

  private async performRefresh(): Promise<void> {
    try {
      await AuthService.getInstance().refreshToken();
    } catch (error) {
      // 갱신 실패 시 로그아웃
      await AuthService.getInstance().logout();
      window.location.href = '/login';
    }
  }

  private isTokenExpiringSoon(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // seconds to milliseconds
      const now = Date.now();
      const fiveMinutes = 5 * 60 * 1000;
      
      return exp - now < fiveMinutes;
    } catch {
      return true; // 파싱 실패 시 갱신 필요로 간주
    }
  }
}

// HTTP 인터셉터에서 사용
export const setupTokenRefreshInterceptor = () => {
  const originalFetch = window.fetch;
  
  window.fetch = async (input, init) => {
    await TokenRefreshManager.getInstance().refreshTokenIfNeeded();
    return originalFetch(input, init);
  };
};
```

## 캐싱 전략

### 1. 쿼리별 캐시 설정
```typescript
// config/queryConfig.ts
export const queryConfig = {
  // 자주 변경되지 않는 데이터
  static: {
    staleTime: 30 * 60 * 1000, // 30분
    gcTime: 60 * 60 * 1000, // 1시간
  },
  
  // 일반적인 데이터
  normal: {
    staleTime: 5 * 60 * 1000, // 5분
    gcTime: 10 * 60 * 1000, // 10분
  },
  
  // 자주 변경되는 데이터
  dynamic: {
    staleTime: 1 * 60 * 1000, // 1분
    gcTime: 2 * 60 * 1000, // 2분
  },
  
  // 실시간에 가까운 데이터
  realtime: {
    staleTime: 0, // 항상 stale
    gcTime: 30 * 1000, // 30초
  },
};
```

### 2. 백그라운드 데이터 동기화
```typescript
// hooks/useBackgroundSync.ts
export function useBackgroundSync() {
  const queryClient = useQueryClient();

  useEffect(() => {
    const handleFocus = () => {
      // 포커스 시 중요한 데이터 갱신
      queryClient.invalidateQueries({ 
        queryKey: ['notifications'],
        refetchType: 'active' 
      });
    };

    const handleOnline = () => {
      // 온라인 복구 시 모든 데이터 갱신
      queryClient.invalidateQueries({ refetchType: 'active' });
    };

    window.addEventListener('focus', handleFocus);
    window.addEventListener('online', handleOnline);

    return () => {
      window.removeEventListener('focus', handleFocus);
      window.removeEventListener('online', handleOnline);
    };
  }, [queryClient]);
}
```

---

최종 수정일: 2025-06-23