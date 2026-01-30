import React, { useState, useMemo, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMyFamilies, useFamilyMembers } from '../hooks/queries/useFamilyQueries';
import { FamilyMemberWithRelationship } from '../api/services/familyService';
import { Search, Plus, UserPlus, LogOut, ChevronLeft, ChevronRight, Settings, User } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import CreateFamilyMemberModal from '@/components/family/CreateFamilyMemberModal';
import { MemberDetailSheet } from '@/components/family/MemberDetailSheet';
import { TagBadge } from '@/components/family/TagBadge';
import { TagFilter } from '@/components/family/TagFilter';
import { getKoreanAge, getWesternAge } from '../utils/age';
import { formatBirthday, formatThisYearSolarBirthday } from '../utils/lunar';

// 멤버 카드 스켈레톤 컴포넌트 (2줄 레이아웃)
const MemberCardSkeleton: React.FC = () => (
  <div className="flex items-start gap-2 px-3 py-2">
    <Skeleton className="w-7 h-7 rounded-full flex-shrink-0 mt-0.5" />
    <div className="flex-1 min-w-0">
      <div className="flex items-center gap-1.5">
        <Skeleton className="h-4 w-20" />
        <Skeleton className="h-3 w-8" />
      </div>
      <div className="flex items-center gap-1 mt-1">
        <Skeleton className="h-4 w-12 rounded-full" />
        <Skeleton className="h-4 w-14 rounded-full" />
      </div>
    </div>
    <Skeleton className="h-3 w-20 flex-shrink-0 mt-0.5" />
    <Skeleton className="w-4 h-4 flex-shrink-0 mt-0.5" />
  </div>
);

const HomePage: React.FC = () => {
  const { data: familiesData, isLoading: familiesLoading } = useMyFamilies();
  const navigate = useNavigate();
  const { userInfo, logout } = useAuth();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFamilyId, setSelectedFamilyId] = useState<number | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);

  // 멤버 상세 시트 상태 - ID만 저장하고 membersData에서 파생
  const [selectedMemberId, setSelectedMemberId] = useState<number | null>(null);
  const [isSheetOpen, setIsSheetOpen] = useState(false);

  // 나이 표시 모드: 'korean' (한국나이) 또는 'western' (만나이)
  const [ageDisplayMode, setAgeDisplayMode] = useState<'korean' | 'western'>('korean');

  const { data: membersData, isLoading: membersLoading } = useFamilyMembers(selectedFamilyId || 0);

  useEffect(() => {
    if (familiesData && familiesData.length > 0 && !selectedFamilyId) {
      setSelectedFamilyId(familiesData[0].id);
    }
  }, [familiesData, selectedFamilyId]);

  // 초기 로딩 상태: 가족 정보 로딩 중이거나, 가족이 있는데 selectedFamilyId가 아직 설정되지 않았거나, 멤버 로딩 중
  const isInitialLoading = familiesLoading ||
    (familiesData && familiesData.length > 0 && !selectedFamilyId) ||
    (selectedFamilyId && membersLoading);

  // React Query 캐시에서 선택된 멤버 파생 (캐시 무효화 시 자동 업데이트)
  const selectedMember = useMemo(() => {
    if (!selectedMemberId || !membersData) return null;
    return membersData.find((m: FamilyMemberWithRelationship) => m.memberId === selectedMemberId) || null;
  }, [selectedMemberId, membersData]);

  const filteredMembers = useMemo(() => {
    if (!membersData) return [];

    let result = membersData;

    // 1. 검색어 필터링
    if (searchTerm) {
      result = result.filter((member: FamilyMemberWithRelationship) =>
        member.memberName.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // 2. 태그 필터링 (OR 조건)
    if (selectedTagIds.length > 0) {
      result = result.filter((member: FamilyMemberWithRelationship) =>
        member.tags?.some((tag) => selectedTagIds.includes(tag.id))
      );
    }

    return result;
  }, [membersData, searchTerm, selectedTagIds]);

  const families = familiesData || [];
  const hasData = families.length > 0 && membersData && membersData.length > 0;

  // 나이 토글 핸들러
  const handleAgeToggle = (e: React.MouseEvent) => {
    e.stopPropagation();
    setAgeDisplayMode(prev => prev === 'korean' ? 'western' : 'korean');
  };

  // 나이 표시 문자열 생성
  const getAgeDisplay = (birthday: string): string => {
    if (ageDisplayMode === 'korean') {
      return `(${getKoreanAge(birthday)})`;
    }
    return `(만 ${getWesternAge(birthday)})`;
  };

  // 멤버 클릭 핸들러
  const handleMemberClick = (member: FamilyMemberWithRelationship) => {
    setSelectedMemberId(member.memberId);
    setIsSheetOpen(true);
  };

  return (
    <div className="app-shell h-screen flex flex-col overflow-hidden">
      {/* Header */}
      <header className="shrink-0 flex items-center justify-between px-3 py-1.5 border-b border-border bg-card">
        <div className="flex items-center gap-2">
          <div>
            <h1 className="text-sm font-medium text-foreground">오래오래</h1>
            {userInfo && (
              <p className="text-[10px] text-muted-foreground">{userInfo.name}</p>
            )}
          </div>
          {membersData && (
            <span className="text-[10px] text-muted-foreground bg-secondary px-1.5 py-0.5 rounded">
              {filteredMembers.length}명
            </span>
          )}
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="[&_svg]:size-5" aria-label="설정 메뉴">
              <Settings strokeWidth={1.25} />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => navigate('/settings')} className="py-1 text-xs">
              <User className="h-3 w-3 mr-2" strokeWidth={1.5} />
              프로필 설정
            </DropdownMenuItem>
            <DropdownMenuItem onClick={logout} className="py-1 text-xs">
              <LogOut className="h-3 w-3 mr-2" strokeWidth={1.5} />
              로그아웃
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </header>

      {/* Search + Filter */}
      <div className="shrink-0 px-3 py-1.5 border-b border-border bg-secondary/30">
        <div className="flex items-center gap-2">
          {/* Tag Filter */}
          {selectedFamilyId && (
            <TagFilter
              familyId={selectedFamilyId}
              selectedTagIds={selectedTagIds}
              onSelectionChange={setSelectedTagIds}
              filteredCount={filteredMembers.length}
            />
          )}
          {/* Search Input */}
          <div className="relative flex-1">
            <Search className="absolute left-2 top-1/2 -translate-y-1/2 h-3 w-3 text-muted-foreground" strokeWidth={1.5} />
            <Input
              type="text"
              placeholder="검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-6"
            />
          </div>
        </div>
      </div>

      {/* Member List */}
      <div className="flex-1 overflow-auto">
        {isInitialLoading ? (
          <div className="divide-y divide-border">
            {[...Array(5)].map((_, i) => (
              <MemberCardSkeleton key={i} />
            ))}
          </div>
        ) : !hasData ? (
          <div className="flex flex-col items-center justify-center h-full text-center px-4">
            <UserPlus className="h-8 w-8 text-muted-foreground/50 mb-2" strokeWidth={1.5} />
            <p className="text-xs text-muted-foreground">등록된 멤버가 없습니다</p>
            <p className="text-[10px] text-muted-foreground mt-0.5">초대링크를 전달하거나 직접 등록하세요</p>
          </div>
        ) : (
          <div className="divide-y divide-border">
            {filteredMembers.map((member: FamilyMemberWithRelationship) => (
              <div
                key={member.memberId}
                className="flex items-start gap-2 px-3 py-2 hover:bg-secondary/50 cursor-pointer"
                onClick={() => handleMemberClick(member)}
              >
                {/* 아바타 */}
                <div className="w-7 h-7 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <span className="text-[10px] font-medium text-primary">
                    {member.memberName.charAt(0)}
                  </span>
                </div>

                {/* 콘텐츠 영역 */}
                <div className="flex-1 min-w-0">
                  {/* 1줄: 이름 + 생일 + 화살표 */}
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1.5">
                      <span className="text-sm font-medium text-foreground">
                        {member.memberName}
                      </span>
                      {member.memberBirthday && (
                        <span
                          className="text-xs text-muted-foreground cursor-pointer hover:text-foreground transition-colors"
                          onClick={handleAgeToggle}
                        >
                          {getAgeDisplay(member.memberBirthday)}
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-1 flex-shrink-0">
                      {member.memberBirthday && (
                        <span className="text-xs text-muted-foreground flex items-center gap-1">
                          {formatBirthday(member.memberBirthday, member.memberBirthdayType ?? null)}
                          {member.memberBirthdayType === 'LUNAR' && (
                            <span className="text-muted-foreground/70">
                              (올해 양력: {formatThisYearSolarBirthday(member.memberBirthday)})
                            </span>
                          )}
                        </span>
                      )}
                      <ChevronRight className="w-4 h-4 text-muted-foreground" strokeWidth={1.5} />
                    </div>
                  </div>
                  {/* 2줄: 태그 - 가로 스크롤 with 좌우 버튼 */}
                  {member.tags && member.tags.length > 0 && (
                    <div className="relative group/tags mt-1">
                      {/* 왼쪽 스크롤 버튼 */}
                      <button
                        className="absolute left-0 top-1/2 -translate-y-1/2 z-10
                                   h-5 w-5 bg-background/80 rounded-full shadow-sm border border-border/50
                                   flex items-center justify-center
                                   opacity-0 group-hover/tags:opacity-100 transition-opacity
                                   hover:bg-background"
                        onClick={(e) => {
                          e.stopPropagation();
                          const container = e.currentTarget.nextElementSibling as HTMLElement;
                          container?.scrollBy({ left: -100, behavior: 'smooth' });
                        }}
                      >
                        <ChevronLeft className="h-3 w-3 text-muted-foreground" strokeWidth={1.5} />
                      </button>

                      {/* 태그 스크롤 영역 */}
                      <div className="flex items-center gap-1 flex-nowrap overflow-x-auto scrollbar-hide">
                        {member.tags.map((tag) => (
                          <TagBadge
                            key={tag.id}
                            name={tag.name}
                            color={tag.color}
                            size="sm"
                            className="flex-shrink-0 whitespace-nowrap"
                          />
                        ))}
                      </div>

                      {/* 오른쪽 스크롤 버튼 */}
                      <button
                        className="absolute right-0 top-1/2 -translate-y-1/2 z-10
                                   h-5 w-5 bg-background/80 rounded-full shadow-sm border border-border/50
                                   flex items-center justify-center
                                   opacity-0 group-hover/tags:opacity-100 transition-opacity
                                   hover:bg-background"
                        onClick={(e) => {
                          e.stopPropagation();
                          const container = e.currentTarget.previousElementSibling as HTMLElement;
                          container?.scrollBy({ left: 100, behavior: 'smooth' });
                        }}
                      >
                        <ChevronRight className="h-3 w-3 text-muted-foreground" strokeWidth={1.5} />
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Footer Actions */}
      <div className="shrink-0 px-3 py-2 border-t border-border bg-card flex gap-1.5">
        <Button onClick={() => navigate('/invites/create')} className="flex-1">
          <Plus className="h-3 w-3" strokeWidth={1.5} />
          초대 링크
        </Button>
        <Button variant="outline" onClick={() => setIsCreateModalOpen(true)} className="flex-1">
          <UserPlus className="h-3 w-3" strokeWidth={1.5} />
          등록
        </Button>
      </div>

      {selectedFamilyId && (
        <CreateFamilyMemberModal
          open={isCreateModalOpen}
          onOpenChange={setIsCreateModalOpen}
          familyId={selectedFamilyId}
        />
      )}

      {/* 멤버 상세 시트 */}
      {selectedFamilyId && (
        <MemberDetailSheet
          familyId={selectedFamilyId}
          member={selectedMember}
          open={isSheetOpen}
          onOpenChange={setIsSheetOpen}
        />
      )}
    </div>
  );
};

export default HomePage;
