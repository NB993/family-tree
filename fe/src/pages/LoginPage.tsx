import React from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { useAuth } from '../contexts/AuthContext';
import { Search } from 'lucide-react';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, isLoading } = useAuth();

  const handleKakaoLogin = () => {
    window.location.href = `${process.env.REACT_APP_API_URL}/oauth2/authorization/kakao`;
  };

  if (isLoading) {
    return (
      <div className="app-shell flex items-center justify-center">
        <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to="/home" replace />;
  }

  return (
    <div className="app-shell flex flex-col">
      {/* Header */}
      <div className="flex-1 flex flex-col justify-center px-4">
        <h1 className="text-xl font-semibold text-foreground">오래오래</h1>
        <p className="text-xs text-muted-foreground mt-0.5">소중한 사람들의 연락처를 한곳에</p>
      </div>

      {/* Actions */}
      <div className="p-4 space-y-2 border-t border-border">
        <Button
          onClick={handleKakaoLogin}
          className="w-full bg-[#FEE500] hover:bg-[#FDD835] text-[#191919]"
        >
          <svg className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 3C6.477 3 2 6.477 2 11c0 2.89 1.922 5.464 4.811 6.89l-.736 2.698a.3.3 0 00.437.333l3.195-2.11A9.847 9.847 0 0012 19c5.523 0 10-3.477 10-8s-4.477-8-10-8z"/>
          </svg>
          카카오로 시작하기
        </Button>

        <Button variant="outline" onClick={() => navigate('/families/search')} className="w-full">
          <Search className="w-3.5 h-3.5" strokeWidth={1.5} />
          둘러보기
        </Button>

        <p className="text-center text-[10px] text-muted-foreground pt-1">
          계속 진행하면{' '}
          <button type="button" className="underline hover:text-primary">서비스 약관</button>
          {' 및 '}
          <button type="button" className="underline hover:text-primary">개인정보 처리방침</button>
          에 동의하게 됩니다.
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
