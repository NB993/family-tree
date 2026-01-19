# 디자인 토큰 시스템 - 학습 회고

> 날짜: 2026-01-19
> 소스: 세션
> 작업 요약: FE 디자인 토큰 시스템을 CSS 변수 기반으로 통합하고 Tailwind와 연동

## 오늘 한 작업

가족 관계 관리 앱의 프론트엔드 디자인 토큰 시스템을 정비했습니다:

1. `index.css`에 CSS 변수로 모든 토큰 정의 (색상, 폰트, 스페이싱, z-index, 트랜지션)
2. `tailwind.config.js`가 CSS 변수를 참조하도록 연동
3. 누락된 토큰 추가 (9px, 10px, 11px 폰트 크기, 카카오 브랜드 색상)
4. 앱 특성을 고려한 트랜지션 시간 조정
5. 디자인 토큰 가이드라인 문서 작성

---

## 등장한 기술/개념들

### 1. 디자인 토큰 (Design Tokens)

#### 이게 뭐야?

디자인 토큰은 **디자인 시스템의 가장 작은 단위**입니다. 색상, 폰트 크기, 간격 같은 값에 이름을 붙여서 재사용하는 방식입니다.

```css
/* 토큰 없이 */
color: #f97316;
font-size: 16px;

/* 토큰 사용 */
color: var(--primary);
font-size: var(--font-size-base);
```

마치 코드에서 매직 넘버 대신 상수를 쓰는 것과 같습니다.

#### 왜 필요해?

**일관성 문제를 해결합니다.**

"이 버튼 색상이 저 버튼 색상이랑 살짝 다른데?" 같은 상황을 방지합니다. 모든 곳에서 같은 토큰을 쓰면 자동으로 일관됩니다.

**유지보수가 쉬워집니다.**

브랜드 색상을 바꾸고 싶을 때, 토큰 하나만 수정하면 전체 앱에 반영됩니다. 100군데를 찾아다니며 고칠 필요가 없어요.

#### 어떻게 탄생했어?

2010년대 중반, 대규모 프로젝트에서 디자이너와 개발자 간 소통 문제가 심각해졌습니다. 디자이너는 "Primary Blue"라고 하는데 개발자는 "#2563eb"를 쓰고, 다른 개발자는 "#3b82f6"을 쓰는 혼란이 생겼죠.

Salesforce가 2019년에 "Design Tokens" 개념을 공식화하고, 여러 대기업들이 채택하면서 표준처럼 자리 잡았습니다.

#### 어떻게 동작해?

```
정의 (tokens.ts / index.css)
    ↓
변환 (Tailwind config)
    ↓
사용 (컴포넌트에서 클래스로)
```

1. 한 곳에서 값을 정의합니다
2. 빌드 도구가 이 값을 CSS나 JavaScript로 변환합니다
3. 개발자는 이름으로 값을 참조합니다

#### 장점은?

- **일관성**: 모든 곳에서 같은 값 사용
- **유지보수성**: 한 곳만 수정하면 전체 반영
- **소통**: 디자이너-개발자가 같은 언어 사용
- **테마 지원**: 다크 모드 구현이 쉬워짐

#### 단점은?

- **초기 설정 비용**: 토큰 시스템을 구축하는 데 시간이 걸림
- **추상화 비용**: `#f97316` 대신 `--primary`를 기억해야 함
- **오버헤드**: 작은 프로젝트에선 과할 수 있음

#### 대안은 뭐가 있어?

- **CSS-in-JS**: styled-components의 ThemeProvider
- **Sass 변수**: `$primary-color`
- **하드코딩**: 그냥 값을 직접 쓰기 (작은 프로젝트)

#### 트레이드오프

| 얻는 것 | 잃는 것 |
|---------|---------|
| 일관성 | 초기 설정 시간 |
| 유지보수성 | 추상화 학습 비용 |
| 테마 지원 | 복잡성 증가 |

---

### 2. CSS Custom Properties (CSS 변수)

#### 이게 뭐야?

CSS 변수는 **CSS 안에서 값을 저장하고 재사용**할 수 있게 해주는 기능입니다.

```css
:root {
  --primary: #f97316;  /* 변수 선언 */
}

button {
  background: var(--primary);  /* 변수 사용 */
}
```

`--`로 시작하고, `var()`로 사용합니다.

#### 왜 필요해?

**런타임에 값을 바꿀 수 있습니다.**

Sass 변수(`$primary`)는 빌드 시점에 값이 고정됩니다. CSS 변수는 브라우저에서 실행 중에도 바꿀 수 있어요. 그래서 다크 모드 전환이 가능합니다.

```css
:root { --background: white; }
.dark { --background: black; }  /* 클래스만 바꾸면 전환 */
```

#### 어떻게 탄생했어?

CSS 프리프로세서(Sass, Less)가 변수 기능으로 인기를 끌자, CSS 표준에서도 변수를 지원하기로 했습니다. 2015년에 표준이 되었고, 이제 모든 브라우저가 지원합니다.

Sass 변수와 달리 **런타임에 동작**한다는 게 가장 큰 차이점입니다.

#### 어떻게 동작해?

1. `:root` (HTML 요소)에 변수를 선언합니다
2. 자식 요소들이 이 변수를 상속받습니다
3. 특정 요소에서 변수를 재정의하면 그 범위 내에서 덮어씁니다

```css
:root { --color: blue; }     /* 전역 */
.dark { --color: white; }    /* .dark 안에서만 white */
```

이건 CSS의 "캐스케이딩" 특성을 그대로 따릅니다.

#### 장점은?

- **런타임 변경**: JavaScript 없이 테마 전환 가능
- **상속**: 부모-자식 관계로 스코프 지정
- **표준**: 별도 빌드 도구 불필요

#### 단점은?

- **IE 미지원**: Internet Explorer에서 동작 안 함 (요즘은 상관없음)
- **타입 없음**: 문자열로만 저장 (숫자 연산 제한)

#### 대안은 뭐가 있어?

- **Sass 변수**: 빌드 타임 변수, 연산 기능 강력
- **CSS-in-JS**: JavaScript 객체로 스타일 관리
- **Tailwind 테마**: `tailwind.config.js`에서 관리

#### 트레이드오프

| CSS 변수 | Sass 변수 |
|----------|-----------|
| 런타임 변경 O | 런타임 변경 X |
| 브라우저 기본 지원 | 빌드 도구 필요 |
| 연산 기능 약함 | 연산 기능 강력 |

---

### 3. 8px 그리드 시스템

#### 이게 뭐야?

모든 간격과 크기를 **8의 배수**로 정하는 디자인 시스템입니다.

```
8px, 16px, 24px, 32px, 40px, 48px...
```

이 프로젝트에서는 Tailwind의 숫자 단위가 8px씩 증가합니다:
- `p-1` = 8px
- `p-2` = 16px
- `p-3` = 24px

#### 왜 필요해?

**시각적 조화**를 만들어줍니다.

인간의 눈은 규칙적인 패턴을 편안하게 느낍니다. 5px, 13px, 22px 같은 무작위 값보다 8px, 16px, 24px이 더 정돈되어 보입니다.

**개발자 의사결정 감소**: "여기 패딩 몇으로 하지?" 고민 대신 가장 가까운 8의 배수를 선택하면 됩니다.

#### 어떻게 탄생했어?

Google의 Material Design에서 대중화되었습니다. 대부분의 디스플레이가 8로 나누어 떨어지는 해상도를 가지고 있어서 (1920×1080, 1280×720 등), 8px 단위가 픽셀에 깔끔하게 맞아떨어집니다.

#### 왜 4px이 아니라 8px?

4px도 많이 쓰입니다. 하지만:
- 8px: 덜 세밀하지만 빠른 의사결정
- 4px: 더 세밀하지만 선택지가 많아 고민됨

정보 밀도가 높은 앱(대시보드, 스프레드시트)은 4px, 일반 앱은 8px가 적당합니다.

---

## 꼬리 질문 타임

### Q: CSS 변수와 Tailwind를 같이 쓰면 왜 좋아?

A: CSS 변수는 **런타임 변경**이 가능하고, Tailwind는 **클래스 기반 스타일링**으로 개발 속도가 빠릅니다. 둘을 연동하면 양쪽 장점을 모두 가져갈 수 있어요.

### Q: 런타임 변경이 왜 중요해?

A: 다크 모드를 생각해보세요. 사용자가 버튼을 누르면 즉시 테마가 바뀌어야 합니다. CSS 변수는 `.dark` 클래스만 추가하면 모든 색상이 바뀌지만, 하드코딩된 값은 페이지를 새로고침해야 합니다.

### Q: 그럼 모든 값을 CSS 변수로 만들어야 해?

A: 아니요. **런타임에 바뀔 가능성이 있는 값**만 CSS 변수로 만들면 됩니다. 색상, 폰트 크기(접근성), 트랜지션(모션 감소 설정) 등이 해당됩니다.

### Q: tailwind.config.js에서 직접 값을 정의하면 안 돼?

A: 가능하지만, 그러면 값이 두 군데(tailwind.config.js, index.css)에 분산됩니다. 다크 모드를 위해 index.css에도 색상을 정의해야 하니까요. CSS 변수로 통일하면 한 곳에서 관리할 수 있습니다.

### Q: 8px 그리드를 쓰면 7px나 9px는 절대 못 쓰는 거야?

A: 쓸 수 있습니다. `p-[7px]` 같은 임의 값(arbitrary value)을 쓰면 됩니다. 하지만 이건 **예외적인 경우**에만 사용해야 합니다. 너무 많이 쓰면 그리드 시스템의 의미가 없어지니까요.

---

## 오늘의 코드 하이라이트

```css
/* index.css - CSS 변수 정의 */
:root {
  /* 색상은 HSL로 정의 - 다크모드에서 값만 바꾸면 됨 */
  --primary: 25 90% 48%;

  /* 폰트 크기 */
  --font-size-xs-sm: 0.625rem;  /* 10px - 보조 텍스트용 */

  /* 트랜지션 - 빠른 조회 패턴에 맞게 단축 */
  --duration-fast: 100ms;
  --duration-normal: 200ms;
}

.dark {
  /* 다크모드에서는 이 값만 바뀜 */
  --primary: 25 90% 55%;
}
```

```javascript
// tailwind.config.js - CSS 변수 참조
fontSize: {
  "xs-sm": ["var(--font-size-xs-sm)", {
    lineHeight: "var(--line-height-xs-sm)",
    fontWeight: "var(--font-weight-regular)"
  }],
},
transitionDuration: {
  fast: "var(--duration-fast)",
  normal: "var(--duration-normal)",
},
```

```tsx
// 컴포넌트에서 사용
<span className="text-xs-sm">보조 텍스트</span>
<button className="transition-all duration-fast">빠른 버튼</button>
```

---

## 더 깊이 알고 싶다면

- [CSS Custom Properties - MDN](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties)
- [Design Tokens - Salesforce](https://www.lightningdesignsystem.com/design-tokens/)
- [Tailwind CSS Configuration](https://tailwindcss.com/docs/configuration)
- [Material Design 8dp Grid](https://m2.material.io/design/layout/spacing-methods.html)

---

## 한 줄 정리

> 디자인 토큰은 CSS 변수로 정의하고, Tailwind가 참조하게 하면 **런타임 테마 변경과 일관된 스타일 관리**가 가능하다.
