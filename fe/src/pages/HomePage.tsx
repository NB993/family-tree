import React, { useState, useMemo, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMyFamilies, useFamilyMembers } from '../hooks/queries/useFamilyQueries';
import { FamilyMemberWithRelationship } from '../api/services/familyService';
import { Search, Plus, UserPlus, LogOut, ChevronRight } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

const HomePage: React.FC = () => {
  const { data: familiesData } = useMyFamilies();
  const navigate = useNavigate();
  const { userInfo, logout } = useAuth();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFamilyId, setSelectedFamilyId] = useState<number | null>(null);

  const { data: membersData } = useFamilyMembers(selectedFamilyId || 0);

  useEffect(() => {
    if (familiesData && familiesData.length > 0 && !selectedFamilyId) {
      setSelectedFamilyId(familiesData[0].id);
    }
  }, [familiesData, selectedFamilyId]);

  const formatBirthDate = (dateString?: string): string => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${month}.${day}`;
  };

  const filteredMembers = useMemo(() => {
    if (!membersData || !searchTerm) return membersData || [];

    return membersData.filter((member: FamilyMemberWithRelationship) =>
      member.memberName.toLowerCase().includes(searchTerm.toLowerCase())
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
        {!hasData ? (
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
                    {member.memberAge ? `${member.memberAge}세` : ''} {formatBirthDate(member.memberBirthday)}
                  </p>
                </div>
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
        <Button variant="outline" onClick={() => navigate('/families')} className="flex-1">
          <UserPlus className="h-3 w-3" strokeWidth={1.5} />
          등록
        </Button>
      </div>
    </div>
  );
};

export default HomePage;
