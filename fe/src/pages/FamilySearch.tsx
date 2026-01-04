import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePublicFamilies } from '../hooks/queries/useFamilyQueries';
import { JoinFamilyButton } from '../components/family/FamilyJoinRequest';
import { PublicFamilyResponse } from '../api/services/familyService';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import { Search, ArrowLeft, Users, Plus, SearchX } from 'lucide-react';

export const FamilySearchPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [debouncedKeyword, setDebouncedKeyword] = useState('');

  React.useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedKeyword(searchKeyword);
    }, 500);

    return () => clearTimeout(timer);
  }, [searchKeyword]);

  const {
    data: familiesData,
    isLoading,
    error,
    refetch
  } = usePublicFamilies({
    keyword: debouncedKeyword || undefined,
    size: 20,
  });

  const handleJoinSuccess = () => {
    refetch();
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  return (
    <div className="min-h-screen bg-background p-5">
      {/* Header */}
      <header className="flex items-center gap-3 mb-6">
        {isAuthenticated && (
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate('/home')}
            className="rounded-xl"
          >
            <ArrowLeft className="h-5 w-5" />
          </Button>
        )}
        <div>
          <h1 className="text-xl font-bold text-foreground">그룹 찾기</h1>
          <p className="text-sm text-muted-foreground">공개된 그룹을 검색해보세요</p>
        </div>
      </header>

      {/* Guest Notice */}
      {!isAuthenticated && (
        <div className="bento-item mb-5 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center flex-shrink-0">
            <span className="text-lg">👋</span>
          </div>
          <div className="flex-1">
            <p className="text-sm text-foreground">게스트로 둘러보는 중</p>
            <button
              onClick={() => navigate('/login')}
              className="text-sm text-primary font-medium"
            >
              로그인하면 더 많은 기능을 사용할 수 있어요
            </button>
          </div>
        </div>
      )}

      {/* Search */}
      <div className="relative mb-5">
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="그룹 이름으로 검색..."
          className="pl-11 h-12 rounded-2xl bg-secondary border-0"
        />
      </div>

      {/* Loading */}
      {isLoading && (
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="bento-item">
              <Skeleton className="h-5 w-2/3 mb-3 rounded-lg" />
              <Skeleton className="h-4 w-1/2 mb-2 rounded-lg" />
              <Skeleton className="h-4 w-1/3 mb-4 rounded-lg" />
              <Skeleton className="h-11 w-full rounded-xl" />
            </div>
          ))}
        </div>
      )}

      {/* Error */}
      {error && (
        <div className="bento-item text-center py-8">
          <div className="w-14 h-14 rounded-2xl bg-red-100 flex items-center justify-center mx-auto mb-4">
            <SearchX className="w-7 h-7 text-red-500" />
          </div>
          <p className="text-foreground font-medium mb-1">오류가 발생했습니다</p>
          <p className="text-sm text-muted-foreground mb-4">{error.message}</p>
          <Button variant="outline" onClick={() => refetch()}>
            다시 시도
          </Button>
        </div>
      )}

      {/* Results */}
      {!isLoading && !error && familiesData && (
        <div className="space-y-4">
          {familiesData.content.length === 0 ? (
            <div className="bento-item text-center py-12">
              <div className="w-16 h-16 rounded-2xl bg-secondary flex items-center justify-center mx-auto mb-4">
                <Search className="w-8 h-8 text-muted-foreground" />
              </div>
              <h3 className="font-semibold text-foreground mb-1">
                {searchKeyword ? '검색 결과가 없습니다' : '공개된 그룹이 없습니다'}
              </h3>
              <p className="text-sm text-muted-foreground mb-4">
                {searchKeyword
                  ? '다른 키워드로 검색해보세요'
                  : '아직 공개된 그룹이 없습니다'}
              </p>
              {isAuthenticated && (
                <Button onClick={() => navigate('/families/create')}>
                  <Plus className="w-4 h-4" />
                  첫 번째 공개 그룹 만들기
                </Button>
              )}
            </div>
          ) : (
            <>
              <p className="text-sm text-muted-foreground">
                {searchKeyword && `"${searchKeyword}" 검색 결과 `}
                총 {familiesData.totalElements}개 그룹
              </p>

              {familiesData.content.map((family: PublicFamilyResponse) => (
                <div key={family.id} className="bento-item">
                  <h3 className="font-semibold text-foreground mb-2">
                    {family.name}
                  </h3>

                  {family.description && (
                    <p className="text-sm text-muted-foreground mb-3 line-clamp-2">
                      {family.description}
                    </p>
                  )}

                  <div className="flex items-center gap-3 text-sm text-muted-foreground mb-4">
                    <span className="flex items-center gap-1">
                      <Users className="w-4 h-4" />
                      {family.memberCount}명
                    </span>
                    <span className="text-muted-foreground/60">·</span>
                    <span>{formatDate(family.createdAt)} 생성</span>
                  </div>

                  <JoinFamilyButton
                    familyId={family.id}
                    familyName={family.name}
                    onSuccess={handleJoinSuccess}
                  />
                </div>
              ))}

              {familiesData.hasNext && (
                <div className="text-center py-4">
                  <p className="text-sm text-muted-foreground">
                    더 많은 그룹이 있습니다. 검색어를 구체화해보세요.
                  </p>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
};
