import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemberEditModal } from '../MemberEditModal';

// Mock useModifyMemberInfo hook
const mockMutateAsync = jest.fn();
jest.mock('@/hooks/queries/useFamilyQueries', () => ({
  useModifyMemberInfo: () => ({
    mutateAsync: mockMutateAsync,
    isPending: false,
  }),
}));

// Mock useToast hook
const mockToast = jest.fn();
jest.mock('@/hooks/use-toast', () => ({
  useToast: () => ({
    toast: mockToast,
  }),
}));

// Mock formatThisYearSolarBirthday
jest.mock('@/utils/lunar', () => ({
  formatThisYearSolarBirthday: jest.fn((birthday: string) => {
    // 간단한 mock 반환값
    return '04.15';
  }),
}));

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('MemberEditModal', () => {
  const defaultProps = {
    open: true,
    onOpenChange: jest.fn(),
    familyId: 1,
    memberId: 2,
    currentName: '홍길동',
    onSuccess: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('렌더링', () => {
    it('모달이 열리면 제목이 표시된다', () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByText('구성원 정보 수정')).toBeInTheDocument();
    });

    it('이름 입력 필드가 현재 이름으로 초기화된다', () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByLabelText(/이름/)).toHaveValue('홍길동');
    });

    it('생일 입력 필드가 표시된다', () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByLabelText('생일')).toBeInTheDocument();
    });

    it('생일 유형 선택 필드가 표시된다', () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByLabelText('생일 유형')).toBeInTheDocument();
    });

    it('저장 및 취소 버튼이 표시된다', () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByRole('button', { name: '저장' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: '취소' })).toBeInTheDocument();
    });

    it('현재 생일이 있으면 초기값으로 설정된다', () => {
      render(
        <MemberEditModal
          {...defaultProps}
          currentBirthday="1990-03-15T00:00:00"
          currentBirthdayType="SOLAR"
        />,
        { wrapper: createWrapper() }
      );

      const birthdayInput = screen.getByLabelText('생일');
      expect(birthdayInput).toHaveValue('1990-03-15');
    });

    it('음력 생일일 때 생일 유형이 음력으로 표시된다', () => {
      render(
        <MemberEditModal
          {...defaultProps}
          currentBirthday="1990-03-15T00:00:00"
          currentBirthdayType="LUNAR"
        />,
        { wrapper: createWrapper() }
      );

      // Select combobox에서 음력이 선택되어 있는지 확인
      const selectTrigger = screen.getByRole('combobox');
      expect(selectTrigger).toHaveTextContent('음력');
    });
  });

  describe('유효성 검사', () => {
    it('이름이 비어있으면 저장 버튼이 비활성화된다', async () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      const nameInput = screen.getByLabelText(/이름/);
      await userEvent.clear(nameInput);

      const submitButton = screen.getByRole('button', { name: '저장' });
      expect(submitButton).toBeDisabled();
    });

    it('이름이 있으면 저장 버튼이 활성화된다', () => {
      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      const submitButton = screen.getByRole('button', { name: '저장' });
      expect(submitButton).not.toBeDisabled();
    });
  });

  describe('버튼 인터랙션', () => {
    it('취소 버튼 클릭 시 onOpenChange(false)가 호출된다', async () => {
      const onOpenChange = jest.fn();
      render(
        <MemberEditModal {...defaultProps} onOpenChange={onOpenChange} />,
        { wrapper: createWrapper() }
      );

      const cancelButton = screen.getByRole('button', { name: '취소' });
      await userEvent.click(cancelButton);

      expect(onOpenChange).toHaveBeenCalledWith(false);
    });

    it('이름 변경 후 저장하면 mutateAsync가 호출된다', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 2 });

      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      const nameInput = screen.getByLabelText(/이름/);
      await userEvent.clear(nameInput);
      await userEvent.type(nameInput, '김철수');

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalledWith({
          familyId: 1,
          memberId: 2,
          request: {
            name: '김철수',
            birthday: undefined,
            birthdayType: undefined,
          },
        });
      });
    });

    it('생일과 함께 저장하면 생일 정보도 전송된다', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 2 });

      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      const birthdayInput = screen.getByLabelText('생일');
      await userEvent.type(birthdayInput, '1990-05-20');

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalledWith({
          familyId: 1,
          memberId: 2,
          request: {
            name: '홍길동',
            birthday: '1990-05-20T00:00:00',
            birthdayType: 'SOLAR',
          },
        });
      });
    });
  });

  describe('모달 상태', () => {
    it('모달이 닫혀있으면 내용이 표시되지 않는다', () => {
      render(<MemberEditModal {...defaultProps} open={false} />, {
        wrapper: createWrapper(),
      });

      expect(screen.queryByText('구성원 정보 수정')).not.toBeInTheDocument();
    });
  });

  describe('제출 및 토스트', () => {
    it('정보 수정 성공 시 성공 토스트가 표시된다', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 2 });
      const onSuccess = jest.fn();

      render(
        <MemberEditModal {...defaultProps} onSuccess={onSuccess} />,
        { wrapper: createWrapper() }
      );

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalled();
        expect(mockToast).toHaveBeenCalledWith({ title: '정보가 수정되었습니다.' });
        expect(onSuccess).toHaveBeenCalled();
      });
    });

    it('정보 수정 실패 시 에러 토스트가 표시된다', async () => {
      mockMutateAsync.mockRejectedValueOnce(new Error('API Error'));

      render(<MemberEditModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(mockMutateAsync).toHaveBeenCalled();
        expect(mockToast).toHaveBeenCalledWith({
          title: '정보 수정에 실패했습니다.',
          variant: 'destructive',
        });
      });
    });

    it('저장 성공 시 모달이 닫힌다', async () => {
      mockMutateAsync.mockResolvedValueOnce({ id: 2 });
      const onOpenChange = jest.fn();

      render(
        <MemberEditModal {...defaultProps} onOpenChange={onOpenChange} />,
        { wrapper: createWrapper() }
      );

      const submitButton = screen.getByRole('button', { name: '저장' });
      await userEvent.click(submitButton);

      await waitFor(() => {
        expect(onOpenChange).toHaveBeenCalledWith(false);
      });
    });
  });

  describe('폼 초기화', () => {
    it('모달이 열릴 때 현재 값으로 초기화된다', () => {
      const { rerender } = render(
        <MemberEditModal {...defaultProps} open={false} />,
        { wrapper: createWrapper() }
      );

      rerender(
        <QueryClientProvider
          client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}
        >
          <MemberEditModal
            {...defaultProps}
            open={true}
            currentName="김철수"
            currentBirthday="1985-01-01T00:00:00"
            currentBirthdayType="LUNAR"
          />
        </QueryClientProvider>
      );

      expect(screen.getByLabelText(/이름/)).toHaveValue('김철수');
      expect(screen.getByLabelText('생일')).toHaveValue('1985-01-01');
    });
  });
});
