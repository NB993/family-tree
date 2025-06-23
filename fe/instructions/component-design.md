# 컴포넌트 설계 원칙

## 컴포넌트 철학

React 컴포넌트는 UI의 독립적이고 재사용 가능한 조각입니다. 각 컴포넌트는 명확한 책임을 가지며, 예측 가능한 방식으로 동작해야 합니다.

## 컴포넌트 분류

### 1. Presentational Components (표현 컴포넌트)
- **목적**: UI 표현에만 집중
- **특징**: 
  - Props를 통해 데이터 수신
  - 상태를 가지지 않거나 UI 상태만 보유
  - 순수 함수형 컴포넌트
  - 테스트하기 쉬움

```typescript
interface ButtonProps {
  label: string;
  onClick: () => void;
  variant?: 'primary' | 'secondary';
  disabled?: boolean;
}

export const Button: React.FC<ButtonProps> = ({ 
  label, 
  onClick, 
  variant = 'primary',
  disabled = false 
}) => {
  return (
    <button
      className={`btn btn-${variant}`}
      onClick={onClick}
      disabled={disabled}
    >
      {label}
    </button>
  );
};
```

### 2. Container Components (컨테이너 컴포넌트)
- **목적**: 비즈니스 로직과 데이터 관리
- **특징**:
  - Custom Hook을 통한 데이터 페칭
  - 여러 Presentational 컴포넌트 조합
  - 상태 관리 담당
  - 사이드 이펙트 처리

```typescript
export const FamilyListContainer: React.FC = () => {
  const { data: families, isLoading, error } = useFamilyList();
  const { mutate: deleteFamily } = useDeleteFamily();

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <FamilyList 
      families={families}
      onDelete={deleteFamily}
    />
  );
};
```

### 3. Page Components (페이지 컴포넌트)
- **목적**: 라우트별 최상위 컴포넌트
- **특징**:
  - 레이아웃 구성
  - 페이지 레벨 상태 관리
  - SEO 메타데이터 설정
  - 권한 검증

```typescript
export const FamilyDetailPage: React.FC = () => {
  const { familyId } = useParams();
  const { data: family, isLoading } = useFamilyDetail(familyId);

  useEffect(() => {
    document.title = family ? `${family.name} - Family Tree` : 'Family Tree';
  }, [family]);

  return (
    <PageLayout>
      <Helmet>
        <meta name="description" content={family?.description} />
      </Helmet>
      {isLoading ? (
        <PageLoader />
      ) : (
        <FamilyDetailContainer familyId={familyId} />
      )}
    </PageLayout>
  );
};
```

## 컴포넌트 작성 규칙

### 1. 명명 규칙
- **파일명**: PascalCase (예: `UserProfile.tsx`)
- **컴포넌트명**: 파일명과 동일
- **Props 인터페이스**: `{ComponentName}Props`
- **스타일 파일**: `{ComponentName}.module.css` (CSS Modules 사용 시)

### 2. 파일 구조
```typescript
// 1. Import 문
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/common';
import { useFamilyData } from '@/hooks/queries';
import type { Family } from '@/types';

// 2. Props 인터페이스
interface FamilyCardProps {
  family: Family;
  onSelect?: (id: string) => void;
  isSelected?: boolean;
}

// 3. 컴포넌트 정의
export const FamilyCard: React.FC<FamilyCardProps> = ({
  family,
  onSelect,
  isSelected = false
}) => {
  // 4. Hooks
  const navigate = useNavigate();
  const [isHovered, setIsHovered] = useState(false);

  // 5. 이벤트 핸들러
  const handleClick = () => {
    onSelect?.(family.id);
    navigate(`/families/${family.id}`);
  };

  // 6. 렌더링
  return (
    <div 
      className={`family-card ${isSelected ? 'selected' : ''}`}
      onClick={handleClick}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <h3>{family.name}</h3>
      <p>{family.description}</p>
    </div>
  );
};
```

### 3. Props 설계 원칙
- **명확한 타입 정의**: TypeScript 인터페이스 사용
- **선택적 Props**: 기본값 제공
- **콜백 네이밍**: `on{Event}` 패턴
- **Boolean Props**: `is`, `has`, `should` 접두사

```typescript
interface ComponentProps {
  // 필수 Props
  id: string;
  title: string;
  
  // 선택적 Props with 기본값
  variant?: 'primary' | 'secondary'; // = 'primary'
  size?: 'small' | 'medium' | 'large'; // = 'medium'
  
  // Boolean Props
  isLoading?: boolean;
  hasError?: boolean;
  shouldAutoFocus?: boolean;
  
  // 콜백 Props
  onClick?: (event: React.MouseEvent) => void;
  onChange?: (value: string) => void;
  onSubmit?: (data: FormData) => Promise<void>;
  
  // Children
  children?: React.ReactNode;
}
```

### 4. 상태 관리 가이드라인
- **로컬 상태**: UI 전용 상태만 useState 사용
- **서버 상태**: React Query 사용
- **전역 상태**: Context API 사용 (최소화)
- **파생 상태**: useMemo 활용

```typescript
const Component: React.FC = () => {
  // UI 상태
  const [isOpen, setIsOpen] = useState(false);
  const [selectedTab, setSelectedTab] = useState(0);
  
  // 서버 상태
  const { data, isLoading } = useQuery({
    queryKey: ['items'],
    queryFn: fetchItems
  });
  
  // 파생 상태
  const filteredItems = useMemo(() => 
    data?.filter(item => item.status === 'active'),
    [data]
  );
  
  return <div>...</div>;
};
```

## 컴포넌트 패턴

### 1. Compound Components (복합 컴포넌트)
```typescript
interface TabsProps {
  children: React.ReactNode;
  defaultTab?: number;
}

interface TabsContextType {
  activeTab: number;
  setActiveTab: (index: number) => void;
}

const TabsContext = React.createContext<TabsContextType | null>(null);

export const Tabs: React.FC<TabsProps> & {
  List: typeof TabsList;
  Tab: typeof Tab;
  Panel: typeof TabPanel;
} = ({ children, defaultTab = 0 }) => {
  const [activeTab, setActiveTab] = useState(defaultTab);
  
  return (
    <TabsContext.Provider value={{ activeTab, setActiveTab }}>
      <div className="tabs">{children}</div>
    </TabsContext.Provider>
  );
};

const TabsList: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <div className="tabs-list">{children}</div>
);

const Tab: React.FC<{ index: number; children: React.ReactNode }> = ({ 
  index, 
  children 
}) => {
  const context = useContext(TabsContext);
  if (!context) throw new Error('Tab must be used within Tabs');
  
  return (
    <button
      className={`tab ${context.activeTab === index ? 'active' : ''}`}
      onClick={() => context.setActiveTab(index)}
    >
      {children}
    </button>
  );
};

const TabPanel: React.FC<{ index: number; children: React.ReactNode }> = ({ 
  index, 
  children 
}) => {
  const context = useContext(TabsContext);
  if (!context) throw new Error('TabPanel must be used within Tabs');
  
  if (context.activeTab !== index) return null;
  
  return <div className="tab-panel">{children}</div>;
};

Tabs.List = TabsList;
Tabs.Tab = Tab;
Tabs.Panel = TabPanel;

// 사용 예시
<Tabs defaultTab={0}>
  <Tabs.List>
    <Tabs.Tab index={0}>Profile</Tabs.Tab>
    <Tabs.Tab index={1}>Settings</Tabs.Tab>
  </Tabs.List>
  <Tabs.Panel index={0}>Profile Content</Tabs.Panel>
  <Tabs.Panel index={1}>Settings Content</Tabs.Panel>
</Tabs>
```

### 2. Render Props Pattern
```typescript
interface DataFetcherProps<T> {
  url: string;
  children: (data: T | null, isLoading: boolean, error: Error | null) => React.ReactNode;
}

function DataFetcher<T>({ url, children }: DataFetcherProps<T>) {
  const [data, setData] = useState<T | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    fetch(url)
      .then(res => res.json())
      .then(setData)
      .catch(setError)
      .finally(() => setIsLoading(false));
  }, [url]);

  return <>{children(data, isLoading, error)}</>;
}

// 사용 예시
<DataFetcher<User[]> url="/api/users">
  {(users, isLoading, error) => {
    if (isLoading) return <Loading />;
    if (error) return <Error message={error.message} />;
    return <UserList users={users!} />;
  }}
</DataFetcher>
```

### 3. Higher-Order Components (HOC)
```typescript
interface WithAuthProps {
  user: User;
}

function withAuth<P extends WithAuthProps>(
  Component: React.ComponentType<P>
): React.FC<Omit<P, keyof WithAuthProps>> {
  return (props: Omit<P, keyof WithAuthProps>) => {
    const { user, isLoading } = useAuth();
    
    if (isLoading) return <LoadingSpinner />;
    if (!user) return <Navigate to="/login" />;
    
    return <Component {...(props as P)} user={user} />;
  };
}

// 사용 예시
const ProfilePage: React.FC<WithAuthProps> = ({ user }) => {
  return <div>Welcome, {user.name}!</div>;
};

export default withAuth(ProfilePage);
```

## 스타일링 가이드라인

### 1. Tailwind CSS 사용
```typescript
// 조건부 클래스 적용
import clsx from 'clsx';

interface ButtonProps {
  variant: 'primary' | 'secondary';
  size: 'sm' | 'md' | 'lg';
  disabled?: boolean;
}

const Button: React.FC<ButtonProps> = ({ variant, size, disabled }) => {
  const className = clsx(
    'inline-flex items-center justify-center rounded-md font-medium transition-colors',
    'focus-visible:outline-none focus-visible:ring-2',
    {
      // Variant styles
      'bg-blue-600 text-white hover:bg-blue-700': variant === 'primary',
      'bg-gray-200 text-gray-900 hover:bg-gray-300': variant === 'secondary',
      
      // Size styles
      'px-3 py-1.5 text-sm': size === 'sm',
      'px-4 py-2 text-base': size === 'md',
      'px-6 py-3 text-lg': size === 'lg',
      
      // State styles
      'opacity-50 cursor-not-allowed': disabled,
    }
  );

  return <button className={className} disabled={disabled}>...</button>;
};
```

### 2. CSS-in-JS 회피
- Tailwind CSS 우선 사용
- 복잡한 스타일링이 필요한 경우 CSS Modules 고려
- 런타임 오버헤드 최소화

## 성능 최적화

### 1. 메모이제이션
```typescript
// React.memo 사용
export const ExpensiveComponent = React.memo<Props>(({ data }) => {
  return <div>{/* Complex rendering */}</div>;
}, (prevProps, nextProps) => {
  // 커스텀 비교 로직
  return prevProps.data.id === nextProps.data.id;
});

// useMemo 사용
const Component: React.FC<{ items: Item[] }> = ({ items }) => {
  const sortedItems = useMemo(
    () => items.sort((a, b) => a.name.localeCompare(b.name)),
    [items]
  );
  
  return <ItemList items={sortedItems} />;
};

// useCallback 사용
const Component: React.FC = () => {
  const [count, setCount] = useState(0);
  
  const handleIncrement = useCallback(() => {
    setCount(prev => prev + 1);
  }, []); // 의존성 배열이 비어있음
  
  return <Button onClick={handleIncrement}>Increment</Button>;
};
```

### 2. 코드 분할
```typescript
// 라우트 기반 분할
const FamilyPage = lazy(() => import('./pages/FamilyPage'));

// 컴포넌트 기반 분할
const HeavyComponent = lazy(() => import('./components/HeavyComponent'));

// 사용
<Suspense fallback={<Loading />}>
  <Routes>
    <Route path="/family" element={<FamilyPage />} />
  </Routes>
</Suspense>
```

## 접근성 고려사항

### 1. 시맨틱 마크업
```typescript
// ❌ Bad
<div onClick={handleClick}>Click me</div>

// ✅ Good
<button onClick={handleClick}>Click me</button>
```

### 2. ARIA 속성
```typescript
<nav aria-label="Main navigation">
  <ul role="list">
    <li><a href="/" aria-current="page">Home</a></li>
    <li><a href="/about">About</a></li>
  </ul>
</nav>

<button
  aria-label="Close dialog"
  aria-pressed={isPressed}
  aria-expanded={isExpanded}
  aria-controls="menu-items"
>
  <CloseIcon aria-hidden="true" />
</button>
```

### 3. 키보드 네비게이션
```typescript
const Modal: React.FC<{ isOpen: boolean; onClose: () => void }> = ({ 
  isOpen, 
  onClose 
}) => {
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    
    if (isOpen) {
      document.addEventListener('keydown', handleEscape);
      return () => document.removeEventListener('keydown', handleEscape);
    }
  }, [isOpen, onClose]);
  
  if (!isOpen) return null;
  
  return (
    <div role="dialog" aria-modal="true">
      {/* Modal content */}
    </div>
  );
};
```

## 테스트 가이드라인

### 1. 컴포넌트 테스트 구조
```typescript
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from './Button';

describe('Button', () => {
  it('renders with label', () => {
    render(<Button label="Click me" onClick={() => {}} />);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });
  
  it('calls onClick when clicked', () => {
    const handleClick = vi.fn();
    render(<Button label="Click me" onClick={handleClick} />);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
  
  it('is disabled when disabled prop is true', () => {
    render(<Button label="Click me" onClick={() => {}} disabled />);
    expect(screen.getByText('Click me')).toBeDisabled();
  });
});
```

---

최종 수정일: 2025-06-23