# 프로필 설정 화면 UI TDD 계획

## 개요

- **PRD**: `007-profile-settings-ui.md`
- **Issue**: #64
- **작성일**: 2026-01-26
- **유형**: FE (React + TypeScript)

## 유스케이스 분석

### 기능명: 프로필 설정 화면 UI

**핵심 유스케이스**:
- [x] UC-1: 프로필 설정 화면 진입 (헤더 드롭다운 → /settings)
- [x] UC-2: 프로필 정보 수정 (이름, 생일, 생일유형)
- [ ] UC-3: 회원 탈퇴 (#66에서 처리)

**입력/출력 정의**:
- 입력: `ModifyUserRequest` (name, birthday?, birthdayType?)
- 출력: `FindUserResponse` (id, email, name, profileUrl, birthday, birthdayType, ...)

**예외 케이스**:
- [x] 이름 비어있음 → 클라이언트 validation 에러
- [x] 생일만 입력, 유형 미선택 → validation 에러
- [x] API 호출 실패 → 에러 토스트

---

## 테스트 케이스 목록

### 1. HomePage - 설정 드롭다운 메뉴

**파일**: `fe/src/pages/__tests__/HomePage.test.tsx` (기존 파일 수정)

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `renders_settings_icon_in_header` | 헤더에 설정(톱니바퀴) 아이콘 렌더링 |
| P0 | `opens_dropdown_menu_when_settings_icon_clicked` | 설정 아이콘 클릭 시 드롭다운 메뉴 표시 |
| P0 | `shows_profile_settings_and_logout_in_dropdown` | 드롭다운에 "프로필 설정", "로그아웃" 항목 표시 |
| P0 | `navigates_to_settings_when_profile_settings_clicked` | "프로필 설정" 클릭 시 /settings로 이동 |
| P1 | `closes_dropdown_when_clicking_outside` | 외부 클릭 시 드롭다운 닫힘 |
| P1 | `dropdown_is_keyboard_accessible` | Tab/Enter로 드롭다운 조작 가능 |

---

### 2. SettingsPage - 컴포넌트 테스트

**파일**: `fe/src/pages/__tests__/SettingsPage.test.tsx` (신규)

#### 렌더링 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `renders_page_title` | "프로필 설정" 제목 렌더링 |
| P0 | `renders_back_button` | 뒤로가기 버튼 렌더링 |
| P0 | `renders_name_input_with_label` | 이름 입력 필드 + 레이블 렌더링 |
| P0 | `renders_birthday_input_with_label` | 생일 입력 필드 + 레이블 렌더링 |
| P0 | `renders_birthday_type_select` | 생일 유형 선택 (양력/음력) 렌더링 |
| P0 | `renders_save_button` | 저장 버튼 렌더링 |
| P1 | `renders_danger_zone_section` | 위험 영역 섹션 렌더링 |
| P1 | `renders_withdraw_button_in_danger_zone` | 회원 탈퇴 버튼 렌더링 (disabled 상태, #66 대기) |

#### 데이터 로딩 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `displays_loading_state_while_fetching_user` | 사용자 정보 로딩 중 로딩 상태 표시 |
| P0 | `populates_form_with_current_user_data` | 현재 사용자 정보로 폼 초기값 설정 |
| P0 | `displays_name_from_user_data` | 사용자 이름 표시 |
| P1 | `displays_birthday_from_user_data` | 사용자 생일 표시 (있는 경우) |
| P1 | `displays_birthday_type_from_user_data` | 사용자 생일 유형 표시 |
| P1 | `handles_user_without_birthday` | 생일 없는 사용자 처리 |

#### 폼 상호작용 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `updates_name_input_on_change` | 이름 입력 시 상태 업데이트 |
| P0 | `updates_birthday_input_on_change` | 생일 입력 시 상태 업데이트 |
| P0 | `updates_birthday_type_on_select` | 생일 유형 선택 시 상태 업데이트 |
| P1 | `clears_birthday_type_when_birthday_cleared` | 생일 삭제 시 생일 유형도 초기화 |

#### Validation 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `shows_error_when_name_is_empty` | 이름 비어있을 때 에러 메시지 표시 |
| P0 | `disables_save_button_when_name_is_empty` | 이름 비어있을 때 저장 버튼 비활성화 |
| P1 | `shows_error_when_birthday_without_type` | 생일만 입력하고 유형 미선택 시 에러 |
| P1 | `allows_submit_without_birthday` | 생일 없이 제출 가능 |

#### 제출 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `calls_modify_user_api_on_submit` | 저장 클릭 시 API 호출 |
| P0 | `shows_success_toast_on_successful_submit` | 성공 시 "프로필이 수정되었습니다" 토스트 |
| P0 | `stays_on_page_after_successful_submit` | 성공 후 페이지 유지 |
| P1 | `shows_error_toast_on_api_failure` | API 실패 시 에러 토스트 |
| P1 | `shows_loading_state_during_submit` | 제출 중 로딩 상태 표시 |
| P1 | `disables_form_during_submit` | 제출 중 폼 비활성화 |

#### 네비게이션 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `navigates_back_when_back_button_clicked` | 뒤로가기 버튼 클릭 시 이전 페이지로 |
| P1 | `shows_confirmation_when_leaving_with_unsaved_changes` | 변경사항 있을 때 페이지 이탈 확인 (선택) |

#### 접근성 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P1 | `all_inputs_have_associated_labels` | 모든 입력 필드에 label 연결 (1.3.1) |
| P1 | `error_messages_are_associated_with_inputs` | 에러 메시지가 입력 필드와 연결 (3.3.1) |
| P1 | `form_is_keyboard_navigable` | Tab으로 모든 요소 접근 가능 (2.1.1) |
| P2 | `focus_is_visible_on_all_interactive_elements` | 포커스 시각적 구분 (2.4.7) |

---

### 3. useUserQueries - Hook 테스트

**파일**: `fe/src/hooks/queries/__tests__/useUserQueries.test.ts` (신규)

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `useCurrentUser_returns_user_data` | 현재 사용자 정보 반환 |
| P0 | `useCurrentUser_returns_loading_state` | 로딩 상태 반환 |
| P0 | `useCurrentUser_returns_error_on_failure` | 에러 발생 시 에러 상태 반환 |
| P0 | `useModifyUser_calls_api_with_correct_params` | 올바른 파라미터로 API 호출 |
| P0 | `useModifyUser_invalidates_user_query_on_success` | 성공 시 사용자 쿼리 무효화 |
| P1 | `useModifyUser_returns_error_on_failure` | 실패 시 에러 반환 |

---

### 4. UserService - API 서비스 테스트

**파일**: `fe/src/api/services/__tests__/user.test.ts` (신규)

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `modifyUser_sends_patch_request` | PATCH 요청 전송 |
| P0 | `modifyUser_sends_correct_payload` | 올바른 payload 전송 |
| P0 | `modifyUser_returns_updated_user` | 수정된 사용자 정보 반환 |
| P1 | `modifyUser_throws_on_network_error` | 네트워크 에러 시 예외 발생 |
| P1 | `modifyUser_throws_on_validation_error` | 400 응답 시 예외 발생 |

---

### 5. App.tsx - 라우팅 테스트

**파일**: `fe/src/__tests__/App.test.tsx` (기존 파일 수정)

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `renders_settings_page_on_settings_route` | /settings 경로에서 SettingsPage 렌더링 |
| P1 | `settings_route_requires_authentication` | /settings는 인증 필요 (ProtectedRoute) |

---

## 테스트 우선순위 요약

| 우선순위 | 개수 | 설명 |
|---------|-----|------|
| P0 (필수) | 28 | 핵심 기능, 반드시 통과해야 함 |
| P1 (중요) | 20 | 예외 처리, 접근성, 엣지 케이스 |
| P2 (권장) | 1 | 추가 개선 사항 |
| **합계** | **49** | |

---

## 다음 단계

1. [ ] `fe/src/types/user.ts` - `ModifyUserRequest`, `BirthdayType` 타입 추가
2. [ ] `fe/src/api/services/user.ts` - `modifyUser()` 메서드 추가
3. [ ] `fe/src/hooks/queries/useUserQueries.ts` - 훅 생성
4. [ ] `fe/src/pages/SettingsPage.tsx` - 페이지 컴포넌트 생성
5. [ ] `fe/src/pages/HomePage.tsx` - 설정 드롭다운 메뉴 추가
6. [ ] `fe/src/App.tsx` - `/settings` 라우트 추가

---

## 테스트 환경

- **테스트 프레임워크**: Vitest
- **컴포넌트 테스트**: React Testing Library
- **API 모킹**: MSW (Mock Service Worker)
- **쿼리 테스트**: @tanstack/react-query testing utilities
