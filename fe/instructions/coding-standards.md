# 코드 작성 스타일 가이드라인

## 기본 원칙

- TypeScript 엄격 모드 사용
- 함수형 컴포넌트와 React Hooks 사용
- 불변성을 유지하는 방식으로 데이터 조작
- any 타입 사용 금지
- 명시적 타입 정의 우선
- **타입 정의는 `src/types` 디렉토리에서 중앙 관리**

## TypeScript 사용 지침

### 1. 타입 정의 규칙

#### 타입 중앙 관리
```typescript
// ✅ 모든 타입 정의는 src/types 디렉토리에서 관리
// src/types/family.ts
export interface FamilyMember {
  id: number;
  name: string;
  role: FamilyMemberRole;
}

export enum FamilyMemberRole {
  OWNER = 'OWNER',
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER'
}

// src/api/services/familyService.ts
import { FamilyMember, FamilyMemberRole } from '../../types/family';

// ❌ 서비스 파일에서 타입 중복 정의 금지
// export interface FamilyMember { ... }  // 중복 정의 금지
```

#### 타입 구조화 원칙
```typescript
// src/types/index.ts - 메인 export 파일
export * from './family';
export * from './auth';
export * from './api';
export * from './error';

// 도메인별 타입 파일 분리
// - family.ts: 가족 관련 타입
// - auth.ts: 인증 관련 타입  
// - api.ts: API 공통 타입 (페이징 등)
// - error.ts: 에러 관련 타입
```

#### 인터페이스 vs 타입 별칭
```typescript
// ✅ 인터페이스: 확장 가능한 객체 타입
interface User {
  id: string;
  name: string;
  email: string;
}

interface AdminUser extends User {
  permissions: string[];
}

// ✅ 타입 별칭: 유니온, 원시값, 함수 타입
type Status = 'pending' | 'approved' | 'rejected';
type EventHandler = (event: MouseEvent) => void;
type ApiResponse<T> = {
  data: T;
  success: boolean;
  message?: string;
};
```

#### Props 타입 정의
```typescript
// ✅ Props 인터페이스 명명
interface ButtonProps {
  // 필수 props
  label: string;
  onClick: (event: React.MouseEvent<HTMLButtonElement>) => void;
  
  // 선택적 props with 기본값 명시
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'small' | 'medium' | 'large';
  disabled?: boolean;
  
  // Boolean props 명명
  isLoading?: boolean;
  hasIcon?: boolean;
  
  // 컴포지션
  children?: React.ReactNode;
  className?: string;
}

// ✅ 제네릭 활용
interface ListProps<T> {
  items: T[];
  renderItem: (item: T, index: number) => React.ReactNode;
  keyExtractor: (item: T) => string;
  emptyMessage?: string;
}
```

#### 타입 가드 함수
```typescript
// API 응답 검증
function isApiSuccessResponse<T>(
  response: unknown
): response is ApiResponse<T> {
  return (
    typeof response === 'object' &&
    response !== null &&
    'success' in response &&
    'data' in response
  );
}

// 도메인 객체 검증
function isValidUser(data: unknown): data is User {
  return (
    typeof data === 'object' &&
    data !== null &&
    typeof (data as User).id === 'string' &&
    typeof (data as User).name === 'string' &&
    typeof (data as User).email === 'string'
  );
}

// 사용 예시
const handleApiResponse = (response: unknown) => {
  if (isApiSuccessResponse<User[]>(response)) {
    // response.data는 User[] 타입
    response.data.forEach(user => console.log(user.name));
  }
};
```

### 2. 제네릭 활용

#### 재사용 가능한 컴포넌트
```typescript
interface SelectProps<T> {
  options: T[];
  value: T | null;
  onChange: (value: T) => void;
  getOptionLabel: (option: T) => string;
  getOptionValue: (option: T) => string;
  placeholder?: string;
}

function Select<T>({
  options,
  value,
  onChange,
  getOptionLabel,
  getOptionValue,
  placeholder = "선택하세요"
}: SelectProps<T>) {
  return (
    <select
      value={value ? getOptionValue(value) : ''}
      onChange={(e) => {
        const selectedOption = options.find(
          option => getOptionValue(option) === e.target.value
        );
        if (selectedOption) {
          onChange(selectedOption);
        }
      }}
    >
      <option value="">{placeholder}</option>
      {options.map(option => (
        <option key={getOptionValue(option)} value={getOptionValue(option)}>
          {getOptionLabel(option)}
        </option>
      ))}
    </select>
  );
}

// 사용 예시
const FamilySelect = () => {
  const [selectedFamily, setSelectedFamily] = useState<Family | null>(null);
  
  return (
    <Select<Family>
      options={families}
      value={selectedFamily}
      onChange={setSelectedFamily}
      getOptionLabel={(family) => family.name}
      getOptionValue={(family) => family.id}
    />
  );
};
```

#### 유틸리티 타입 활용
```typescript
// Pick으로 필요한 필드만 선택
type CreateUserDto = Pick<User, 'name' | 'email'>;

// Omit으로 특정 필드 제외
type UpdateUserDto = Omit<User, 'id' | 'createdAt'>;

// Partial로 선택적 업데이트
type PatchUserDto = Partial<UpdateUserDto>;

// Record로 매핑 타입
type UserRolePermissions = Record<UserRole, string[]>;

// 커스텀 유틸리티 타입
type Optional<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>;
type CreateFamilyDto = Optional<Family, 'id' | 'createdAt' | 'updatedAt'>;
```

## React 컴포넌트 작성 규칙

### 1. 컴포넌트 구조
```typescript
// 1. Imports (외부 → 내부 순서)
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';

import { Button, Card, LoadingSpinner } from '@/components/common';
import { FamilyService } from '@/api/services';
import { useFamilyPermissions } from '@/hooks';
import type { Family, User } from '@/types';

// 2. 타입 정의
interface FamilyDetailProps {
  familyId: string;
  currentUser: User;
  onEdit?: (family: Family) => void;
}

// 3. 컴포넌트 정의
export const FamilyDetail: React.FC<FamilyDetailProps> = ({
  familyId,
  currentUser,
  onEdit
}) => {
  // 4. Hooks (useState → useEffect → 커스텀 훅 순서)
  const [selectedTab, setSelectedTab] = useState<'info' | 'members'>('info');
  const navigate = useNavigate();
  
  // 5. Queries & Mutations
  const { data: family, isLoading } = useQuery({
    queryKey: ['families', familyId],
    queryFn: () => FamilyService.getInstance().getById(familyId),
  });
  
  // 6. 파생 상태 (useMemo)
  const canEdit = useMemo(
    () => family && currentUser.id === family.createdBy,
    [family, currentUser.id]
  );
  
  // 7. 이벤트 핸들러 (useCallback)
  const handleEdit = useCallback(() => {
    if (family && onEdit) {
      onEdit(family);
    }
  }, [family, onEdit]);
  
  // 8. Effects
  useEffect(() => {
    if (family) {
      document.title = `${family.name} - Family Tree`;
    }
  }, [family]);
  
  // 9. 조기 반환
  if (isLoading) return <LoadingSpinner />;
  if (!family) return <div>Family not found</div>;
  
  // 10. 렌더링
  return (
    <Card>
      <div className="family-detail">
        <header className="family-header">
          <h1>{family.name}</h1>
          {canEdit && (
            <Button onClick={handleEdit}>Edit</Button>
          )}
        </header>
        
        <div className="family-content">
          {selectedTab === 'info' ? (
            <FamilyInfo family={family} />
          ) : (
            <FamilyMembers familyId={familyId} />
          )}
        </div>
      </div>
    </Card>
  );
};
```

### 2. 이벤트 핸들러 명명
```typescript
// ✅ 일관된 명명 패턴
const Component = () => {
  // handle + 동작
  const handleClick = () => {};
  const handleSubmit = () => {};
  const handleChange = () => {};
  
  // on + 이벤트 (props로 받는 경우)
  const { onSave, onCancel, onDelete } = props;
  
  // 비동기 액션
  const handleSaveAsync = async () => {};
  const handleDeleteConfirm = async () => {};
};
```

### 3. 조건부 렌더링
```typescript
// ✅ 명확한 조건부 렌더링
const Component = () => {
  // Early return 활용
  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  if (!data) return <EmptyState />;
  
  return (
    <div>
      {/* 단순 조건 */}
      {showHeader && <Header />}
      
      {/* 복잡한 조건은 변수로 분리 */}
      {(() => {
        if (user.role === 'admin') {
          return <AdminPanel />;
        }
        if (user.role === 'member') {
          return <MemberPanel />;
        }
        return <GuestPanel />;
      })()}
      
      {/* 배열 렌더링 */}
      {items.length > 0 ? (
        items.map(item => <Item key={item.id} data={item} />)
      ) : (
        <EmptyMessage />
      )}
    </div>
  );
};
```

## 스타일링 규칙

### 1. Tailwind CSS 사용 원칙
```typescript
// ✅ 클래스명 순서 (Tailwind 공식 권장)
// 1. Layout (display, position, top/right/bottom/left)
// 2. Box Model (width, height, margin, padding)
// 3. Typography (font-size, font-weight, color)
// 4. Visual (background, border, shadow)
// 5. Interactive (hover, focus, transition)

const Component = () => (
  <button
    className={clsx(
      // Layout
      'flex items-center justify-center',
      // Box Model  
      'w-full px-4 py-2',
      // Typography
      'text-sm font-medium text-white',
      // Visual
      'bg-blue-600 border border-transparent rounded-md shadow-sm',
      // Interactive
      'hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500',
      'transition-colors duration-200',
      // Conditional
      {
        'opacity-50 cursor-not-allowed': disabled,
        'bg-red-600 hover:bg-red-700': variant === 'danger',
      }
    )}
  >
    Click me
  </button>
);
```

### 2. 조건부 스타일링
```typescript
import clsx from 'clsx';

// ✅ clsx 라이브러리 사용
const Button = ({ variant, size, disabled, className, ...props }) => {
  const buttonClasses = clsx(
    // 기본 스타일
    'inline-flex items-center justify-center font-medium transition-colors',
    
    // Variant 스타일
    {
      'bg-blue-600 text-white hover:bg-blue-700': variant === 'primary',
      'bg-gray-200 text-gray-900 hover:bg-gray-300': variant === 'secondary',
      'bg-red-600 text-white hover:bg-red-700': variant === 'danger',
    },
    
    // Size 스타일
    {
      'px-3 py-1.5 text-sm': size === 'small',
      'px-4 py-2 text-base': size === 'medium',
      'px-6 py-3 text-lg': size === 'large',
    },
    
    // State 스타일
    {
      'opacity-50 cursor-not-allowed': disabled,
    },
    
    // 추가 클래스
    className
  );
  
  return <button className={buttonClasses} disabled={disabled} {...props} />;
};
```

### 3. CSS 변수 활용
```typescript
// 테마 변수를 CSS 속성으로 전달
const ThemeProvider = ({ theme, children }) => {
  const cssVars = {
    '--color-primary': theme.colors.primary,
    '--color-secondary': theme.colors.secondary,
    '--font-family': theme.fonts.body,
    '--border-radius': theme.borderRadius,
  } as React.CSSProperties;
  
  return (
    <div style={cssVars} className="theme-provider">
      {children}
    </div>
  );
};
```

## 성능 최적화 규칙

### 1. 메모이제이션 가이드라인
```typescript
// ✅ React.memo: 비용이 큰 컴포넌트
const ExpensiveComponent = React.memo<Props>(({ data, onAction }) => {
  // 복잡한 렌더링 로직
  return <div>{/* ... */}</div>;
});

// ✅ useMemo: 비용이 큰 계산
const Component = ({ items, filter }) => {
  const filteredItems = useMemo(
    () => items.filter(item => item.name.includes(filter)),
    [items, filter]
  );
  
  const aggregatedData = useMemo(
    () => calculateComplexAggregation(filteredItems),
    [filteredItems]
  );
  
  return <div>{/* ... */}</div>;
};

// ✅ useCallback: 자식에게 전달되는 함수
const Parent = ({ onSave }) => {
  const [items, setItems] = useState([]);
  
  const handleItemAdd = useCallback((newItem) => {
    setItems(prev => [...prev, newItem]);
  }, []);
  
  const handleItemDelete = useCallback((id) => {
    setItems(prev => prev.filter(item => item.id !== id));
  }, []);
  
  return (
    <ItemList
      items={items}
      onAdd={handleItemAdd}
      onDelete={handleItemDelete}
    />
  );
};
```

### 2. 리스트 렌더링 최적화
```typescript
// ✅ 안정적인 key 사용
const ItemList = ({ items }) => (
  <ul>
    {items.map(item => (
      <li key={item.id}>  {/* ❌ index 사용 금지 */}
        <ItemCard item={item} />
      </li>
    ))}
  </ul>
);

// ✅ 가상화가 필요한 큰 리스트
import { FixedSizeList as List } from 'react-window';

const VirtualizedList = ({ items }) => (
  <List
    height={600}
    itemCount={items.length}
    itemSize={50}
    itemData={items}
  >
    {({ index, style, data }) => (
      <div style={style}>
        <ItemCard item={data[index]} />
      </div>
    )}
  </List>
);
```

## 에러 처리 패턴

### 1. 에러 바운더리
```typescript
interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
}

class ErrorBoundary extends React.Component<
  React.PropsWithChildren<{}>,
  ErrorBoundaryState
> {
  constructor(props: React.PropsWithChildren<{}>) {
    super(props);
    this.state = { hasError: false };
  }
  
  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }
  
  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Error Boundary caught an error:', error, errorInfo);
    // 에러 리포팅 서비스로 전송
  }
  
  render() {
    if (this.state.hasError) {
      return (
        <div className="error-fallback">
          <h2>Something went wrong</h2>
          <button onClick={() => window.location.reload()}>
            Reload page
          </button>
        </div>
      );
    }
    
    return this.props.children;
  }
}
```

### 2. 비동기 에러 처리
```typescript
// ✅ React Query의 에러 처리
const Component = () => {
  const { data, isLoading, error } = useQuery({
    queryKey: ['data'],
    queryFn: fetchData,
    retry: 3,
    retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
  });
  
  if (error) {
    return <ErrorMessage error={error} />;
  }
  
  // ...
};

// ✅ 수동 에러 처리
const Component = () => {
  const [error, setError] = useState<Error | null>(null);
  
  const handleAction = async () => {
    try {
      setError(null);
      await performAction();
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error'));
    }
  };
  
  return (
    <div>
      {error && <ErrorAlert error={error} onDismiss={() => setError(null)} />}
      <button onClick={handleAction}>Action</button>
    </div>
  );
};
```

## 접근성 규칙

### 1. 시맨틱 HTML
```typescript
// ✅ 적절한 HTML 요소 사용
const Navigation = () => (
  <nav aria-label="Main navigation">
    <ul>
      <li><a href="/" aria-current="page">Home</a></li>
      <li><a href="/about">About</a></li>
    </ul>
  </nav>
);

// ✅ 폼 접근성
const ContactForm = () => (
  <form onSubmit={handleSubmit}>
    <div>
      <label htmlFor="name">Name *</label>
      <input
        id="name"
        type="text"
        required
        aria-describedby="name-error"
      />
      <div id="name-error" role="alert">
        {nameError}
      </div>
    </div>
    
    <fieldset>
      <legend>Contact method</legend>
      <label>
        <input type="radio" name="contact" value="email" />
        Email
      </label>
      <label>
        <input type="radio" name="contact" value="phone" />
        Phone
      </label>
    </fieldset>
  </form>
);
```

### 2. ARIA 속성
```typescript
// ✅ 적절한 ARIA 사용
const Modal = ({ isOpen, onClose, title, children }) => {
  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === 'Escape') onClose();
    };
    
    if (isOpen) {
      document.addEventListener('keydown', handleEscape);
      return () => document.removeEventListener('keydown', handleEscape);
    }
  }, [isOpen, onClose]);
  
  if (!isOpen) return null;
  
  return (
    <div 
      className="modal-overlay" 
      onClick={onClose}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <header>
          <h2 id="modal-title">{title}</h2>
          <button
            onClick={onClose}
            aria-label="Close modal"
            className="close-button"
          >
            ×
          </button>
        </header>
        <main>{children}</main>
      </div>
    </div>
  );
};
```

## 코드 리뷰 체크리스트

### TypeScript
- [ ] any 타입 사용하지 않았는가?
- [ ] 모든 props에 타입이 정의되었는가?
- [ ] 타입 가드가 필요한 곳에 구현되었는가?

### React
- [ ] 컴포넌트가 단일 책임을 가지는가?
- [ ] 불필요한 리렌더링이 방지되었는가?
- [ ] 이벤트 핸들러 명명이 일관적인가?

### 성능
- [ ] 리스트에 안정적인 key가 사용되었는가?
- [ ] 비용이 큰 계산에 memoization이 적용되었는가?
- [ ] 코드 분할이 적절히 적용되었는가?

### 접근성
- [ ] 시맨틱 HTML이 사용되었는가?
- [ ] ARIA 속성이 적절히 사용되었는가?
- [ ] 키보드 네비게이션이 가능한가?

---

최종 수정일: 2025-06-23