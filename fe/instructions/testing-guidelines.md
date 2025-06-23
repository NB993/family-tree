# 테스트 작성 가이드라인

## 테스트 철학

테스트는 코드의 품질과 안정성을 보장하는 핵심 요소입니다. 우리는 **사용자 중심의 테스트**를 작성하여 실제 사용 시나리오를 검증하고, **유지보수 가능한 테스트 코드**를 통해 장기적인 개발 효율성을 확보합니다.

## 테스트 분류 및 전략

### 1. 테스트 피라미드
```
     E2E Tests
    (소수, 느림, 비용 높음)
         ↑
   Integration Tests  
  (적당함, 보통, 보통 비용)
         ↑
    Unit Tests
  (다수, 빠름, 비용 낮음)
```

### 2. 테스트 유형별 비율
- **Unit Tests**: 70% - 개별 함수, 훅, 유틸리티
- **Integration Tests**: 20% - 컴포넌트 통합, API 통신
- **E2E Tests**: 10% - 핵심 사용자 플로우

## 테스트 도구 및 설정

### 기술 스택
- **테스트 러너**: Vitest
- **컴포넌트 테스팅**: React Testing Library
- **어설션**: @testing-library/jest-dom
- **목킹**: Vitest (built-in)
- **E2E 테스팅**: Playwright

### Vitest 설정
```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    globals: true,
    css: true,
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
```

### 테스트 설정 파일
```typescript
// src/test/setup.ts
import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach, vi } from 'vitest';

// 각 테스트 후 정리
afterEach(() => {
  cleanup();
});

// 전역 모킹
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Intersection Observer 모킹
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}));
```

## 단위 테스트 (Unit Tests)

### 1. 컴포넌트 테스트 기본 패턴
```typescript
// components/Button.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Button } from './Button';

describe('Button', () => {
  it('renders with label', () => {
    render(<Button label="Click me" onClick={() => {}} />);
    
    expect(screen.getByRole('button', { name: 'Click me' })).toBeInTheDocument();
  });

  it('calls onClick when clicked', async () => {
    const user = userEvent.setup();
    const handleClick = vi.fn();
    
    render(<Button label="Click me" onClick={handleClick} />);
    
    await user.click(screen.getByRole('button', { name: 'Click me' }));
    
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('is disabled when disabled prop is true', () => {
    render(<Button label="Click me" onClick={() => {}} disabled />);
    
    expect(screen.getByRole('button', { name: 'Click me' })).toBeDisabled();
  });

  it('applies variant styling correctly', () => {
    render(<Button label="Click me" onClick={() => {}} variant="primary" />);
    
    const button = screen.getByRole('button', { name: 'Click me' });
    expect(button).toHaveClass('btn-primary');
  });
});
```

### 2. 커스텀 훅 테스트
```typescript
// hooks/useCounter.test.ts
import { renderHook, act } from '@testing-library/react';
import { useCounter } from './useCounter';

describe('useCounter', () => {
  it('should initialize with default value', () => {
    const { result } = renderHook(() => useCounter());
    
    expect(result.current.count).toBe(0);
  });

  it('should initialize with provided value', () => {
    const { result } = renderHook(() => useCounter(10));
    
    expect(result.current.count).toBe(10);
  });

  it('should increment count', () => {
    const { result } = renderHook(() => useCounter());
    
    act(() => {
      result.current.increment();
    });
    
    expect(result.current.count).toBe(1);
  });

  it('should decrement count', () => {
    const { result } = renderHook(() => useCounter(5));
    
    act(() => {
      result.current.decrement();
    });
    
    expect(result.current.count).toBe(4);
  });

  it('should reset to initial value', () => {
    const { result } = renderHook(() => useCounter(10));
    
    act(() => {
      result.current.increment();
      result.current.increment();
    });
    
    expect(result.current.count).toBe(12);
    
    act(() => {
      result.current.reset();
    });
    
    expect(result.current.count).toBe(10);
  });
});
```

### 3. 유틸리티 함수 테스트
```typescript
// utils/format.test.ts
import { formatDate, formatCurrency, validateEmail } from './format';

describe('formatDate', () => {
  it('formats date correctly with default format', () => {
    const date = new Date('2023-12-25');
    
    expect(formatDate(date)).toBe('2023-12-25');
  });

  it('formats date with custom format', () => {
    const date = new Date('2023-12-25');
    
    expect(formatDate(date, 'MM/dd/yyyy')).toBe('12/25/2023');
  });

  it('handles invalid date', () => {
    expect(formatDate(null)).toBe('');
    expect(formatDate(undefined)).toBe('');
  });
});

describe('validateEmail', () => {
  it('validates correct email format', () => {
    expect(validateEmail('user@example.com')).toBe(true);
    expect(validateEmail('test.email+label@domain.co.uk')).toBe(true);
  });

  it('rejects invalid email format', () => {
    expect(validateEmail('invalid-email')).toBe(false);
    expect(validateEmail('user@')).toBe(false);
    expect(validateEmail('@domain.com')).toBe(false);
    expect(validateEmail('')).toBe(false);
  });
});
```

## 통합 테스트 (Integration Tests)

### 1. React Query와 함께 테스트
```typescript
// components/FamilyList.test.tsx
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { FamilyList } from './FamilyList';
import { FamilyService } from '@/api/services/family';

// 서비스 모킹
vi.mock('@/api/services/family');
const mockFamilyService = vi.mocked(FamilyService);

// 테스트용 쿼리 클라이언트 생성
const createTestQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      gcTime: 0,
    },
    mutations: {
      retry: false,
    },
  },
});

// 테스트 래퍼 컴포넌트
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const queryClient = createTestQueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
};

describe('FamilyList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('displays loading state initially', () => {
    mockFamilyService.getInstance.mockReturnValue({
      getList: vi.fn().mockImplementation(() => new Promise(() => {})), // pending promise
    } as any);

    render(
      <TestWrapper>
        <FamilyList />
      </TestWrapper>
    );

    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  it('displays families when data is loaded', async () => {
    const mockFamilies = [
      { id: '1', name: 'Smith Family', description: 'A lovely family' },
      { id: '2', name: 'Johnson Family', description: 'Another family' },
    ];

    mockFamilyService.getInstance.mockReturnValue({
      getList: vi.fn().mockResolvedValue({
        items: mockFamilies,
        totalCount: 2,
        pageNumber: 1,
        pageSize: 10,
        hasNext: false,
        hasPrevious: false,
      }),
    } as any);

    render(
      <TestWrapper>
        <FamilyList />
      </TestWrapper>
    );

    await waitFor(() => {
      expect(screen.getByText('Smith Family')).toBeInTheDocument();
      expect(screen.getByText('Johnson Family')).toBeInTheDocument();
    });
  });

  it('displays error message when API fails', async () => {
    mockFamilyService.getInstance.mockReturnValue({
      getList: vi.fn().mockRejectedValue(new Error('API Error')),
    } as any);

    render(
      <TestWrapper>
        <FamilyList />
      </TestWrapper>
    );

    await waitFor(() => {
      expect(screen.getByText(/error/i)).toBeInTheDocument();
    });
  });
});
```

### 2. 폼 컴포넌트 통합 테스트
```typescript
// components/CreateFamilyForm.test.tsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { CreateFamilyForm } from './CreateFamilyForm';
import { FamilyService } from '@/api/services/family';

vi.mock('@/api/services/family');
const mockFamilyService = vi.mocked(FamilyService);

describe('CreateFamilyForm', () => {
  const mockOnSuccess = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('submits form with valid data', async () => {
    const user = userEvent.setup();
    const mockCreate = vi.fn().mockResolvedValue({ id: '1', name: 'Test Family' });
    
    mockFamilyService.getInstance.mockReturnValue({
      create: mockCreate,
    } as any);

    render(<CreateFamilyForm onSuccess={mockOnSuccess} />);

    // 폼 필드 입력
    await user.type(screen.getByLabelText(/family name/i), 'Test Family');
    await user.type(screen.getByLabelText(/description/i), 'A test family');

    // 폼 제출
    await user.click(screen.getByRole('button', { name: /create/i }));

    // API 호출 확인
    await waitFor(() => {
      expect(mockCreate).toHaveBeenCalledWith({
        name: 'Test Family',
        description: 'A test family',
      });
    });

    // 성공 콜백 호출 확인
    expect(mockOnSuccess).toHaveBeenCalledWith({ id: '1', name: 'Test Family' });
  });

  it('shows validation errors for empty fields', async () => {
    const user = userEvent.setup();

    render(<CreateFamilyForm onSuccess={mockOnSuccess} />);

    // 빈 폼 제출
    await user.click(screen.getByRole('button', { name: /create/i }));

    await waitFor(() => {
      expect(screen.getByText(/family name is required/i)).toBeInTheDocument();
    });

    // API 호출되지 않음 확인
    expect(mockFamilyService.getInstance).not.toHaveBeenCalled();
  });

  it('displays error message when API fails', async () => {
    const user = userEvent.setup();
    const mockCreate = vi.fn().mockRejectedValue(new Error('API Error'));
    
    mockFamilyService.getInstance.mockReturnValue({
      create: mockCreate,
    } as any);

    render(<CreateFamilyForm onSuccess={mockOnSuccess} />);

    await user.type(screen.getByLabelText(/family name/i), 'Test Family');
    await user.click(screen.getByRole('button', { name: /create/i }));

    await waitFor(() => {
      expect(screen.getByText(/failed to create family/i)).toBeInTheDocument();
    });
  });
});
```

## 모킹 전략

### 1. API 서비스 모킹
```typescript
// test/mocks/familyService.ts
export const createMockFamilyService = () => ({
  getList: vi.fn(),
  getById: vi.fn(),
  create: vi.fn(),
  update: vi.fn(),
  delete: vi.fn(),
  uploadProfileImage: vi.fn(),
});

// 테스트에서 사용
const mockFamilyService = createMockFamilyService();
mockFamilyService.getList.mockResolvedValue(mockFamilyListResponse);
```

### 2. React Router 모킹
```typescript
// test/mocks/router.ts
export const mockNavigate = vi.fn();
export const mockLocation = { pathname: '/families' };

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => mockLocation,
    useParams: () => ({ familyId: '1' }),
  };
});
```

### 3. Context 모킹
```typescript
// test/mocks/authContext.ts
export const mockAuthContext = {
  state: {
    isAuthenticated: true,
    user: { id: '1', name: 'Test User', email: 'test@example.com' },
    isLoading: false,
  },
  login: vi.fn(),
  logout: vi.fn(),
  refreshToken: vi.fn(),
};

export const AuthContextWrapper: React.FC<{ children: React.ReactNode }> = ({ 
  children 
}) => (
  <AuthContext.Provider value={mockAuthContext}>
    {children}
  </AuthContext.Provider>
);
```

## E2E 테스트 (Playwright)

### 1. Playwright 설정
```typescript
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
  },
});
```

### 2. 페이지 객체 모델
```typescript
// e2e/pages/FamilyPage.ts
import { Page, Locator } from '@playwright/test';

export class FamilyPage {
  readonly page: Page;
  readonly createButton: Locator;
  readonly familyNameInput: Locator;
  readonly descriptionInput: Locator;
  readonly submitButton: Locator;
  readonly familyCards: Locator;

  constructor(page: Page) {
    this.page = page;
    this.createButton = page.getByRole('button', { name: /create family/i });
    this.familyNameInput = page.getByLabel(/family name/i);
    this.descriptionInput = page.getByLabel(/description/i);
    this.submitButton = page.getByRole('button', { name: /submit/i });
    this.familyCards = page.locator('[data-testid="family-card"]');
  }

  async goto() {
    await this.page.goto('/families');
  }

  async createFamily(name: string, description: string) {
    await this.createButton.click();
    await this.familyNameInput.fill(name);
    await this.descriptionInput.fill(description);
    await this.submitButton.click();
  }

  async getFamilyCardByName(name: string) {
    return this.familyCards.filter({ hasText: name });
  }
}
```

### 3. E2E 테스트 작성
```typescript
// e2e/family-management.spec.ts
import { test, expect } from '@playwright/test';
import { FamilyPage } from './pages/FamilyPage';
import { LoginPage } from './pages/LoginPage';

test.describe('Family Management', () => {
  test.beforeEach(async ({ page }) => {
    // 로그인
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login('test@example.com', 'password');
  });

  test('should create a new family', async ({ page }) => {
    const familyPage = new FamilyPage(page);
    await familyPage.goto();

    await familyPage.createFamily('Test Family', 'A test family description');

    // 생성된 가족이 목록에 표시되는지 확인
    const familyCard = await familyPage.getFamilyCardByName('Test Family');
    await expect(familyCard).toBeVisible();
    await expect(familyCard).toContainText('A test family description');
  });

  test('should navigate to family detail page', async ({ page }) => {
    const familyPage = new FamilyPage(page);
    await familyPage.goto();

    // 기존 가족 카드 클릭
    const familyCard = await familyPage.getFamilyCardByName('Smith Family');
    await familyCard.click();

    // 상세 페이지로 이동했는지 확인
    await expect(page).toHaveURL(/\/families\/\d+/);
    await expect(page.getByRole('heading', { name: 'Smith Family' })).toBeVisible();
  });

  test('should handle family deletion', async ({ page }) => {
    const familyPage = new FamilyPage(page);
    await familyPage.goto();

    const familyCard = await familyPage.getFamilyCardByName('Test Family');
    
    // 삭제 버튼 클릭
    await familyCard.getByRole('button', { name: /delete/i }).click();
    
    // 확인 다이얼로그에서 확인
    await page.getByRole('button', { name: /confirm/i }).click();

    // 가족이 목록에서 제거되었는지 확인
    await expect(familyCard).not.toBeVisible();
  });
});
```

## 테스트 베스트 프랙티스

### 1. AAA 패턴 (Arrange-Act-Assert)
```typescript
test('should update family name', async () => {
  // Arrange (준비)
  const family = { id: '1', name: 'Old Name', description: 'Description' };
  const updatedName = 'New Name';
  
  // Act (실행)
  const result = await updateFamilyName(family.id, updatedName);
  
  // Assert (검증)
  expect(result.name).toBe(updatedName);
  expect(result.id).toBe(family.id);
});
```

### 2. 의미 있는 테스트명
```typescript
// ✅ Good: 동작과 예상 결과가 명확
test('should display error message when required field is empty');
test('should redirect to login page when user is not authenticated');
test('should update family list after creating new family');

// ❌ Bad: 구현 세부사항에 집중
test('should call handleSubmit function');
test('should set loading state to true');
```

### 3. 테스트 독립성
```typescript
// ✅ Good: 각 테스트가 독립적
describe('FamilyService', () => {
  beforeEach(() => {
    // 각 테스트 전에 초기화
    vi.clearAllMocks();
  });

  test('should create family with valid data', () => {
    // 이 테스트만을 위한 데이터 준비
    const familyData = { name: 'Test Family' };
    // ...
  });

  test('should handle creation error', () => {
    // 독립적인 에러 시나리오 테스트
    // ...
  });
});
```

### 4. 의도가 명확한 어설션
```typescript
// ✅ Good: 구체적이고 명확한 검증
expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
expect(screen.getByText('Family created successfully')).toBeInTheDocument();

// ❌ Bad: 너무 일반적인 검증
expect(element).toBeTruthy();
expect(result).toBeDefined();
```

### 5. 테스트 데이터 관리
```typescript
// test/fixtures/families.ts
export const mockFamilies = [
  {
    id: '1',
    name: 'Smith Family',
    description: 'A loving family',
    members: 4,
    createdAt: '2023-01-01T00:00:00Z',
  },
  {
    id: '2',
    name: 'Johnson Family',
    description: 'Another family',
    members: 3,
    createdAt: '2023-01-02T00:00:00Z',
  },
];

export const createMockFamily = (overrides = {}) => ({
  id: 'test-id',
  name: 'Test Family',
  description: 'Test description',
  members: 2,
  createdAt: new Date().toISOString(),
  ...overrides,
});
```

## 테스트 커버리지 및 품질

### 1. 커버리지 설정
```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html', 'json'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
        '**/*.config.ts',
      ],
      thresholds: {
        global: {
          branches: 80,
          functions: 80,
          lines: 80,
          statements: 80,
        },
      },
    },
  },
});
```

### 2. 테스트 품질 체크리스트
- [ ] 테스트가 실제 사용자 시나리오를 반영하는가?
- [ ] 테스트명이 의도를 명확히 표현하는가?
- [ ] 각 테스트가 독립적으로 실행 가능한가?
- [ ] 테스트 데이터가 재사용 가능하게 구성되었는가?
- [ ] 비동기 로직이 적절히 처리되었는가?
- [ ] 에러 케이스가 충분히 테스트되었는가?
- [ ] 모킹이 과도하지 않은가?

---

최종 수정일: 2025-06-23