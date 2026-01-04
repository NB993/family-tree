# shadcn/ui 전체 리디자인 계획

## 현재 상태
- **빌드 도구**: Create React App (react-scripts 5.0.1)
- **React**: 19.0.0, TypeScript 4.9.5
- **Tailwind CSS**: 3.4.17 (shadcn 호환 설정 완료)
- **기존 컴포넌트**: Button, Card (CSS 모듈), Avatar (Radix UI)
- **페이지**: 11개 (HomePage, FamilyPage, LoginPage 등)

## 구현 단계

### Phase 1: 환경 설정

#### 1.1 의존성 설치
```bash
cd fe
npm install class-variance-authority clsx tailwind-merge
npm install @radix-ui/react-slot @radix-ui/react-dialog @radix-ui/react-dropdown-menu \
  @radix-ui/react-label @radix-ui/react-select @radix-ui/react-separator \
  @radix-ui/react-tabs @radix-ui/react-toast @radix-ui/react-tooltip \
  @radix-ui/react-popover @radix-ui/react-checkbox @radix-ui/react-switch \
  @radix-ui/react-scroll-area @radix-ui/react-accordion @radix-ui/react-alert-dialog
```

#### 1.2 CRACO 설정 (CRA path alias 지원)
```bash
npm install @craco/craco
```

**수정 파일:**
- `fe/package.json` - scripts를 craco로 변경
- `fe/craco.config.js` - 새로 생성
- `fe/tsconfig.json` - baseUrl, paths 추가

#### 1.3 유틸리티 설정
- `fe/src/lib/utils.ts` 생성 - cn() 함수 (clsx + tailwind-merge)

---

### Phase 2: Core 컴포넌트 생성

**위치**: `fe/src/components/ui/`

| 컴포넌트 | 파일명 | 용도 |
|---------|--------|------|
| Button | button.tsx | 버튼 (variant: default, secondary, outline, ghost, destructive) |
| Card | card.tsx | 카드 컨테이너 (CardHeader, CardContent, CardFooter) |
| Input | input.tsx | 텍스트 입력 |
| Label | label.tsx | 폼 라벨 |
| Textarea | textarea.tsx | 멀티라인 입력 |
| Select | select.tsx | 드롭다운 선택 |

---

### Phase 3: Feedback 컴포넌트

| 컴포넌트 | 파일명 | 용도 |
|---------|--------|------|
| Toast | toast.tsx, toaster.tsx | 알림 메시지 |
| Alert | alert.tsx | 경고/정보 메시지 |
| Badge | badge.tsx | 상태 표시 |
| Skeleton | skeleton.tsx | 로딩 상태 |

---

### Phase 4: Navigation 컴포넌트

| 컴포넌트 | 파일명 | 용도 |
|---------|--------|------|
| Dialog | dialog.tsx | 모달 다이얼로그 |
| DropdownMenu | dropdown-menu.tsx | 드롭다운 메뉴 |
| Tabs | tabs.tsx | 탭 네비게이션 |
| Separator | separator.tsx | 구분선 |
| Sheet | sheet.tsx | 사이드 패널 |

---

### Phase 5: Form 컴포넌트

| 컴포넌트 | 파일명 | 용도 |
|---------|--------|------|
| Checkbox | checkbox.tsx | 체크박스 |
| Switch | switch.tsx | 토글 스위치 |
| ScrollArea | scroll-area.tsx | 커스텀 스크롤 |
| Tooltip | tooltip.tsx | 툴팁 |
| Popover | popover.tsx | 팝오버 |

---

### Phase 6: 페이지별 마이그레이션

**우선순위 순서:**

1. **LoginPage** - Card, Button 사용 (단순)
2. **NotFoundPage** - Button만 사용 (단순)
3. **CreateFamilyPage** - Card, Button, Input, Label, Select
4. **FamilyPage** - Card, Button, Avatar, Badge
5. **FamilySearchPage** - Card, Input, Button, Skeleton
6. **HomePage** - 복잡한 레이아웃, 다수 컴포넌트
7. **FamilyMembersPage** - 리스트 + 다수 컴포넌트
8. **나머지 페이지들** - CreateInvitePage, InviteResponsePage 등

---

### Phase 7: 정리

- `fe/src/components/common/Button/` 삭제
- `fe/src/components/common/Card/` 삭제
- CSS 모듈 파일들 삭제
- 불필요한 import 정리

---

## 수정될 주요 파일

### 설정 파일
- `fe/package.json` - 의존성 추가, scripts 수정
- `fe/tsconfig.json` - path alias 추가
- `fe/craco.config.js` - 새로 생성

### 새로 생성할 파일
- `fe/src/lib/utils.ts`
- `fe/src/components/ui/button.tsx`
- `fe/src/components/ui/card.tsx`
- `fe/src/components/ui/input.tsx`
- `fe/src/components/ui/label.tsx`
- `fe/src/components/ui/textarea.tsx`
- `fe/src/components/ui/select.tsx`
- `fe/src/components/ui/dialog.tsx`
- `fe/src/components/ui/toast.tsx`
- `fe/src/components/ui/toaster.tsx`
- `fe/src/components/ui/badge.tsx`
- `fe/src/components/ui/skeleton.tsx`
- `fe/src/components/ui/dropdown-menu.tsx`
- `fe/src/components/ui/tabs.tsx`
- `fe/src/components/ui/separator.tsx`
- `fe/src/components/ui/sheet.tsx`
- `fe/src/components/ui/checkbox.tsx`
- `fe/src/components/ui/switch.tsx`
- `fe/src/components/ui/scroll-area.tsx`
- `fe/src/components/ui/tooltip.tsx`
- `fe/src/components/ui/popover.tsx`
- `fe/src/components/ui/accordion.tsx`
- `fe/src/components/ui/alert-dialog.tsx`
- `fe/src/components/ui/alert.tsx`

### 수정할 페이지 파일
- `fe/src/pages/LoginPage.tsx`
- `fe/src/pages/NotFoundPage.tsx`
- `fe/src/pages/CreateFamilyPage.tsx`
- `fe/src/pages/FamilyPage.tsx`
- `fe/src/pages/FamilySearchPage.tsx`
- `fe/src/pages/HomePage.tsx`
- `fe/src/pages/FamilyMembersPage.tsx`
- `fe/src/pages/CreateInvitePage.tsx`
- `fe/src/pages/InviteResponsePage.tsx`
- `fe/src/pages/InviteCallbackPage.tsx`
- `fe/src/pages/OAuth2CallbackPage.tsx`

### 삭제할 파일
- `fe/src/components/common/Button/Button.tsx`
- `fe/src/components/common/Button/Button.css`
- `fe/src/components/common/Card/Card.tsx`
- `fe/src/components/common/Card/Card.css`

---

## 주의사항

1. **CRA 제약**: shadcn CLI 직접 사용 불가, 수동으로 컴포넌트 생성
2. **브랜드 컬러 유지**: 기존 orange/amber 테마 유지 (이미 CSS 변수로 설정됨)
3. **기능 보존**: 기존 기능 동작 유지하면서 스타일만 변경
4. **점진적 적용**: 한 페이지씩 마이그레이션하여 문제 최소화
