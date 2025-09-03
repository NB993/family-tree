import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const AuthCallbackPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    const handleAuthCallback = async () => {
      const urlParams = new URLSearchParams(location.search);
      const success = urlParams.get('success');
      const error = urlParams.get('error') ? decodeURIComponent(urlParams.get('error')!) : null;

      console.log('Auth Callback (쿠키 기반):', { success, error });

      if (success === 'false' || error) {
        console.error('OAuth2 로그인 실패:', error);
        alert('로그인에 실패했습니다. 다시 시도해주세요.');
        navigate('/login');
        return;
      }

      if (success === 'true') {
        try {
          // 토큰은 HttpOnly 쿠키에 이미 설정됨
          // /api/users/me API로 사용자 정보 조회
          const response = await fetch('http://localhost:8080/api/user/me', {
            credentials: 'include' // 쿠키 포함
          });
          
          if (response.ok) {
            const userInfo = await response.json();
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
            
            console.log('로그인 성공 (쿠키 기반):', userInfo);
            alert(`${userInfo.name}님, 환영합니다!`);
            
            // 홈 페이지로 이동
            navigate('/home');
          } else {
            throw new Error('사용자 정보 조회 실패');
          }
        } catch (error) {
          console.error('사용자 정보 조회 중 오류:', error);
          alert('로그인 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
          navigate('/login');
        }
      } else {
        console.log('로그인 결과 확인 불가, 로그인 페이지로 이동');
        navigate('/login');
      }
      
      setIsProcessing(false);
    };

    handleAuthCallback();
  }, [location, navigate]);

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

export default AuthCallbackPage;
