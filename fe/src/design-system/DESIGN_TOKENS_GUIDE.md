# Design Token System Guide

## Overview

이 프로젝트는 일관된 디자인을 위해 **토큰 기반 디자인 시스템**을 사용합니다.

### 토큰 구조

```
tokens.ts (정의) → index.css (CSS 변수) → tailwind.config.js (Tailwind 클래스)
```

| 파일 | 역할 |
|------|------|
| `tokens.ts` | 디자인 토큰의 원본 정의 (참조용) |
| `index.css` | CSS 변수로 토큰 적용 (런타임 사용) |
| `tailwind.config.js` | Tailwind 클래스와 CSS 변수 연동 |

---

## Typography (타이포그래피)

### Font Size 사용 규칙

| 토큰 | 크기 | 용도 | Tailwind 클래스 |
|------|------|------|-----------------|
| `2xs` | 9px | 최소 크기, 법적 고지 등 | `text-2xs` |
| `xs-sm` | 10px | 보조 텍스트, 태그 | `text-xs-sm` |
| `xs-md` | 11px | 작은 보조 텍스트 | `text-xs-md` |
| `caption` | 12px | 캡션, 힌트 텍스트 | `text-caption` |
| `body2` | 14px | 본문 (보조) | `text-body2` |
| `body1` | 16px | 본문 (기본) | `text-body1` |
| `subtitle` | 18px | 부제목 | `text-subtitle` |
| `h6` ~ `h1` | 20~32px | 헤딩 | `text-h6` ~ `text-h1` |

### 사용 예시

```tsx
// Good - 토큰 사용
<span className="text-xs-sm">보조 텍스트</span>
<p className="text-body1">본문 텍스트</p>
<h2 className="text-h2">제목</h2>

// Bad - 임의 값 사용 (리팩토링 대상)
<span className="text-[10px]">보조 텍스트</span>
```

### Font Weight

| 토큰 | 값 | Tailwind 클래스 |
|------|-----|-----------------|
| light | 300 | `font-light` |
| regular | 400 | `font-normal` |
| medium | 500 | `font-medium` |
| semibold | 600 | `font-semibold` |
| bold | 700 | `font-bold` |

---

## Colors (색상)

### Primary Colors (브랜드 컬러)

오렌지 계열의 따뜻한 브랜드 컬러입니다.

| 용도 | Tailwind 클래스 | HEX |
|------|-----------------|-----|
| 배경 (밝은) | `bg-primary-50` | #fff7ed |
| 기본 | `bg-primary-500` | #f97316 |
| 호버 | `bg-primary-600` | #ea580c |
| 텍스트 | `text-primary-500` | #f97316 |

### Semantic Colors

| 용도 | 클래스 | 색상 |
|------|--------|------|
| 성공 | `text-success-500`, `bg-success-50` | 초록 |
| 경고 | `text-warning-500`, `bg-warning-50` | 노랑 |
| 에러 | `text-error-500`, `bg-error-50` | 빨강 |
| 정보 | `text-info-500`, `bg-info-50` | 파랑 |

### External Brand Colors

외부 브랜드 색상은 별도 토큰으로 관리합니다.

| 브랜드 | 배경 | 호버 | 텍스트 |
|--------|------|------|--------|
| Kakao | `bg-kakao` | `hover:bg-kakao-hover` | `text-kakao-text` |

```tsx
// 카카오 로그인 버튼
<button className="bg-kakao hover:bg-kakao-hover text-kakao-text">
  카카오로 로그인
</button>
```

### 다크 모드

CSS 변수 기반으로 다크 모드를 지원합니다.

```tsx
// 자동으로 다크 모드 대응
<div className="bg-background text-foreground">
  자동으로 라이트/다크 전환됨
</div>
```

---

## Spacing (간격)

### 4px 기반 스페이싱 시스템

| 토큰 | 값 | CSS 변수 | 용도 |
|------|-----|----------|------|
| 0.5 | 2px | `--spacing-0-5` | 미세 조정 |
| 1 | 4px | `--spacing-1` | 아이콘 간격 |
| 2 | 8px | `--spacing-2` | 요소 내부 |
| 3 | 12px | `--spacing-3` | 작은 패딩 |
| 4 | 16px | `--spacing-4` | 기본 패딩 |
| 6 | 24px | `--spacing-6` | 섹션 간격 |
| 8 | 32px | `--spacing-8` | 큰 섹션 |

### Tailwind에서 사용

```tsx
// 패딩/마진
<div className="p-4">16px 패딩</div>
<div className="m-2">8px 마진</div>
<div className="gap-3">12px 갭</div>
```

---

## Border Radius

| 토큰 | 값 | Tailwind 클래스 |
|------|-----|-----------------|
| sm | 4px | `rounded-sm` |
| DEFAULT | 8px | `rounded` |
| md | 8px | `rounded-md` |
| lg | 12px | `rounded-lg` |
| xl | 16px | `rounded-xl` |
| full | 9999px | `rounded-full` |

---

## Z-Index

레이어 순서를 위한 z-index 토큰입니다.

| 토큰 | 값 | Tailwind 클래스 | 용도 |
|------|-----|-----------------|------|
| base | 0 | `z-base` | 기본 레이어 |
| dropdown | 10 | `z-dropdown` | 드롭다운 메뉴 |
| sticky | 20 | `z-sticky` | 스티키 헤더 |
| fixed | 30 | `z-fixed` | 고정 요소 |
| modal-backdrop | 40 | `z-modal-backdrop` | 모달 배경 |
| modal | 50 | `z-modal` | 모달 |
| popover | 60 | `z-popover` | 팝오버 |
| tooltip | 70 | `z-tooltip` | 툴팁 |
| notification | 80 | `z-notification` | 알림 |

```tsx
// Good - 토큰 사용
<div className="z-modal">모달</div>

// Bad - 임의 값 (리팩토링 대상)
<div className="z-[100]">모달</div>
```

---

## Transitions

| 토큰 | 값 | Tailwind 클래스 |
|------|-----|-----------------|
| fast | 150ms | `duration-fast` |
| normal | 300ms | `duration-normal` |
| slow | 500ms | `duration-slow` |

```tsx
<button className="transition-all duration-fast ease-out">
  빠른 전환
</button>
```

---

## Component Tokens

### Button

| 사이즈 | 높이 | CSS 변수 |
|--------|------|----------|
| sm | 36px | `--button-height-sm` |
| md | 44px | `--button-height-md` |
| lg | 52px | `--button-height-lg` |

### Input

| 사이즈 | 높이 | CSS 변수 |
|--------|------|----------|
| sm | 36px | `--input-height-sm` |
| md | 44px | `--input-height-md` |
| lg | 52px | `--input-height-lg` |

---

## Arbitrary Values 사용 규칙

### 허용되는 경우

1. **외부 라이브러리 호환**: Radix UI 등 외부 라이브러리의 특수 값
   ```tsx
   rounded-[inherit]  // 부모 상속
   ```

2. **일회성 특수 값**: 디자인 시스템에 없는 특수한 경우
   ```tsx
   max-w-[400px]  // 앱 쉘 최대 너비
   ```

### 금지되는 경우

1. **폰트 크기**: 반드시 토큰 사용
   ```tsx
   // Bad
   text-[10px]

   // Good
   text-xs-sm
   ```

2. **색상**: 반드시 토큰 사용
   ```tsx
   // Bad
   bg-[#FEE500]

   // Good
   bg-kakao
   ```

3. **z-index**: 반드시 토큰 사용
   ```tsx
   // Bad
   z-[100]

   // Good
   z-notification
   ```

---

## Migration Guide

기존 코드에서 임의 값을 토큰으로 교체하는 가이드입니다.

### 폰트 크기 마이그레이션

| 기존 (임의 값) | 변경 (토큰) |
|----------------|-------------|
| `text-[9px]` | `text-2xs` |
| `text-[10px]` | `text-xs-sm` |
| `text-[11px]` | `text-xs-md` |
| `text-[12px]` | `text-caption` |

### 색상 마이그레이션

| 기존 (임의 값) | 변경 (토큰) |
|----------------|-------------|
| `bg-[#FEE500]` | `bg-kakao` |
| `bg-[#FDD835]` | `bg-kakao-hover` |
| `text-[#191919]` | `text-kakao-text` |

---

## Quick Reference

```tsx
// Typography
text-2xs          // 9px
text-xs-sm        // 10px
text-xs-md        // 11px
text-caption      // 12px
text-body2        // 14px
text-body1        // 16px

// Colors
bg-primary-500    // 브랜드 오렌지
bg-kakao          // 카카오 노랑
text-foreground   // 기본 텍스트 (다크모드 대응)

// Z-Index
z-dropdown        // 10
z-modal           // 50
z-notification    // 80

// Transitions
duration-fast     // 150ms
duration-normal   // 300ms
```
