import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card, CardContent } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { useMyFamilies } from '../hooks/queries/useFamilyQueries';
import { AuthService } from '../api/services/authService';

const HomePage: React.FC = () => {
  const { data: familiesData, isLoading, isError } = useMyFamilies();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const authService = AuthService.getInstance();
      
      // 백엔드 로그아웃 API 호출 (Refresh Token 무효화)
      await authService.logout();
      
      // localStorage에서 인증 정보 제거
      authService.clearAllAuthData();
      
      // 로그인 페이지로 이동
      navigate('/login');
    } catch (error) {
      console.error('로그아웃 중 오류 발생:', error);
      // 오류가 발생해도 로컬 데이터는 삭제하고 로그인 페이지로 이동
      AuthService.getInstance().clearAllAuthData();
      navigate('/login');
    }
  };

  // 사용자 정보 가져오기
  const userInfo = localStorage.getItem('userInfo');
  const user = userInfo ? JSON.parse(userInfo) : null;

  if (isLoading) {
    return (
      <div className="container">
        <div className="text-center py-20">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">가족 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="container">
        <div className="text-center py-20">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">오류가 발생했습니다</h2>
          <p className="text-gray-600 mb-6">가족 정보를 불러올 수 없습니다.</p>
          <Button onClick={() => window.location.reload()}>다시 시도</Button>
        </div>
      </div>
    );
  }

  const families = familiesData || [];

  return (
    <div className="container">
      <div className="py-8">
        {/* 헤더 영역 */}
        <div className="flex justify-between items-center mb-8">
          <div className="text-center flex-1">
            <h1 className="text-3xl font-bold text-gray-900 mb-4">
              🏠 Family Tree
            </h1>
            <p className="text-lg text-gray-600">
              가족의 이야기를 따뜻하게 기록하세요
            </p>
          </div>
          
          {/* 사용자 정보 및 로그아웃 */}
          <div className="flex flex-col items-end space-y-2 min-w-[140px]">
            {user && (
              <div className="text-right">
                <p className="text-sm text-gray-600">안녕하세요!</p>
                <p className="text-sm font-medium text-gray-900">{user.name}님</p>
              </div>
            )}
            <Button 
              variant="outline" 
              size="sm"
              onClick={handleLogout}
            >
              로그아웃
            </Button>
          </div>
        </div>

        {families.length === 0 ? (
          <Card className="text-center">
            <CardContent>
              <div className="py-12">
                <div className="text-6xl mb-4">👨‍👩‍👧‍👦</div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  아직 가족이 없습니다
                </h3>
                <p className="text-gray-600 mb-6">
                  새로운 가족을 만들거나 기존 가족에 참여해보세요
                </p>
                <div className="flex gap-4 justify-center flex-wrap">
                  <Link to="/families/create">
                    <Button variant="primary" size="lg">
                      새 가족 만들기
                    </Button>
                  </Link>
                  <Link to="/families/search">
                    <Button variant="outline" size="lg">
                      가족 찾기
                    </Button>
                  </Link>
                </div>
              </div>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-semibold text-gray-900">
                내 가족 ({families.length})
              </h2>
              <div className="flex gap-2">
                <Link to="/families/search">
                  <Button variant="outline" size="sm">
                    가족 찾기
                  </Button>
                </Link>
                <Link to="/families/create">
                  <Button variant="outline" size="sm">
                    가족 추가
                  </Button>
                </Link>
              </div>
            </div>

            <div className="grid gap-4">
              {families.map((family) => (
                <Card key={family.id} clickable>
                  <CardContent>
                    <Link to={`/families/${family.id}/members`} className="block">
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-lg font-semibold text-gray-900 mb-1">
                            {family.name}
                          </h3>
                          <p className="text-sm text-gray-600 mb-2">
                            구성원 {family.memberCount}명
                          </p>
                          {family.description && (
                            <p className="text-sm text-gray-500">
                              {family.description}
                            </p>
                          )}
                        </div>
                        <div className="flex items-center space-x-2">
                          {family.isPublic ? (
                            <span className="px-2 py-1 text-xs bg-green-100 text-green-700 rounded-full">
                              공개
                            </span>
                          ) : (
                            <span className="px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded-full">
                              비공개
                            </span>
                          )}
                          <svg
                            className="w-5 h-5 text-gray-400"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M9 5l7 7-7 7"
                            />
                          </svg>
                        </div>
                      </div>
                    </Link>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        )}

        <div className="mt-12 text-center">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            다른 기능들
          </h3>
          <div className="grid grid-cols-2 gap-4">
            <Card clickable>
              <CardContent>
                <div className="text-center py-6">
                  <div className="text-3xl mb-2">🌳</div>
                  <h4 className="font-medium text-gray-900 mb-1">가족 트리</h4>
                  <p className="text-sm text-gray-600">
                    가족 관계도 보기
                  </p>
                </div>
              </CardContent>
            </Card>
            <Card clickable>
              <CardContent>
                <div className="text-center py-6">
                  <div className="text-3xl mb-2">📱</div>
                  <h4 className="font-medium text-gray-900 mb-1">연락처</h4>
                  <p className="text-sm text-gray-600">
                    가족 연락처 관리
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;