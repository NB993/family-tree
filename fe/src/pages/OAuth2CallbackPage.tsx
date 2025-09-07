import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthService } from '../api/services/authService';

const OAuth2CallbackPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isProcessing, setIsProcessing] = useState(false);
  const [hasProcessed, setHasProcessed] = useState(false);

  useEffect(() => {
    // 이미 처리했으면 스킵
    if (hasProcessed) return;
    
    const urlParams = new URLSearchParams(location.search);
    const success = urlParams.get('success');
    const error = urlParams.get('error');

    console.log('OAuth2 Callback:', { success, error, path: location.pathname });

    if (error) {
      console.error('OAuth2 로그인 실패:', error);
      alert('로그인에 실패했습니다. 다시 시도해주세요.');
      setHasProcessed(true);
      navigate('/login');
      return;
    }

    // 백엔드가 성공적으로 리다이렉트한 경우
    if (success === 'true') {
      handleOAuth2Success();
    } else {
      console.log('인증 실패, 로그인 페이지로 이동');
      setHasProcessed(true);
      navigate('/login');
    }
  }, [location, navigate, hasProcessed]);

  const handleOAuth2Success = async () => {
    // 이미 처리 중이거나 처리 완료된 경우 스킵
    if (isProcessing || hasProcessed) return;
    
    setIsProcessing(true);
    setHasProcessed(true); // 중복 실행 방지
    
    // 백엔드에서 HttpOnly 쿠키로 토큰을 설정했으므로
    // 바로 홈으로 이동. ProtectedRoute에서 인증 확인
    console.log('OAuth2 login successful, navigating to /home...');
    
    // 홈 페이지로 이동
    // ProtectedRoute가 useAuth를 통해 /api/user/me를 호출하여 인증 확인
    setTimeout(() => {
      navigate('/home');
    }, 100);
    
    setIsProcessing(false);
  };

  return (
    <div className="container">
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">
            {isProcessing ? '로그인 처리 중...' : '로그인 완료'}
          </p>
        </div>
      </div>
    </div>
  );
};

export default OAuth2CallbackPage;
