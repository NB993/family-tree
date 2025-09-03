import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export const RootRedirect: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();

  // 로딩 중일 때
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-orange-500 mx-auto mb-2"></div>
          <p className="text-gray-600">로딩 중...</p>
        </div>
      </div>
    );
  }

  // 인증된 경우 홈으로, 게스트는 가족 검색 페이지로 리다이렉트
  return <Navigate to={isAuthenticated ? "/home" : "/families/search"} replace />;
};