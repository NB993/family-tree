import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import HomePage from '../HomePage';

// Mock hooks
const mockUseMyFamilies = jest.fn();
const mockUseFamilyMembers = jest.fn();

jest.mock('@/hooks/queries/useFamilyQueries', () => ({
  useMyFamilies: () => mockUseMyFamilies(),
  useFamilyMembers: () => mockUseFamilyMembers(),
  useCreateFamilyMember: () => ({
    mutateAsync: jest.fn(),
    isPending: false,
  }),
}));

jest.mock('@/contexts/AuthContext', () => ({
  useAuth: () => ({
    userInfo: { name: '테스트유저' },
    logout: jest.fn(),
  }),
}));

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{children}</BrowserRouter>
    </QueryClientProvider>
  );
};

describe('HomePage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('로딩 상태', () => {
    it('가족 정보 로딩 중일 때 스켈레톤 UI가 표시된다', () => {
      mockUseMyFamilies.mockReturnValue({
        data: undefined,
        isLoading: true,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: undefined,
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      // 스켈레톤 UI가 표시되어야 함 (animate-pulse 클래스를 가진 요소)
      const skeletons = document.querySelectorAll('.animate-pulse');
      expect(skeletons.length).toBeGreaterThan(0);
    });

    it('멤버 정보 로딩 중일 때 스켈레톤 UI가 표시된다', () => {
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: undefined,
        isLoading: true,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      const skeletons = document.querySelectorAll('.animate-pulse');
      expect(skeletons.length).toBeGreaterThan(0);
    });

    it('로딩 중일 때 "등록된 멤버가 없습니다" 메시지가 표시되지 않는다', () => {
      mockUseMyFamilies.mockReturnValue({
        data: undefined,
        isLoading: true,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: undefined,
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      expect(screen.queryByText('등록된 멤버가 없습니다')).not.toBeInTheDocument();
    });
  });

  describe('빈 상태', () => {
    it('멤버가 없을 때 빈 상태 메시지가 표시된다', () => {
      mockUseMyFamilies.mockReturnValue({
        data: [],
        isLoading: false,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: [],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      expect(screen.getByText('등록된 멤버가 없습니다')).toBeInTheDocument();
      expect(screen.getByText('초대링크를 전달하거나 직접 등록하세요')).toBeInTheDocument();
    });
  });

  describe('데이터 로드 완료', () => {
    it('멤버 목록이 정상적으로 표시된다', async () => {
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: [
          {
            memberId: 1,
            memberName: '홍길동',
            memberPhoneNumber: '010-1234-5678',
            phoneNumberDisplay: '010-1234-5678',
            member: { status: 'ACTIVE' },
          },
          {
            memberId: 2,
            memberName: '김철수',
            memberPhoneNumber: null,
            phoneNumberDisplay: null,
            member: { status: 'ACTIVE' },
          },
        ],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
        expect(screen.getByText('김철수')).toBeInTheDocument();
      });
    });

    it('데이터 로드 후 스켈레톤이 사라진다', async () => {
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: [
          {
            memberId: 1,
            memberName: '홍길동',
            memberPhoneNumber: null,
            phoneNumberDisplay: null,
            member: { status: 'ACTIVE' },
          },
        ],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      // 스켈레톤이 사라져야 함
      const skeletons = document.querySelectorAll('.animate-pulse');
      expect(skeletons.length).toBe(0);
    });
  });

  describe('깜빡임 방지', () => {
    it('가족 데이터 로드 후 멤버 로딩 중일 때 스켈레톤이 표시된다 (빈 화면 아님)', () => {
      // selectedFamilyId가 useEffect로 설정된 후 멤버 데이터 로딩 중인 상태
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
      // 멤버 데이터 로딩 중 (깜빡임이 발생하던 시점)
      mockUseFamilyMembers.mockReturnValue({
        data: undefined,
        isLoading: true,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      // 빈 메시지가 표시되지 않아야 함 (깜빡임 방지)
      expect(screen.queryByText('등록된 멤버가 없습니다')).not.toBeInTheDocument();

      // 스켈레톤이 표시되어야 함
      const skeletons = document.querySelectorAll('.animate-pulse');
      expect(skeletons.length).toBeGreaterThan(0);
    });
  });
});
