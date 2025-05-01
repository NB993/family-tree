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

## 코드 스타일 지침

- 함수 컴포넌트와 리액트 훅을 사용합니다
- 타입스크립트 인터페이스를 적극 활용합니다
- 불변성을 유지하는 방식으로 상태를 업데이트합니다
- 반복되는 로직은 커스텀 훅으로 분리합니다
- 컴포넌트 props는 명시적으로 타입을 정의합니다

## 개발 검증 체크리스트

코드 제출 전 다음 항목을 확인하세요:

1. [ ] API 호출은 ApiClient와 도메인 서비스를 통해서만 이루어집니까?
2. [ ] 서버 상태는 React Query 훅을 통해 관리됩니까?
3. [ ] 에러 처리가 useApiError 훅을 통해 구현되었습니까?
4. [ ] 타입 정의가 명확하게 되어 있습니까?
5. [ ] 컴포넌트가 단일 책임 원칙을 따르고 있습니까?
6. [ ] 기존 아키텍처 패턴을 일관되게 따르고 있습니까?

이 지침을 따르면 프로젝트의 일관성을 유지하고 확장성 있는 코드베이스를 구축할 수 있습니다.
