import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { useFamilyMembers, useFamilyDetail } from '../hooks/queries/useFamilyQueries';
import { FamilyMemberWithRelationship } from '../api/services/familyService';
import { ArrowLeft, Users, Plus, ChevronRight, Settings } from 'lucide-react';

const FamilyMembersPage: React.FC = () => {
  const { familyId } = useParams<{ familyId: string }>();
  const familyIdNumber = familyId ? parseInt(familyId, 10) : undefined;
  const navigate = useNavigate();
  const { data: familyData, isLoading: familyLoading } = useFamilyDetail(familyIdNumber!);
  const { data: membersData, isLoading: membersLoading, isError } = useFamilyMembers(familyIdNumber!);

  const isLoading = familyLoading || membersLoading;
  const members = membersData || [];

  if (!familyId) {
    return (
      <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
        <h2 className="text-sm font-medium text-foreground">그룹 ID가 없습니다</h2>
        <p className="text-[10px] text-muted-foreground mt-0.5">올바른 페이지로 이동해주세요</p>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="app-shell flex items-center justify-center">
        <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
        <h2 className="text-sm font-medium text-foreground">오류가 발생했습니다</h2>
        <p className="text-[10px] text-muted-foreground mt-0.5 mb-3">멤버 정보를 불러올 수 없습니다</p>
        <Button size="sm" onClick={() => window.location.reload()}>다시 시도</Button>
      </div>
    );
  }

  const sortedMembers = [...members].sort((a, b) => a.memberName.localeCompare(b.memberName));

  return (
    <div className="app-shell flex flex-col">
      {/* Header */}
      <header className="flex items-center justify-between px-3 py-2 border-b border-border">
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
            <ArrowLeft className="h-3.5 w-3.5" strokeWidth={1.5} />
          </Button>
          <div>
            <h1 className="text-sm font-medium text-foreground">{familyData?.name || '그룹'}</h1>
            <span className="text-[10px] text-muted-foreground">{members.length}명</span>
          </div>
        </div>
        <div className="flex gap-1">
          <Button variant="ghost" size="icon">
            <Settings className="h-3.5 w-3.5" strokeWidth={1.5} />
          </Button>
          <Button size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={1.5} /> 추가
          </Button>
        </div>
      </header>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {members.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full text-center px-4">
            <Users className="h-8 w-8 text-muted-foreground/50 mb-2" strokeWidth={1} />
            <h3 className="text-xs font-medium text-foreground">아직 멤버가 없습니다</h3>
            <p className="text-[10px] text-muted-foreground mt-0.5 mb-3">첫 번째 멤버를 추가해보세요</p>
            <Button size="sm">
              <Plus className="h-3.5 w-3.5" strokeWidth={1.5} /> 멤버 추가
            </Button>
          </div>
        ) : (
          <div className="divide-y divide-border">
            {sortedMembers.map((member: FamilyMemberWithRelationship) => (
              <div
                key={member.memberId}
                className="flex items-center gap-2 px-3 py-2 hover:bg-secondary/50 cursor-pointer"
                onClick={() => navigate(`/families/${familyId}/members/${member.memberId}`)}
              >
                <div className="w-7 h-7 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                  <span className="text-[10px] font-medium text-primary">
                    {member.memberName.charAt(0)}
                  </span>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-xs font-medium text-foreground truncate">{member.memberName}</p>
                  <p className="text-[10px] text-muted-foreground truncate">
                    {member.relationshipGuideMessage || '-'}
                  </p>
                </div>
                <ChevronRight className="w-3.5 h-3.5 text-muted-foreground flex-shrink-0" strokeWidth={1.5} />
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Footer Stats */}
      {members.length > 0 && (
        <div className="px-3 py-2 border-t border-border bg-secondary/30">
          <div className="flex justify-around text-center">
            <div>
              <div className="text-sm font-semibold text-primary">{members.length}</div>
              <div className="text-[10px] text-muted-foreground">총 멤버</div>
            </div>
            <div>
              <div className="text-sm font-semibold text-primary">
                {members.filter(m => m.member.status === 'ACTIVE').length}
              </div>
              <div className="text-[10px] text-muted-foreground">활성</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FamilyMembersPage;
