import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { OAuth2Response } from '../types/auth';
import { AuthService } from '../api/services/authService';

const OAuth2CallbackPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');
    const error = urlParams.get('error');

    console.log('OAuth2 Callback:', { code, state, error });

    if (error) {
      console.error('OAuth2 로그인 실패:', error);
      alert('로그인에 실패했습니다. 다시 시도해주세요.');
      navigate('/login');
      return;
    }

    if (code) {
      // 백엔드로 OAuth2 처리 요청
      handleOAuth2Callback(code, state);
    } else {
      console.log('인증 코드가 없음, 로그인 페이지로 이동');
      navigate('/login');
    }
  }, [location, navigate]);

  const handleOAuth2Callback = async (code: string, state: string | null) => {
    try {
      setIsProcessing(true);
      const authService = AuthService.getInstance();
      
      // 백엔드의 OAuth2 콜백 엔드포인트로 요청
      const response = await fetch(`http://localhost:8080${location.pathname}${location.search}`, {
        method: 'GET',
        credentials: 'include', // 쿠키 포함 (Refresh Token이 HttpOnly 쿠키로 설정됨)
      });

      if (response.ok) {
        const data: OAuth2Response = await response.json();
        
        if (data.success && data.tokenInfo) {
          // Access Token만 localStorage에 저장 (Refresh Token은 HttpOnly 쿠키로 관리)
          authService.saveAccessToken(data.tokenInfo.accessToken);
          
          // 사용자 정보 저장
          if (data.userInfo) {
            authService.saveUserInfo(data.userInfo);
          }
          
          console.log('로그인 성공:', data.userInfo);
          alert(`${data.userInfo?.name}님, 환영합니다!`);
          
          // 홈 페이지로 이동
          navigate('/home');
        } else {
          throw new Error(data.errorMessage || '로그인 처리 중 오류가 발생했습니다.');
        }
      } else {
        throw new Error('서버 응답 오류');
      }
    } catch (error) {
      console.error('OAuth2 처리 실패:', error);
      alert('로그인 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
      navigate('/login');
    } finally {
      setIsProcessing(false);
    }
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