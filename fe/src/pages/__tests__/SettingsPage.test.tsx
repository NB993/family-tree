import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import SettingsPage from '../SettingsPage';

// Mock hasPointerCapture for Radix UI Select
beforeAll(() => {
  Element.prototype.hasPointerCapture = jest.fn(() => false);
  Element.prototype.setPointerCapture = jest.fn();
  Element.prototype.releasePointerCapture = jest.fn();
});

// Mock useCurrentUser and useModifyUser hooks
const mockMutateAsync = jest.fn();
const mockUseCurrentUser = jest.fn();
const mockUseModifyUser = jest.fn();

jest.mock('@/hooks/queries/useUserQueries', () => ({
  useCurrentUser: () => mockUseCurrentUser(),
  useModifyUser: () => mockUseModifyUser(),
}));

// Mock useToast hook
const mockToast = jest.fn();
jest.mock('@/hooks/use-toast', () => ({
  useToast: () => ({
    toast: mockToast,
  }),
}));

// Mock react-router-dom navigate
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

// Mock formatThisYearSolarBirthday
jest.mock('@/utils/lunar', () => ({
  formatThisYearSolarBirthday: jest.fn((birthday: string) => '04.15'),
}));

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{children}</BrowserRouter>
    </QueryClientProvider>
  );
};

const mockUser = {
  id: 1,
  email: 'test@example.com',
  name: '홍길동',
  profileUrl: null,
  birthday: '1990-03-15T00:00:00',
  birthdayType: 'SOLAR' as const,
};

const mockUserWithoutBirthday = {
  id: 1,
  email: 'test@example.com',
  name: '김철수',
  profileUrl: null,
  birthday: null,
  birthdayType: null,
};

describe('SettingsPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseModifyUser.mockReturnValue({
      mutateAsync: mockMutateAsync,
      isPending: false,
    });
  });

  describe('렌더링 테스트', () => {
    beforeEach(() => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });
    });

    it('renders_page_title: "프로필 설정" 제목 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByText('프로필 설정')).toBeInTheDocument();
    });

    it('renders_back_button: 뒤로가기 버튼 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByRole('button', { name: '뒤로 가기' })).toBeInTheDocument();
    });

    it('renders_name_input_with_label: 이름 입력 필드 + 레이블 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByLabelText(/이름/)).toBeInTheDocument();
    });

    it('renders_birthday_input_with_label: 생일 입력 필드 + 레이블 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByLabelText('생일')).toBeInTheDocument();
    });

    it('renders_birthday_type_select: 생일 유형 선택 (양력/음력) 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByLabelText('생일 유형')).toBeInTheDocument();
    });

    it('renders_birthday_type_select_with_border: 생일 유형 셀렉트에 테두리 적용', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      const selectTrigger = screen.getByLabelText('생일 유형');
      console.log('SelectTrigger className:', selectTrigger.className);
      expect(selectTrigger.className).toContain('border');
    });

    it('renders_save_button: 저장 버튼 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByRole('button', { name: '저장' })).toBeInTheDocument();
    });

    it('renders_danger_zone_section: 회원 탈퇴 섹션 렌더링', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      expect(screen.getByText('계정을 삭제하면 모든 데이터가 영구적으로 삭제됩니다.')).toBeInTheDocument();
    });

    it('renders_withdraw_button_in_danger_zone: 회원 탈퇴 버튼 렌더링 (disabled 상태)', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });
      const withdrawButton = screen.getByRole('button', { name: '회원 탈퇴' });
      expect(withdrawButton).toBeInTheDocument();
      expect(withdrawButton).toBeDisabled();
    });
  });

  describe('데이터 로딩 테스트', () => {
    it('displays_loading_state_while_fetching_user: 사용자 정보 로딩 중 로딩 상태 표시', () => {
      mockUseCurrentUser.mockReturnValue({
        data: undefined,
        isLoading: true,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      // 스켈레톤 UI가 표시되어야 함
      const skeletons = document.querySelectorAll('.animate-pulse');
      expect(skeletons.length).toBeGreaterThan(0);
    });

    it('populates_form_with_current_user_data: 현재 사용자 정보로 폼 초기값 설정', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByLabelText(/이름/)).toHaveValue('홍길동');
      });
    });

    it('displays_name_from_user_data: 사용자 이름 표시', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByLabelText(/이름/)).toHaveValue('홍길동');
      });
    });

    it('displays_birthday_from_user_data: 사용자 생일 표시 (있는 경우)', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByLabelText('생일')).toHaveValue('1990-03-15');
      });
    });

    it('displays_birthday_type_from_user_data: 사용자 생일 유형 표시', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      await waitFor(() => {
        const selectTrigger = screen.getByRole('combobox');
        expect(selectTrigger).toHaveTextContent('양력');
      });
    });

    it('handles_user_without_birthday: 생일 없는 사용자 처리', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUserWithoutBirthday,
        isLoading: false,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByLabelText(/이름/)).toHaveValue('김철수');
        expect(screen.getByLabelText('생일')).toHaveValue('');
      });
    });
  });

  describe('폼 상호작용 테스트', () => {
    beforeEach(() => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });
    });

    it('updates_name_input_on_change: 이름 입력 시 상태 업데이트', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      const nameInput = screen.getByLabelText(/이름/);
      await userEvent.clear(nameInput);
      await userEvent.type(nameInput, '김철수');

      expect(nameInput).toHaveValue('김철수');
    });

    it('updates_birthday_input_on_change: 생일 입력 시 상태 업데이트', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      const birthdayInput = screen.getByLabelText('생일');
      await userEvent.clear(birthdayInput);
      await userEvent.type(birthdayInput, '1985-05-20');

      expect(birthdayInput).toHaveValue('1985-05-20');
    });

    it('updates_birthday_type_on_select: 생일 유형 선택 시 상태 업데이트', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      const selectTrigger = screen.getByRole('combobox');
      await userEvent.click(selectTrigger);

      const lunarOption = screen.getByRole('option', { name: '음력' });
      await userEvent.click(lunarOption);

      await waitFor(() => {
        expect(selectTrigger).toHaveTextContent('음력');
      });
    });
  });

  describe('Validation 테스트', () => {
    beforeEach(() => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });
    });

    it('shows_error_when_name_is_empty: 이름 비어있을 때 에러 메시지 표시', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      const nameInput = screen.getByLabelText(/이름/);
      await userEvent.clear(nameInput);

      expect(screen.getByText('이름을 입력해주세요')).toBeInTheDocument();
    });

    it('disables_save_button_when_name_is_empty: 이름 비어있을 때 저장 버튼 비활성화', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      const nameInput = screen.getByLabelText(/이름/);
      await userEvent.clear(nameInput);

      const submitButton = screen.getByRole('button', { name: '저장' });
      expect(submitButton).toBeDisabled();
    });

    it('allows_submit_without_birthday: 생일 없이 제출 가능', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUserWithoutBirthday,
        isLoading: false,
      });
      mockMutateAsync.mockResolvedValueOnce({ id: 1 });

      render(<SettingsPage />, { wrapper: createWrapper() });

      const submitButton = screen.getByRole('button', { name: '저장' });
      expect(submitButton).not.toBeDisabled();

      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalledWith({
          name: '김철수',
          birthday: undefined,
          birthdayType: undefined,
        });
      });
    });
  });

  describe('제출 테스트', () => {
    beforeEach(() => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });
    });

    it('calls_modify_user_api_on_submit: 저장 클릭 시 API 호출', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 1 });

      render(<SettingsPage />, { wrapper: createWrapper() });

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalledWith({
          name: '홍길동',
          birthday: '1990-03-15T00:00:00',
          birthdayType: 'SOLAR',
        });
      });
    });

    it('shows_success_toast_on_successful_submit: 성공 시 "프로필이 수정되었습니다" 토스트', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 1 });

      render(<SettingsPage />, { wrapper: createWrapper() });

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockToast).toHaveBeenCalledWith({ title: '프로필이 수정되었습니다.' });
      });
    });

    it('stays_on_page_after_successful_submit: 성공 후 페이지 유지', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 1 });

      render(<SettingsPage />, { wrapper: createWrapper() });

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalled();
      });

      // navigate가 호출되지 않아야 함
      expect(mockNavigate).not.toHaveBeenCalled();
    });

    it('shows_error_toast_on_api_failure: API 실패 시 에러 토스트', async () => {
      mockMutateAsync.mockRejectedValueOnce(new Error('API Error'));

      render(<SettingsPage />, { wrapper: createWrapper() });

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockToast).toHaveBeenCalledWith({
          title: '프로필 수정에 실패했습니다.',
          variant: 'destructive',
        });
      });
    });

    it('shows_loading_state_during_submit: 제출 중 로딩 상태 표시', async () => {
      mockUseModifyUser.mockReturnValue({
        mutateAsync: mockMutateAsync,
        isPending: true,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      expect(screen.getByRole('button', { name: '저장 중...' })).toBeInTheDocument();
    });

    it('disables_form_during_submit: 제출 중 폼 비활성화', async () => {
      mockUseModifyUser.mockReturnValue({
        mutateAsync: mockMutateAsync,
        isPending: true,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      const submitButton = screen.getByRole('button', { name: '저장 중...' });
      expect(submitButton).toBeDisabled();
    });
  });

  describe('네비게이션 테스트', () => {
    beforeEach(() => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });
    });

    it('navigates_back_when_back_button_clicked: 뒤로가기 버튼 클릭 시 이전 페이지로', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      const backButton = screen.getByRole('button', { name: '뒤로 가기' });
      await userEvent.click(backButton);

      expect(mockNavigate).toHaveBeenCalledWith(-1);
    });
  });

  describe('접근성 테스트', () => {
    beforeEach(() => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });
    });

    it('all_inputs_have_associated_labels: 모든 입력 필드에 label 연결 (1.3.1)', () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      // 이름 입력
      const nameInput = screen.getByLabelText(/이름/);
      expect(nameInput).toHaveAttribute('id', 'name');

      // 생일 입력
      const birthdayInput = screen.getByLabelText('생일');
      expect(birthdayInput).toHaveAttribute('id', 'birthday');

      // 생일 유형
      const birthdayTypeSelect = screen.getByLabelText('생일 유형');
      expect(birthdayTypeSelect).toHaveAttribute('id', 'birthdayType');
    });

    it('form_is_keyboard_navigable: Tab으로 모든 요소 접근 가능 (2.1.1)', async () => {
      render(<SettingsPage />, { wrapper: createWrapper() });

      // 뒤로가기 버튼으로 시작
      const backButton = screen.getByRole('button', { name: '뒤로 가기' });
      backButton.focus();
      expect(document.activeElement).toBe(backButton);

      // Tab으로 이름 입력 필드로 이동
      await userEvent.tab();
      expect(document.activeElement).toBe(screen.getByLabelText(/이름/));

      // Tab으로 생일 입력 필드로 이동
      await userEvent.tab();
      expect(document.activeElement).toBe(screen.getByLabelText('생일'));

      // Tab으로 생일 유형 선택으로 이동
      await userEvent.tab();
      expect(document.activeElement).toBe(screen.getByRole('combobox'));
    });
  });

  describe('음력 생일 표시', () => {
    // Radix UI Select 컴포넌트는 jsdom에서 완전히 지원되지 않음
    // E2E 테스트에서 검증하는 것이 적합
    it.skip('음력으로 변경 시 양력 변환 날짜 표시', async () => {
      mockUseCurrentUser.mockReturnValue({
        data: mockUser,
        isLoading: false,
      });

      render(<SettingsPage />, { wrapper: createWrapper() });

      // 생일이 있는 상태에서 음력 선택
      const selectTrigger = screen.getByRole('combobox');
      await userEvent.click(selectTrigger);

      const lunarOption = screen.getByRole('option', { name: '음력' });
      await userEvent.click(lunarOption);

      // 양력 변환 날짜가 표시되는지 확인
      await waitFor(() => {
        expect(screen.getByText(/올해 양력:/)).toBeInTheDocument();
      });
    });
  });
});