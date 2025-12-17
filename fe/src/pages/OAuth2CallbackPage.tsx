import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthService } from '../api/services/authService';
import { logger } from '../utils/logger';

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

    logger.debug('OAuth2 Callback:', { success, error, path: location.pathname });

    if (error) {
      logger.error('OAuth2 로그인 실패:', error);
      alert('로그인에 실패했습니다. 다시 시도해주세요.');
      setHasProcessed(true);
      navigate('/login');
      return;
    }

    // 백엔드가 성공적으로 리다이렉트한 경우
    if (success === 'true') {
      handleOAuth2Success();
    } else {
      logger.info('인증 실패, 로그인 페이지로 이동');
      setHasProcessed(true);
      navigate('/login');
    }
  }, [location, navigate, hasProcessed]);

  const handleOAuth2Success = async () => {
    // 이미 처리 중이거나 처리 완료된 경우 스킵
    if (isProcessing || hasProcessed) return;

    setIsProcessing(true);
    setHasProcessed(true); // 중복 실행 방지

    logger.info('OAuth2 login successful, verifying authentication...');

    try {
      // 쿠키가 제대로 설정되었는지 확인하기 위해 현재 사용자 정보 조회 시도
      const authService = AuthService.getInstance();
      const userInfo = await authService.getCurrentUser();

      logger.info('Authentication verified successfully:', { userId: userInfo.id, email: userInfo.email });

      // 인증 확인 후 홈으로 이동
      navigate('/home');
    } catch (error) {
      logger.error('Authentication verification failed:', error);
      alert('인증 확인에 실패했습니다. 쿠키가 제대로 설정되지 않았을 수 있습니다. 다시 로그인해주세요.');
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
