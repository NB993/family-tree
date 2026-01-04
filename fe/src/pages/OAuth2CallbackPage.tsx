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

    if (success === 'true') {
      handleOAuth2Success();
    } else {
      logger.info('인증 실패, 로그인 페이지로 이동');
      setHasProcessed(true);
      navigate('/login');
    }
  }, [location, navigate, hasProcessed]);

  const handleOAuth2Success = async () => {
    if (isProcessing || hasProcessed) return;

    setIsProcessing(true);
    setHasProcessed(true);

    logger.info('OAuth2 login successful, verifying authentication...');

    try {
      const authService = AuthService.getInstance();
      const userInfo = await authService.getCurrentUser();

      logger.info('Authentication verified successfully:', { userId: userInfo.id, email: userInfo.email });

      navigate('/home');
    } catch (error) {
      logger.error('Authentication verification failed:', error);
      alert('인증 확인에 실패했습니다. 다시 로그인해주세요.');
      navigate('/login');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="app-shell flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary border-t-transparent mx-auto" />
        <p className="text-[10px] text-muted-foreground mt-2">
          {isProcessing ? '로그인 처리 중...' : '로그인 완료'}
        </p>
      </div>
    </div>
  );
};

export default OAuth2CallbackPage;
