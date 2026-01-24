import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SetRelationshipModal } from '../SetRelationshipModal';
import { FamilyMemberRelationshipType, FamilyMemberRelationshipLabels } from '@/types/family';

// Mock useModifyMemberRelationship hook
const mockMutateAsync = jest.fn();
jest.mock('@/hooks/queries/useFamilyQueries', () => ({
  useModifyMemberRelationship: () => ({
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

describe('SetRelationshipModal', () => {
  const defaultProps = {
    open: true,
    onOpenChange: jest.fn(),
    familyId: 1,
    memberId: 2,
    memberName: '홍길동',
    onSuccess: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('렌더링', () => {
    it('모달이 열리면 제목이 표시된다', () => {
      render(<SetRelationshipModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByText('홍길동님과의 관계 설정')).toBeInTheDocument();
    });

    it('관계 선택 필드가 표시된다', () => {
      render(<SetRelationshipModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      // Select combobox가 있고 placeholder가 표시되는지 확인
      const selectTrigger = screen.getByRole('combobox');
      expect(selectTrigger).toBeInTheDocument();
      expect(selectTrigger).toHaveTextContent('관계를 선택하세요');
    });

    it('설정 및 취소 버튼이 표시된다', () => {
      render(<SetRelationshipModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      expect(screen.getByRole('button', { name: '설정' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: '취소' })).toBeInTheDocument();
    });

    it('관계가 선택되지 않으면 설정 버튼이 비활성화된다', () => {
      render(<SetRelationshipModal {...defaultProps} />, {
        wrapper: createWrapper(),
      });

      const submitButton = screen.getByRole('button', { name: '설정' });
      expect(submitButton).toBeDisabled();
    });

    it('현재 관계가 있으면 초기값으로 설정된다', () => {
      render(
        <SetRelationshipModal
          {...defaultProps}
          currentRelationshipType={FamilyMemberRelationshipType.FATHER}
        />,
        { wrapper: createWrapper() }
      );

      // Select trigger에 라벨이 표시되는지 확인
      const selectTrigger = screen.getByRole('combobox');
      expect(selectTrigger).toHaveTextContent(FamilyMemberRelationshipLabels[FamilyMemberRelationshipType.FATHER]);
    });

    it('현재 CUSTOM 관계가 있으면 초기값으로 설정된다', () => {
      render(
        <SetRelationshipModal
          {...defaultProps}
          currentRelationshipType={FamilyMemberRelationshipType.CUSTOM}
          currentCustomRelationship="외할아버지"
        />,
        { wrapper: createWrapper() }
      );

      // Select trigger에 라벨이 표시되는지 확인
      const selectTrigger = screen.getByRole('combobox');
      expect(selectTrigger).toHaveTextContent(FamilyMemberRelationshipLabels[FamilyMemberRelationshipType.CUSTOM]);
      // 커스텀 관계 입력 필드에 값이 설정되어 있는지 확인
      expect(screen.getByDisplayValue('외할아버지')).toBeInTheDocument();
    });

    it('CUSTOM 관계일 때 글자 수 카운터가 표시된다', () => {
      render(
        <SetRelationshipModal
          {...defaultProps}
          currentRelationshipType={FamilyMemberRelationshipType.CUSTOM}
          currentCustomRelationship="외할아버지"
        />,
        { wrapper: createWrapper() }
      );

      expect(screen.getByText('5/50자')).toBeInTheDocument();
    });
  });

  describe('버튼 인터랙션', () => {
    it('취소 버튼 클릭 시 onOpenChange(false)가 호출된다', async () => {
      const onOpenChange = jest.fn();
      render(
        <SetRelationshipModal {...defaultProps} onOpenChange={onOpenChange} />,
        { wrapper: createWrapper() }
      );

      const cancelButton = screen.getByRole('button', { name: '취소' });
      await userEvent.click(cancelButton);

      expect(onOpenChange).toHaveBeenCalledWith(false);
    });

    it('현재 관계가 있을 때 설정 버튼이 활성화된다', () => {
      render(
        <SetRelationshipModal
          {...defaultProps}
          currentRelationshipType={FamilyMemberRelationshipType.FATHER}
        />,
        { wrapper: createWrapper() }
      );

      const submitButton = screen.getByRole('button', { name: '설정' });
      expect(submitButton).not.toBeDisabled();
    });

    it('CUSTOM 관계에서 customRelationship이 있으면 설정 버튼이 활성화된다', () => {
      render(
        <SetRelationshipModal
          {...defaultProps}
          currentRelationshipType={FamilyMemberRelationshipType.CUSTOM}
          currentCustomRelationship="외할아버지"
        />,
        { wrapper: createWrapper() }
      );

      const submitButton = screen.getByRole('button', { name: '설정' });
      expect(submitButton).not.toBeDisabled();
    });

    it('CUSTOM 관계에서 customRelationship이 비어있으면 설정 버튼이 비활성화된다', () => {
      render(
        <SetRelationshipModal
          {...defaultProps}
          currentRelationshipType={FamilyMemberRelationshipType.CUSTOM}
          currentCustomRelationship=""
        />,
        { wrapper: createWrapper() }
      );

      const submitButton = screen.getByRole('button', { name: '설정' });
      expect(submitButton).toBeDisabled();
    });
  });

  describe('모달 상태', () => {
    it('모달이 닫혀있으면 내용이 표시되지 않는다', () => {
      render(<SetRelationshipModal {...defaultProps} open={false} />, {
        wrapper: createWrapper(),
      });

      expect(screen.queryByText('홍길동님과의 관계 설정')).not.toBeInTheDocument();
    });
  });
});
