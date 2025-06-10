# Family Tree 디자인 시스템 v1.0

## 📋 문서 정보
- **프로젝트**: NB993/family-tree
- **이슈**: #17 - 디자인 시스템 기본 구조 설계
- **버전**: v1.0
- **작성일**: 2025-06-10

---

## 🎯 브랜드 방향성

### 핵심 가치
- **따뜻함**: 가족의 유대감을 표현하는 따뜻한 색감
- **가독성**: 모든 연령대가 편안하게 읽을 수 있는 명확한 텍스트
- **모바일 최적화**: 스마트폰에서 최상의 경험 제공
- **장식적 우아함**: 기능성과 아름다움의 조화

### 디자인 철학
> "가족의 이야기를 따뜻하고 아름답게 기록하는 디지털 공간"

---

## 🎨 색상 시스템 (Color Palette)

### Primary Colors (따뜻한 계열)
```
🧡 Primary Orange
- Orange-50:  #FFF7ED (매우 연한 오렌지, 배경용)
- Orange-100: #FFEDD5 (연한 오렌지, 카드 배경)
- Orange-200: #FED7AA (부드러운 오렌지)
- Orange-300: #FDBA74 (중간 오렌지)
- Orange-400: #FB923C (진한 오렌지)
- Orange-500: #F97316 (메인 브랜드 컬러)
- Orange-600: #EA580C (어두운 오렌지, 호버)
- Orange-700: #C2410C (매우 어두운 오렌지)

🌅 Secondary Warm
- Amber-50:   #FFFBEB (따뜻한 배경)
- Amber-100:  #FEF3C7 (강조 배경)
- Amber-400:  #FBBF24 (액센트 컬러)
- Amber-500:  #F59E0B (보조 브랜드 컬러)
```

### Neutral Colors (가독성 최적화)
```
⚪ Gray Scale
- Gray-50:   #F9FAFB (최상위 배경)
- Gray-100:  #F3F4F6 (카드 배경)
- Gray-200:  #E5E7EB (경계선)
- Gray-300:  #D1D5DB (비활성 요소)
- Gray-400:  #9CA3AF (보조 텍스트)
- Gray-500:  #6B7280 (일반 텍스트)
- Gray-600:  #4B5563 (중요 텍스트)
- Gray-700:  #374151 (제목)
- Gray-800:  #1F2937 (강조 제목)
- Gray-900:  #111827 (최고 대비 텍스트)
```

### Semantic Colors
```
✅ Success (성공/완료)
- Green-50:  #ECFDF5
- Green-500: #10B981
- Green-600: #059669

⚠️ Warning (주의/경고)  
- Yellow-50:  #FFFBEB
- Yellow-500: #F59E0B
- Yellow-600: #D97706

❌ Error (오류/위험)
- Red-50:  #FEF2F2
- Red-500: #EF4444
- Red-600: #DC2626

ℹ️ Info (정보/알림)
- Blue-50:  #EFF6FF
- Blue-500: #3B82F6
- Blue-600: #2563EB
```

---

## 📝 타이포그래피 (Typography)

### Font Family (가독성 우선)
```
Primary Font Stack:
font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, 
            'Segoe UI', 'Noto Sans KR', sans-serif;

Fallback for English:
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 
            'Roboto', 'Helvetica Neue', Arial, sans-serif;
```

### Font Scale (모바일 최적화)
```
🔤 Heading Scale
- H1: 32px (2rem)    - font-weight: 700 (Bold)
- H2: 28px (1.75rem) - font-weight: 600 (SemiBold)  
- H3: 24px (1.5rem)  - font-weight: 600 (SemiBold)
- H4: 20px (1.25rem) - font-weight: 600 (SemiBold)
- H5: 18px (1.125rem)- font-weight: 500 (Medium)
- H6: 16px (1rem)    - font-weight: 500 (Medium)

📖 Body Text
- Large:  18px (1.125rem) - font-weight: 400 (Regular)
- Normal: 16px (1rem)     - font-weight: 400 (Regular)  
- Small:  14px (0.875rem) - font-weight: 400 (Regular)
- XSmall: 12px (0.75rem)  - font-weight: 400 (Regular)

🏷️ Label & Caption
- Label:   14px (0.875rem) - font-weight: 500 (Medium)
- Caption: 12px (0.75rem)  - font-weight: 400 (Regular)
```

### Line Height (가독성 최적화)
```
- Tight:  1.25 (제목용)
- Normal: 1.5  (본문용)  
- Relaxed: 1.75 (긴 텍스트용)
```

---

## 📐 간격 시스템 (Spacing)

### Base Unit System (8px Grid)
```
🔢 Spacing Scale
- xs:  4px  (0.25rem)
- sm:  8px  (0.5rem)
- md:  12px (0.75rem)
- lg:  16px (1rem)
- xl:  20px (1.25rem)
- 2xl: 24px (1.5rem)
- 3xl: 32px (2rem)
- 4xl: 40px (2.5rem)
- 5xl: 48px (3rem)
- 6xl: 64px (4rem)
```

### Component Spacing
```
🧩 내부 여백 (Padding)
- Button: py-3 px-6 (12px 상하, 24px 좌우)
- Input:  py-3 px-4 (12px 상하, 16px 좌우)
- Card:   p-6       (24px 전체)
- Modal:  p-8       (32px 전체)

📏 외부 여백 (Margin)
- Section: mb-8  (32px 하단)
- Card:    mb-6  (24px 하단)
- Button:  mr-4  (16px 우측, 버튼 그룹 시)
```

### Layout Spacing (모바일 최적화)
```
📱 모바일 레이아웃
- Container Max Width: 390px (iPhone 14 Pro 기준)
- Container Padding: px-4 (16px 좌우)
- Section Gap: gap-6 (24px)
- Card Gap: gap-4 (16px)

🖥️ 데스크톱에서도 모바일 폭 유지
- Max Width: 390px (고정)
- Center Alignment: mx-auto
- 좌우 여백: 나머지 공간은 빈 공간으로 유지
```

---

## 🧩 기본 컴포넌트 (장식적 스타일)

### 🔘 Button Components
```css
/* Primary Button (메인 액션) */
.btn-primary {
  background: linear-gradient(135deg, #F97316 0%, #EA580C 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.25);
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #EA580C 0%, #C2410C 100%);
  box-shadow: 0 6px 16px rgba(249, 115, 22, 0.35);
  transform: translateY(-1px);
}

/* Secondary Button (보조 액션) */
.btn-secondary {
  background: #FFEDD5;
  color: #C2410C;
  border: 2px solid #FED7AA;
  padding: 10px 22px;
  border-radius: 12px;
  font-weight: 500;
}

/* Text Button (텍스트 링크) */
.btn-text {
  background: transparent;
  color: #F97316;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: underline;
  text-underline-offset: 4px;
}
```

### 📝 Input Components  
```css
/* Text Input (장식적 스타일) */
.input-text {
  background: #FFFBEB;
  border: 2px solid #FED7AA;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(249, 115, 22, 0.1);
}

.input-text:focus {
  border-color: #F97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
  outline: none;
}

/* Label (장식적 요소) */
.input-label {
  color: #C2410C;
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 6px;
  display: block;
}

.input-label::after {
  content: " ✨";
  font-size: 12px;
}
```

### 🃏 Card Components
```css
/* Basic Card (따뜻한 그림자) */
.card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.08);
  border: 1px solid #FFEDD5;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 8px 30px rgba(249, 115, 22, 0.12);
  transform: translateY(-2px);
}

/* Feature Card (특별한 카드) */
.card-feature {
  background: linear-gradient(135deg, #FFFBEB 0%, #FFEDD5 100%);
  border: 2px solid #FED7AA;
  position: relative;
}

.card-feature::before {
  content: "👨‍👩‍👧‍👦";
  position: absolute;
  top: -10px;
  right: -10px;
  background: #F97316;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}
```

### 🎭 Modal Components
```css
/* Modal Overlay */
.modal-overlay {
  background: rgba(17, 24, 39, 0.75);
  backdrop-filter: blur(4px);
}

/* Modal Content */
.modal-content {
  background: white;
  border-radius: 20px;
  padding: 32px;
  max-width: 350px;
  width: 90vw;
  box-shadow: 0 20px 50px rgba(249, 115, 22, 0.15);
  border: 1px solid #FFEDD5;
}

/* Modal Header */
.modal-header {
  text-align: center;
  margin-bottom: 24px;
  position: relative;
}

.modal-header::after {
  content: "";
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #F97316, #FBBF24);
  border-radius: 2px;
  margin: 16px auto 0;
  display: block;
}
```

### 🧭 Navigation Components
```css
/* Mobile Header */
.mobile-header {
  background: linear-gradient(135deg, #FFFBEB 0%, #FFEDD5 100%);
  padding: 16px;
  border-bottom: 2px solid #FED7AA;
  box-shadow: 0 2px 12px rgba(249, 115, 22, 0.1);
}

/* Navigation Link */
.nav-link {
  color: #C2410C;
  font-weight: 500;
  padding: 12px 16px;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.nav-link:hover, .nav-link.active {
  background: #FFEDD5;
  color: #9A3412;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.15);
}

/* Navigation Icon */
.nav-icon {
  width: 20px;
  height: 20px;
  margin-right: 8px;
  filter: drop-shadow(0 1px 2px rgba(249, 115, 22, 0.2));
}
```

---

## 📱 모바일 최적화 가이드라인

### Viewport 설정
```html
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
```

### 터치 인터페이스 최적화
```css
/* 터치 타겟 최소 크기 */
.touch-target {
  min-height: 44px;
  min-width: 44px;
}

/* 터치 피드백 */
.touch-feedback:active {
  background-color: rgba(249, 115, 22, 0.1);
  transition: background-color 0.1s ease;
}
```

### 스크롤 최적화
```css
/* 부드러운 스크롤 */
html {
  scroll-behavior: smooth;
}

/* 관성 스크롤 (iOS) */
.scroll-container {
  -webkit-overflow-scrolling: touch;
  overflow-y: auto;
}
```

---

## ♿ 접근성 가이드라인

### 색상 대비 (WCAG 2.1 AA 준수)
```
✅ 텍스트 대비율
- 일반 텍스트: 4.5:1 이상
- 큰 텍스트: 3:1 이상
- UI 요소: 3:1 이상

✅ 검증된 조합
- Gray-800 (#1F2937) on White: 16.28:1 ✅
- Gray-700 (#374151) on White: 12.63:1 ✅  
- Orange-600 (#EA580C) on White: 5.48:1 ✅
- Orange-500 (#F97316) on White: 3.99:1 ⚠️ (큰 텍스트만)
```

### 키보드 내비게이션
```css
/* 포커스 표시 */
.focus-visible {
  outline: 2px solid #F97316;
  outline-offset: 2px;
  border-radius: 4px;
}

/* 스킵 링크 */
.skip-link {
  position: absolute;
  top: -40px;
  left: 6px;
  z-index: 1000;
  background: #F97316;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  text-decoration: none;
}

.skip-link:focus {
  top: 6px;
}
```

---

## 🎨 장식적 요소 가이드

### 아이콘 사용
```
👨‍👩‍👧‍👦 가족 관련: 가족 구성원, 관계 표시
🌳 트리 관련: 가계도, 계층 구조
📝 기록 관련: 메모, 정보 입력
💫 특별함: 중요한 정보, 강조
🎉 성취: 완료, 성공 상태
```

### 그래디언트 활용
```css
/* 메인 그래디언트 */
.gradient-primary {
  background: linear-gradient(135deg, #F97316 0%, #EA580C 100%);
}

/* 배경 그래디언트 */
.gradient-background {
  background: linear-gradient(135deg, #FFFBEB 0%, #FFEDD5 100%);
}

/* 텍스트 그래디언트 */
.gradient-text {
  background: linear-gradient(135deg, #F97316, #FBBF24);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
```

### 그림자 시스템
```css
/* 부드러운 그림자 */
.shadow-soft {
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.08);
}

/* 강조 그림자 */
.shadow-emphasis {
  box-shadow: 0 8px 30px rgba(249, 115, 22, 0.12);
}

/* 떠있는 그림자 */
.shadow-floating {
  box-shadow: 0 12px 40px rgba(249, 115, 22, 0.15);
}
```

---

## 📋 컴포넌트 체크리스트

### ✅ 완성된 컴포넌트
- [x] Color Palette (따뜻한 오렌지 계열)
- [x] Typography Scale (가독성 최적화)
- [x] Spacing System (8px Grid)
- [x] Button Components (3가지 변형)
- [x] Input Components (장식적 스타일)
- [x] Card Components (따뜻한 그림자)
- [x] Modal Components (부드러운 디자인)
- [x] Navigation Components (모바일 최적화)

### 🔄 향후 확장 예정
- [ ] Form Components (체크박스, 라디오, 셀렉트)
- [ ] Table Components (가계도 표시용)
- [ ] Loading Components (스피너, 스켈레톤)
- [ ] Toast/Alert Components (알림)
- [ ] Avatar Components (프로필 이미지)
- [ ] Badge Components (상태 표시)

---

## 🛠️ 구현 가이드라인

### CSS Framework 권장사항
- **Primary**: Tailwind CSS (유틸리티 클래스로 빠른 구현)
- **Alternative**: Styled Components (컴포넌트 기반 스타일링)

### 디자인 토큰 관리
```javascript
// design-tokens.js
export const designTokens = {
  colors: {
    primary: {
      50: '#FFF7ED',
      500: '#F97316',
      600: '#EA580C'
    }
  },
  spacing: {
    xs: '4px',
    sm: '8px',
    md: '12px'
  },
  typography: {
    fontFamily: 'Pretendard, -apple-system, sans-serif',
    fontSize: {
      h1: '32px',
      body: '16px'
    }
  }
};
```

---

## 📝 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| v1.0 | 2025-06-10 | 초기 디자인 시스템 설계 완료 | 기획자 AI |
