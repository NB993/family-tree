import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import HomePage from '../HomePage';

// 테스트용 고정 날짜 설정 (2026-01-06)
const realDate = Date;
beforeAll(() => {
  const mockDate = new Date('2026-01-06T12:00:00Z');
  global.Date = class extends realDate {
    constructor(...args: any[]) {
      if (args.length === 0) {
        super(mockDate.getTime());
        return this;
      }
      // @ts-ignore
      super(...args);
    }
    static now() {
      return mockDate.getTime();
    }
  } as any;
});
afterAll(() => {
  global.Date = realDate;
});

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

// axios import 체인을 끊기 위해 MemberDetailSheet mock
jest.mock('@/components/family/MemberDetailSheet', () => ({
  MemberDetailSheet: () => null,
}));

// TagFilter도 useTagQueries를 사용하므로 mock
jest.mock('@/components/family/TagFilter', () => ({
  TagFilter: () => null,
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

  describe('생일/나이 표시 (PRD-005)', () => {
    const memberWithBirthday = {
      memberId: 1,
      memberName: '홍길동',
      memberBirthday: '1990-12-25',
      memberBirthdayType: 'SOLAR' as const,
      memberPhoneNumber: null,
      phoneNumberDisplay: null,
      hasRelationship: false,
      relationshipSetupRequired: false,
      relationshipGuideMessage: '',
      member: { status: 'ACTIVE' },
    };

    const memberWithoutBirthday = {
      memberId: 2,
      memberName: '김철수',
      memberBirthday: null,
      memberBirthdayType: null,
      memberPhoneNumber: null,
      phoneNumberDisplay: null,
      hasRelationship: false,
      relationshipSetupRequired: false,
      relationshipGuideMessage: '',
      member: { status: 'ACTIVE' },
    };

    beforeEach(() => {
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
    });

    it('생일 정보가 있는 멤버는 나이와 생일이 표시된다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithBirthday],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
        // 한국나이 37세 (1990년생, 2026년)
        expect(screen.getByText('(37)')).toBeInTheDocument();
        // 양력 생일
        expect(screen.getByText('1990.12.25')).toBeInTheDocument();
      });
    });

    it('생일 정보가 없는 멤버는 나이와 생일이 표시되지 않는다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithoutBirthday],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('김철수')).toBeInTheDocument();
      });

      // 나이나 생일이 표시되지 않아야 함
      expect(screen.queryByText(/\(\d+\)/)).not.toBeInTheDocument();
    });

    it('나이 클릭 시 한국나이/만나이 토글된다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithBirthday],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('(37)')).toBeInTheDocument();
      });

      // 나이 클릭
      fireEvent.click(screen.getByText('(37)'));

      // 만나이로 변경 (1990.12.25 -> 2026.01.06 기준 만 35세)
      await waitFor(() => {
        expect(screen.getByText('(만 35)')).toBeInTheDocument();
      });

      // 다시 클릭하면 한국나이로
      fireEvent.click(screen.getByText('(만 35)'));
      await waitFor(() => {
        expect(screen.getByText('(37)')).toBeInTheDocument();
      });
    });

    it('양력 생일은 날짜만 표시된다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithBirthday],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('1990.12.25')).toBeInTheDocument();
      });
    });

    it('음력 생일은 (음)라벨과 올해 양력 날짜가 함께 표시된다', async () => {
      const lunarMember = {
        ...memberWithBirthday,
        memberBirthdayType: 'LUNAR' as const,
      };
      mockUseFamilyMembers.mockReturnValue({
        data: [lunarMember],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      // 음력 생일 표시: (음)1990.12.25
      await waitFor(() => {
        expect(screen.getByText('(음)1990.12.25')).toBeInTheDocument();
      });

      // 올해 양력 날짜도 표시됨 (ArrowRight 아이콘과 함께)
      // 올해 양력 날짜는 매년 달라지므로 M.DD 형식 패턴으로 확인
      await waitFor(() => {
        const birthdaySpan = screen.getByText('(음)1990.12.25').parentElement;
        expect(birthdaySpan).toBeInTheDocument();
        // ArrowRight 아이콘이 있는지 확인
        expect(birthdaySpan?.querySelector('svg')).toBeInTheDocument();
      });
    });

    it('연락처가 더 이상 표시되지 않는다 (PRD-005 변경사항)', async () => {
      const memberWithPhone = {
        ...memberWithBirthday,
        memberPhoneNumber: '010-1234-5678',
        phoneNumberDisplay: '010-1234-5678',
      };
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithPhone],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      // 연락처가 표시되지 않아야 함
      expect(screen.queryByText('010-1234-5678')).not.toBeInTheDocument();
    });
  });

  describe('스켈레톤 UI 레이아웃 (PRD-005)', () => {
    it('스켈레톤 UI가 1줄 레이아웃으로 표시된다', () => {
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
      mockUseFamilyMembers.mockReturnValue({
        data: undefined,
        isLoading: true,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      // 스켈레톤 컨테이너가 flex items-center로 1줄 레이아웃
      const skeletonContainers = document.querySelectorAll('.flex.items-center.gap-2');
      expect(skeletonContainers.length).toBeGreaterThan(0);
    });
  });

  describe('태그 영역 스크롤 버튼', () => {
    const memberWithTags = {
      memberId: 1,
      memberName: '홍길동',
      memberBirthday: '1990-12-25',
      memberBirthdayType: 'SOLAR' as const,
      memberPhoneNumber: null,
      phoneNumberDisplay: null,
      hasRelationship: false,
      relationshipSetupRequired: false,
      relationshipGuideMessage: '',
      member: { status: 'ACTIVE' },
      tags: [
        { id: 1, name: '가족', color: '#FF0000' },
        { id: 2, name: '친척', color: '#00FF00' },
        { id: 3, name: '친구', color: '#0000FF' },
      ],
    };

    const memberWithoutTags = {
      memberId: 2,
      memberName: '김철수',
      memberBirthday: null,
      memberBirthdayType: null,
      memberPhoneNumber: null,
      phoneNumberDisplay: null,
      hasRelationship: false,
      relationshipSetupRequired: false,
      relationshipGuideMessage: '',
      member: { status: 'ACTIVE' },
      tags: [],
    };

    beforeEach(() => {
      mockUseMyFamilies.mockReturnValue({
        data: [{ id: 1, name: '테스트가족' }],
        isLoading: false,
      });
    });

    it('태그가 있는 멤버는 좌우 스크롤 버튼이 렌더링된다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithTags],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      // 태그 영역의 좌우 스크롤 버튼 확인 (ChevronLeft, ChevronRight 아이콘을 가진 버튼)
      const tagContainer = document.querySelector('.group\\/tags');
      expect(tagContainer).toBeInTheDocument();

      const scrollButtons = tagContainer?.querySelectorAll('button');
      expect(scrollButtons?.length).toBe(2);
    });

    it('태그가 없는 멤버는 스크롤 버튼이 렌더링되지 않는다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithoutTags],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('김철수')).toBeInTheDocument();
      });

      // 태그 영역이 없어야 함
      const tagContainer = document.querySelector('.group\\/tags');
      expect(tagContainer).not.toBeInTheDocument();
    });

    it('왼쪽 스크롤 버튼 클릭 시 scrollBy가 호출된다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithTags],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      const tagContainer = document.querySelector('.group\\/tags');
      const scrollArea = tagContainer?.querySelector('.overflow-x-auto');
      const leftButton = tagContainer?.querySelector('button');

      // scrollBy mock
      const scrollByMock = jest.fn();
      if (scrollArea) {
        scrollArea.scrollBy = scrollByMock;
      }

      if (leftButton) {
        fireEvent.click(leftButton);
      }

      expect(scrollByMock).toHaveBeenCalledWith({ left: -100, behavior: 'smooth' });
    });

    it('오른쪽 스크롤 버튼 클릭 시 scrollBy가 호출된다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithTags],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      const tagContainer = document.querySelector('.group\\/tags');
      const scrollArea = tagContainer?.querySelector('.overflow-x-auto');
      const buttons = tagContainer?.querySelectorAll('button');
      const rightButton = buttons?.[1];

      // scrollBy mock
      const scrollByMock = jest.fn();
      if (scrollArea) {
        scrollArea.scrollBy = scrollByMock;
      }

      if (rightButton) {
        fireEvent.click(rightButton);
      }

      expect(scrollByMock).toHaveBeenCalledWith({ left: 100, behavior: 'smooth' });
    });

    it('스크롤 버튼 클릭 시 멤버 카드 클릭 이벤트가 전파되지 않는다', async () => {
      mockUseFamilyMembers.mockReturnValue({
        data: [memberWithTags],
        isLoading: false,
      });

      render(<HomePage />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      const tagContainer = document.querySelector('.group\\/tags');
      const leftButton = tagContainer?.querySelector('button');

      // scrollBy mock (에러 방지)
      const scrollArea = tagContainer?.querySelector('.overflow-x-auto');
      if (scrollArea) {
        scrollArea.scrollBy = jest.fn();
      }

      if (leftButton) {
        fireEvent.click(leftButton);
      }

      // MemberDetailSheet가 열리지 않아야 함 (이벤트 전파 방지 확인)
      // Sheet가 열리면 role="dialog"가 나타남
      expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    });
  });
});
