import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import CreateFamilyMemberModal from '../CreateFamilyMemberModal';

// Mock useCreateFamilyMember hook
const mockMutateAsync = jest.fn();
jest.mock('@/hooks/queries/useFamilyQueries', () => ({
  useCreateFamilyMember: () => ({
    mutateAsync: mockMutateAsync,
    isPending: false,
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

describe('CreateFamilyMemberModal', () => {
  const defaultProps = {
    open: true,
    onOpenChange: jest.fn(),
    familyId: 1,
    onSuccess: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('모달이 열리면 제목이 표시된다', () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    expect(screen.getByText('가족 구성원 등록')).toBeInTheDocument();
  });

  it('이름 입력 필드가 표시된다', () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    expect(screen.getByLabelText(/이름/)).toBeInTheDocument();
    expect(screen.getByPlaceholderText('이름을 입력하세요')).toBeInTheDocument();
  });

  it('관계 선택 필드가 표시된다', () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    expect(screen.getByText('관계')).toBeInTheDocument();
    expect(screen.getByText('관계를 선택하세요')).toBeInTheDocument();
  });

  it('생년월일 입력 필드가 표시된다', () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    expect(screen.getByText('생년월일')).toBeInTheDocument();
  });

  it('등록 및 취소 버튼이 표시된다', () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    expect(screen.getByRole('button', { name: '등록' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '취소' })).toBeInTheDocument();
  });

  it('이름이 비어있으면 등록 버튼이 비활성화된다', () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    const submitButton = screen.getByRole('button', { name: '등록' });
    expect(submitButton).toBeDisabled();
  });

  it('이름을 입력하면 등록 버튼이 활성화된다', async () => {
    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    const nameInput = screen.getByPlaceholderText('이름을 입력하세요');
    await userEvent.type(nameInput, '홍길동');

    const submitButton = screen.getByRole('button', { name: '등록' });
    expect(submitButton).not.toBeDisabled();
  });

  it('취소 버튼 클릭 시 onOpenChange(false)가 호출된다', async () => {
    const onOpenChange = jest.fn();
    render(
      <CreateFamilyMemberModal {...defaultProps} onOpenChange={onOpenChange} />,
      { wrapper: createWrapper() }
    );

    const cancelButton = screen.getByRole('button', { name: '취소' });
    await userEvent.click(cancelButton);

    expect(onOpenChange).toHaveBeenCalledWith(false);
  });

  it('폼 제출 시 mutateAsync가 호출된다', async () => {
    mockMutateAsync.mockResolvedValue({});

    render(<CreateFamilyMemberModal {...defaultProps} />, {
      wrapper: createWrapper(),
    });

    const nameInput = screen.getByPlaceholderText('이름을 입력하세요');
    await userEvent.type(nameInput, '홍길동');

    const submitButton = screen.getByRole('button', { name: '등록' });
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(mockMutateAsync).toHaveBeenCalledWith({
        familyId: 1,
        form: {
          name: '홍길동',
          birthday: undefined,
        },
      });
    });
  });

  it('폼 제출 성공 시 onSuccess가 호출된다', async () => {
    mockMutateAsync.mockResolvedValue({});
    const onSuccess = jest.fn();

    render(
      <CreateFamilyMemberModal {...defaultProps} onSuccess={onSuccess} />,
      { wrapper: createWrapper() }
    );

    const nameInput = screen.getByPlaceholderText('이름을 입력하세요');
    await userEvent.type(nameInput, '홍길동');

    const submitButton = screen.getByRole('button', { name: '등록' });
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(onSuccess).toHaveBeenCalled();
    });
  });

  it('모달이 닫히면 폼이 리셋된다', async () => {
    const { rerender } = render(
      <CreateFamilyMemberModal {...defaultProps} open={true} />,
      { wrapper: createWrapper() }
    );

    const nameInput = screen.getByPlaceholderText('이름을 입력하세요');
    await userEvent.type(nameInput, '홍길동');

    rerender(
      <QueryClientProvider
        client={
          new QueryClient({
            defaultOptions: { queries: { retry: false } },
          })
        }
      >
        <CreateFamilyMemberModal {...defaultProps} open={false} />
      </QueryClientProvider>
    );

    rerender(
      <QueryClientProvider
        client={
          new QueryClient({
            defaultOptions: { queries: { retry: false } },
          })
        }
      >
        <CreateFamilyMemberModal {...defaultProps} open={true} />
      </QueryClientProvider>
    );

    const newNameInput = screen.getByPlaceholderText('이름을 입력하세요');
    expect(newNameInput).toHaveValue('');
  });
});
