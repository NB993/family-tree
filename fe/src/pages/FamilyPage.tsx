import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useFamilyDetail, useFamilyMembers, useFamilyAnnouncements } from '../hooks/queries/useFamilyQueries';
import { useAuth } from '../contexts/AuthContext';
import { ArrowLeft, Users, ChevronRight, Settings } from 'lucide-react';

const FamilyPage: React.FC = () => {
  const { familyId } = useParams<{ familyId: string }>();
  const familyIdNumber = familyId ? parseInt(familyId, 10) : undefined;
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const { data: familyData, isLoading: familyLoading } = useFamilyDetail(familyIdNumber!);
  const { data: membersData, isLoading: membersLoading } = useFamilyMembers(familyIdNumber!);
  const { data: announcements, isLoading: announcementsLoading } = useFamilyAnnouncements(familyIdNumber!);

  const isLoading = familyLoading || membersLoading || announcementsLoading;
  const members = membersData || [];

  if (!familyId) {
    return (
      <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
        <h2 className="text-sm font-medium text-foreground">그룹 ID가 없습니다</h2>
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
            <div className="flex items-center gap-1">
              <span className="text-[10px] text-muted-foreground">{members.length}명</span>
              {familyData?.isPublic ? (
                <Badge variant="success" className="text-[9px] px-1 py-0">공개</Badge>
              ) : (
                <Badge variant="secondary" className="text-[9px] px-1 py-0">비공개</Badge>
              )}
            </div>
          </div>
        </div>
        {isAuthenticated ? (
          <Button variant="ghost" size="icon">
            <Settings className="h-3.5 w-3.5" strokeWidth={1.25} />
          </Button>
        ) : (
          <Button variant="outline" size="sm" onClick={() => navigate('/login')}>로그인</Button>
        )}
      </header>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {/* Description */}
        {familyData?.description && (
          <div className="px-3 py-2 border-b border-border">
            <p className="text-xs text-muted-foreground">{familyData.description}</p>
          </div>
        )}

        {/* Members Section */}
        <div className="border-b border-border">
          <div className="flex items-center justify-between px-3 py-2 bg-secondary/30">
            <span className="text-xs font-medium text-foreground">멤버</span>
            <Link to={`/families/${familyId}/members`}>
              <Button variant="ghost" size="sm" className="h-6 text-[10px]">
                전체 <ChevronRight className="w-3 h-3" strokeWidth={1.5} />
              </Button>
            </Link>
          </div>
          {members.length > 0 ? (
            <div className="divide-y divide-border">
              {members.slice(0, 3).map((m) => (
                <div key={m.memberId} className="flex items-center gap-2 px-3 py-2">
                  <div className="w-6 h-6 rounded-full bg-primary/10 flex items-center justify-center">
                    <span className="text-[9px] font-medium text-primary">{m.memberName.charAt(0)}</span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-xs font-medium text-foreground truncate">{m.memberName}</p>
                    <p className="text-[10px] text-muted-foreground truncate">{m.relationshipGuideMessage}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="px-3 py-4 text-center">
              <Users className="w-6 h-6 text-muted-foreground/50 mx-auto mb-1" strokeWidth={1.5} />
              <p className="text-[10px] text-muted-foreground">멤버가 없습니다</p>
            </div>
          )}
        </div>

        {/* Announcements Section */}
        <div>
          <div className="flex items-center justify-between px-3 py-2 bg-secondary/30">
            <span className="text-xs font-medium text-foreground">공지사항</span>
          </div>
          {announcements && announcements.length > 0 ? (
            <div className="divide-y divide-border">
              {announcements.slice(0, 3).map((a) => (
                <div key={a.id} className="px-3 py-2">
                  <div className="flex items-center gap-1 mb-0.5">
                    <span className="text-xs font-medium text-foreground truncate">{a.title}</span>
                    {a.isImportant && <Badge variant="destructive" className="text-[9px] px-1 py-0">중요</Badge>}
                  </div>
                  <p className="text-[10px] text-muted-foreground line-clamp-1">{a.content}</p>
                </div>
              ))}
            </div>
          ) : (
            <div className="px-3 py-4 text-center">
              <p className="text-[10px] text-muted-foreground">공지사항이 없습니다</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default FamilyPage;
