import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePublicFamilies } from '../hooks/queries/useFamilyQueries';
import { JoinFamilyButton } from '../components/family/FamilyJoinRequest';
import { PublicFamilyResponse } from '../api/services/familyService';
import { useAuth } from '../contexts/AuthContext';

export const FamilySearchPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [debouncedKeyword, setDebouncedKeyword] = useState('');

  // 디바운스 처리
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
    // 성공 후 페이지를 새로고침하거나 특정 동작 수행
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
    <div className="min-h-screen bg-gradient-to-br from-orange-50 to-amber-50">
      <div className="w-full max-w-sm mx-auto px-4 py-6">
        {/* 헤더 */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            {isAuthenticated && (
              <button
                onClick={() => navigate('/home')}
                className="flex items-center justify-center w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors"
              >
                <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
              </button>
            )}
            <h1 className="text-2xl font-bold text-gray-900">
              그룹 찾기
            </h1>
          </div>
          <p className="text-gray-600">
            공개된 그룹을 검색하고 참여 요청을 보내보세요
          </p>
          
          {/* 게스트 상태 표시 */}
          {!isAuthenticated && (
            <div className="mt-4 p-3 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-700">
                👋 게스트로 둥러보고 있습니다. 
                <button 
                  onClick={() => navigate('/login')}
                  className="ml-1 underline font-medium hover:text-blue-800"
                >
                  로그인
                </button>
                하면 더 많은 기능을 사용할 수 있습니다.
              </p>
            </div>
          )}
        </div>

        {/* 검색창 */}
        <div className="mb-6">
          <div className="relative">
            <input
              type="text"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              placeholder="그룹 이름으로 검색..."
              className="w-full px-4 py-3 pl-12 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent"
            />
            <div className="absolute left-4 top-1/2 transform -translate-y-1/2">
              <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
        </div>

        {/* 로딩 상태 */}
        {isLoading && (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="family-card animate-pulse">
                <div className="h-4 bg-gray-200 rounded w-2/3 mb-3"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2 mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-1/3 mb-4"></div>
                <div className="h-10 bg-gray-200 rounded"></div>
              </div>
            ))}
          </div>
        )}

        {/* 에러 상태 */}
        {error && (
          <div className="family-card bg-red-50 border-red-200">
            <div className="text-center py-8">
              <p className="text-red-600 mb-2">그룹 목록을 불러오는 중 오류가 발생했습니다.</p>
              <p className="text-sm text-red-500 mb-4">{error.message}</p>
              <button
                onClick={() => refetch()}
                className="family-button-secondary"
              >
                다시 시도
              </button>
            </div>
          </div>
        )}

        {/* 검색 결과 */}
        {!isLoading && !error && familiesData && (
          <div className="space-y-4">
            {familiesData.content.length === 0 ? (
              <div className="family-card">
                <div className="text-center py-12">
                  <div className="text-5xl mb-4">🔍</div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">
                    {searchKeyword ? '검색 결과가 없습니다' : '공개된 그룹이 없습니다'}
                  </h3>
                  <p className="text-gray-500">
                    {searchKeyword
                      ? '다른 키워드로 검색해보세요'
                      : '아직 공개된 그룹이 없습니다.'
                    }
                  </p>
                  {isAuthenticated && (
                    <button
                      onClick={() => navigate('/families/create')}
                      className="mt-4 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
                    >
                      첫 번째 공개 그룹 만들기
                    </button>
                  )}
                </div>
              </div>
            ) : (
              <>
                {/* 검색 결과 헤더 */}
                <div className="flex items-center justify-between mb-4">
                  <p className="text-sm text-gray-600">
                    {searchKeyword && `"${searchKeyword}" 검색 결과 `}
                    총 {familiesData.totalElements}개 그룹
                  </p>
                  {familiesData.hasNext && (
                    <p className="text-xs text-gray-500">
                      {familiesData.content.length}개 표시 중
                    </p>
                  )}
                </div>

                {/* 가족 목록 */}
                {familiesData.content.map((family: PublicFamilyResponse) => (
                  <div key={family.id} className="family-card">
                    <div className="mb-4">
                      <h3 className="text-lg font-semibold text-gray-900 mb-2">
                        {family.name}
                      </h3>
                      
                      {family.description && (
                        <p className="text-gray-600 mb-3 text-sm leading-relaxed">
                          {family.description}
                        </p>
                      )}

                      <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                        <span className="flex items-center gap-1">
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                          </svg>
                          {family.memberCount}명
                        </span>
                        <span>
                          {formatDate(family.createdAt)} 생성
                        </span>
                      </div>
                    </div>

                    <JoinFamilyButton
                      familyId={family.id}
                      familyName={family.name}
                      onSuccess={handleJoinSuccess}
                    />
                  </div>
                ))}

                {/* 더 보기 표시 */}
                {familiesData.hasNext && (
                  <div className="text-center py-6">
                    <p className="text-sm text-gray-500">
                      더 많은 그룹이 있습니다. 검색어를 구체화해보세요.
                    </p>
                  </div>
                )}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};