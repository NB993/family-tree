import React, { useState, useMemo, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMyFamilies, useFamilyMembers } from '../hooks/queries/useFamilyQueries';
import { FamilyMemberWithRelationship } from '../api/services/familyService';
import { Search, Plus, UserPlus, LogOut, ChevronRight, Phone } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import CreateFamilyMemberModal from '@/components/family/CreateFamilyMemberModal';

// 멤버 카드 스켈레톤 컴포넌트
const MemberCardSkeleton: React.FC = () => (
  <div className="flex items-center gap-2 px-3 py-1.5">
    <Skeleton className="w-6 h-6 rounded-full flex-shrink-0" />
    <div className="flex-1 min-w-0">
      <Skeleton className="h-3 w-20 mb-1" />
      <Skeleton className="h-2.5 w-16" />
    </div>
    <Skeleton className="w-3 h-3 flex-shrink-0" />
  </div>
);

const HomePage: React.FC = () => {
  const { data: familiesData, isLoading: familiesLoading } = useMyFamilies();
  const navigate = useNavigate();
  const { userInfo, logout } = useAuth();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFamilyId, setSelectedFamilyId] = useState<number | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

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

  const filteredMembers = useMemo(() => {
    if (!membersData || !searchTerm) return membersData || [];

    return membersData.filter((member: FamilyMemberWithRelationship) =>
      member.memberName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      member.memberPhoneNumber?.includes(searchTerm) ||
      member.phoneNumberDisplay?.includes(searchTerm)
    );
  }, [membersData, searchTerm]);

  const families = familiesData || [];
  const hasData = families.length > 0 && membersData && membersData.length > 0;

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
        <Button variant="ghost" size="icon" onClick={logout}>
          <LogOut className="h-3.5 w-3.5" strokeWidth={1.5} />
        </Button>
      </header>

      {/* Search */}
      <div className="shrink-0 px-3 py-1.5 border-b border-border bg-secondary/30">
        <div className="relative">
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
            <UserPlus className="h-8 w-8 text-muted-foreground/50 mb-2" strokeWidth={1} />
            <p className="text-xs text-muted-foreground">등록된 멤버가 없습니다</p>
            <p className="text-[10px] text-muted-foreground mt-0.5">초대링크를 전달하거나 직접 등록하세요</p>
          </div>
        ) : (
          <div className="divide-y divide-border">
            {filteredMembers.map((member: FamilyMemberWithRelationship) => (
              <div
                key={member.memberId}
                className="flex items-center gap-2 px-3 py-1.5 hover:bg-secondary/50 cursor-pointer"
                onClick={() => navigate(`/families/${selectedFamilyId}/members/${member.memberId}`)}
              >
                <div className="w-6 h-6 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <span className="text-[9px] font-medium text-primary">
                    {member.memberName.charAt(0)}
                  </span>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-xs font-medium text-foreground truncate">
                    {member.memberName}
                  </p>
                  <p className="text-[10px] text-muted-foreground truncate">
                    {member.phoneNumberDisplay || '-'}
                  </p>
                </div>
                {member.memberPhoneNumber && (
                  <a
                    href={`tel:${member.memberPhoneNumber}`}
                    className="w-5 h-5 rounded-md bg-green-50 flex items-center justify-center flex-shrink-0"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <Phone className="w-2.5 h-2.5 text-green-600" strokeWidth={1.5} />
                  </a>
                )}
                <ChevronRight className="w-3 h-3 text-muted-foreground flex-shrink-0" strokeWidth={1.5} />
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
    </div>
  );
};

export default HomePage;
