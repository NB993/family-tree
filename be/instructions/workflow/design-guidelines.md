# 디자이너 AI 가이드라인

## 문서 정보
- **목적**: 디자이너 AI가 기획자 AI의 기획서를 바탕으로 시각적 디자인을 완성할 때 준수해야 할 가이드라인
- **대상**: 디자이너 AI
- **관련 문서**: `planning-template.md`

---

## 1. 기본 원칙

### 1.1 역할과 책임
- **기획자 AI의 텍스트 와이어프레임을 정확히 해석하고 시각적으로 구현**
- **기능과 사용자 경험을 해치지 않는 선에서 시각적 완성도 향상**
- **일관된 디자인 시스템 적용으로 브랜드 정체성 유지**

### 1.2 디자인 우선순위
1. **기능성**: 사용자가 의도한 작업을 쉽게 완료할 수 있어야 함
2. **일관성**: 전체 서비스의 디자인 패턴과 조화
3. **접근성**: 모든 사용자가 사용할 수 있는 포용적 디자인
4. **미적 완성도**: 시각적으로 매력적이고 현대적인 디자인

---

## 2. 인터랙션 디자인 가이드라인

### 2.1 입력 필드 상호작용

#### 이메일 입력란
```
기본 상태:
- 테두리: #E5E7EB (연한 회색)
- 배경: #FFFFFF (흰색)
- 플레이스홀더: #9CA3AF (중간 회색)

포커스 상태:
- 테두리: #3B82F6 (파란색) 2px
- 그림자: 0 0 0 3px rgba(59, 130, 246, 0.1)
- 플레이스홀더 애니메이션: 위로 이동하여 라벨로 변환

입력 중:
- 실시간 이메일 형식 검증 표시
- 유효: 우측에 체크마크 아이콘 (녹색)
- 무효: 우측에 X 아이콘 (빨간색)

포커스 아웃:
- 서버 중복 체크 중: 로딩 스피너 표시
- 중복 발견: 테두리 빨간색 + 하단 에러 메시지

에러 상태:
- 테두리: #EF4444 (빨간색)
- 배경: rgba(239, 68, 68, 0.05)
- 에러 메시지: #EF4444, 12px, 입력란 하단 4px 간격
```

#### 비밀번호 입력란
```
기본/포커스 상태: 이메일 입력란과 동일

조건 안내:
- 입력란 하단에 조건 목록 표시
- 충족된 조건: 체크마크 + 녹색 텍스트
- 미충족 조건: X마크 + 회색 텍스트
- 실시간 업데이트

보기/숨기기 기능:
- 우측 끝에 눈 모양 아이콘 배치
- 기본: 닫힌 눈 (비밀번호 숨김)
- 클릭: 열린 눈 (비밀번호 표시)
- 호버 시: 배경색 변경으로 상호작용 표시
```

### 2.2 버튼 상태 디자인

#### 회원가입 버튼
```
비활성화 상태:
- 배경: #E5E7EB (연한 회색)
- 텍스트: #9CA3AF (중간 회색)
- 커서: not-allowed
- 호버/클릭 효과 없음

활성화 상태:
- 배경: #3B82F6 (브랜드 파란색)
- 텍스트: #FFFFFF (흰색)
- 호버: 배경색 어두워짐 (#2563EB)
- 클릭: 배경색 더 어두워짐 (#1D4ED8) + 살짝 아래로 이동

처리 중 상태:
- 배경: #3B82F6 (유지)
- 텍스트: "처리 중..." + 회전하는 스피너
- 클릭 불가능 상태

완료 상태:
- 배경: #10B981 (녹색)
- 텍스트: "완료" + 체크마크 아이콘
- 1.5초 후 자동으로 리디렉션
```

### 2.3 애니메이션 가이드라인
```
전환 효과:
- 일반적인 상태 변화: 200ms ease-in-out
- 호버 효과: 150ms ease-out
- 포커스 상태: 100ms ease-out
- 로딩 상태: 무한 반복, 1s linear

주의사항:
- 과도한 애니메이션은 피할 것
- 사용자의 모션 감소 설정(prefers-reduced-motion) 고려
- 성능에 영향을 주지 않는 선에서 구현
```

---

## 3. 반응형 디자인 가이드라인

### 3.1 브레이크포인트
```
Mobile First 접근법 사용:
- xs: 0px ~ 575px (모바일)
- sm: 576px ~ 767px (큰 모바일)
- md: 768px ~ 991px (태블릿)
- lg: 992px ~ 1199px (작은 데스크톱)
- xl: 1200px 이상 (데스크톱)
```

### 3.2 디바이스별 레이아웃

#### 모바일 (0px ~ 767px)
```
폼 레이아웃:
- 좌우 여백: 16px
- 입력 필드 높이: 48px (터치 최적화)
- 버튼 높이: 48px
- 텍스트 크기: 16px (iOS 확대 방지)

간격 조정:
- 요소 간 간격: 16px
- 섹션 간 간격: 32px
- 상하 패딩: 24px
```

#### 태블릿 (768px ~ 991px)
```
폼 레이아웃:
- 중앙 정렬: 최대 너비 500px
- 좌우 여백: 32px
- 입력 필드 높이: 44px
- 버튼 높이: 44px

간격 조정:
- 요소 간 간격: 20px
- 섹션 간 간격: 40px
- 상하 패딩: 40px
```

#### 데스크톱 (992px 이상)
```
폼 레이아웃:
- 중앙 정렬: 최대 너비 400px
- 입력 필드 높이: 40px
- 버튼 높이: 40px

간격 조정:
- 요소 간 간격: 24px
- 섹션 간 간격: 48px
- 상하 패딩: 64px

추가 기능:
- 호버 효과 활성화
- 키보드 네비게이션 강화
```

---

## 4. 접근성 가이드라인

### 4.1 색상 및 대비
```
최소 대비 비율 (WCAG AA 기준):
- 일반 텍스트: 4.5:1
- 큰 텍스트 (18px+ 또는 14px+ bold): 3:1
- UI 컴포넌트: 3:1

색상 의존성:
- 정보 전달 시 색상만 사용 금지
- 아이콘, 텍스트 등 추가 표시 요소 병행
- 색맹 사용자를 위한 패턴/텍스처 활용
```

### 4.2 키보드 네비게이션
```
탭 순서:
1. 이메일 입력란
2. 비밀번호 입력란
3. 비밀번호 보기/숨기기 버튼
4. 회원가입 버튼
5. 로그인 링크

포커스 인디케이터:
- 명확한 아웃라인 또는 그림자 제공
- 브라우저 기본 outline 제거 시 대체 스타일 필수
- 최소 2px 두께, 고대비 색상 사용
```

### 4.3 스크린 리더 지원
```
라벨링:
- 모든 input에 적절한 label 또는 aria-label 제공
- placeholder는 라벨 대체 불가

에러 메시지:
- aria-live="polite" 속성으로 실시간 알림
- aria-describedby로 관련 도움말 연결
- 오류 필드에 aria-invalid="true" 설정

상태 변화:
- 버튼 상태 변화 시 aria-label 업데이트
- 로딩 상태는 aria-live로 알림
- 성공/실패 결과 명확히 전달
```

---

## 5. 타이포그래피 가이드라인

### 5.1 폰트 시스템
```
Primary Font Stack:
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 
             'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 
             'Fira Sans', 'Droid Sans', 'Helvetica Neue', 
             sans-serif;

Fallback: Arial, sans-serif;
```

### 5.2 폰트 크기 및 가중치
```
페이지 제목: 
- 크기: 32px (모바일), 40px (데스크톱)
- 가중치: 700 (Bold)
- 줄 높이: 1.2

섹션 제목:
- 크기: 20px (모바일), 24px (데스크톱)  
- 가중치: 600 (SemiBold)
- 줄 높이: 1.3

본문 텍스트:
- 크기: 16px (모바일), 14px (데스크톱)
- 가중치: 400 (Regular)
- 줄 높이: 1.5

라벨 텍스트:
- 크기: 14px
- 가중치: 500 (Medium)
- 줄 높이: 1.4

캡션/도움말:
- 크기: 12px
- 가중치: 400 (Regular)
- 줄 높이: 1.4
```

---

## 6. 컬러 시스템

### 6.1 Primary Colors
```
브랜드 파란색:
- Primary: #3B82F6
- Hover: #2563EB  
- Active: #1D4ED8
- Light: #DBEAFE
- Dark: #1E40AF

중성 색상:
- Gray-50: #F9FAFB
- Gray-100: #F3F4F6
- Gray-200: #E5E7EB
- Gray-300: #D1D5DB
- Gray-400: #9CA3AF
- Gray-500: #6B7280
- Gray-600: #4B5563
- Gray-700: #374151
- Gray-800: #1F2937
- Gray-900: #111827
```

### 6.2 Semantic Colors
```
성공:
- Primary: #10B981
- Light: #D1FAE5
- Dark: #047857

경고:
- Primary: #F59E0B
- Light: #FEF3C7
- Dark: #D97706

오류:
- Primary: #EF4444
- Light: #FEE2E2
- Dark: #DC2626

정보:
- Primary: #3B82F6
- Light: #DBEAFE
- Dark: #1D4ED8
```

---

## 7. 컴포넌트 라이브러리

### 7.1 Input Component
```
기본 스타일:
.input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  font-size: 16px;
  transition: all 200ms ease-in-out;
}

.input:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input.error {
  border-color: #EF4444;
  background-color: rgba(239, 68, 68, 0.05);
}
```

### 7.2 Button Component
```
기본 스타일:
.button {
  width: 100%;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 150ms ease-out;
}

.button.primary {
  background-color: #3B82F6;
  color: #FFFFFF;
}

.button.primary:hover {
  background-color: #2563EB;
}

.button:disabled {
  background-color: #E5E7EB;
  color: #9CA3AF;
  cursor: not-allowed;
}
```

---

## 8. 품질 체크리스트

### 8.1 디자인 완성도 체크
- [ ] 기획자 AI의 와이어프레임 모든 요소 반영
- [ ] 브랜드 컬러 시스템 적용
- [ ] 일관된 간격과 정렬
- [ ] 모든 상태 (기본, 호버, 포커스, 에러) 디자인 완료
- [ ] 로딩 상태 및 피드백 제공

### 8.2 반응형 디자인 체크
- [ ] 모바일(320px~), 태블릿, 데스크톱 모든 사이즈 테스트
- [ ] 터치 인터페이스 최적화 (최소 44px 터치 영역)
- [ ] 가로/세로 모드 모두 대응
- [ ] 콘텐츠 오버플로우 처리

### 8.3 접근성 체크
- [ ] 색상 대비 비율 4.5:1 이상 확보
- [ ] 키보드 네비게이션 가능
- [ ] 스크린 리더 호환성
- [ ] aria 속성 적절히 사용
- [ ] 에러 메시지 명확하게 전달

### 8.4 사용성 체크
- [ ] 직관적인 인터페이스
- [ ] 명확한 피드백 제공
- [ ] 오류 방지 및 복구 기능
- [ ] 일관된 상호작용 패턴

---

## 9. 도구 및 리소스

### 9.1 권장 디자인 도구
- **Figma**: 협업 및 프로토타이핑
- **Adobe XD**: 디자인 시스템 구축
- **Sketch**: macOS 환경 디자인

### 9.2 접근성 검증 도구
- **WAVE**: 웹 접근성 평가
- **axe**: 브라우저 확장 프로그램
- **Colour Contrast Analyser**: 색상 대비 검사

### 9.3 참고 리소스
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Material Design Guidelines](https://material.io/design)
- [Apple Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
- [Nielsen Norman Group](https://www.nngroup.com/)

---

## 변경 이력
```
| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| v1.0.0 | 2025-05-26 | 초기 가이드라인 작성 | Claude AI |
```