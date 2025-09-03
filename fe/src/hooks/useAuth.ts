import { useState, useEffect } from 'react';
import { AuthService } from '../api/services/authService';
import { AuthState, UserInfo } from '../types/auth';

export const useAuth = (): AuthState => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuthStatus = async () => {
      const authService = AuthService.getInstance();
      
      // Access Token 존재 여부 확인
      const accessToken = authService.getAccessToken();
      
      if (!accessToken) {
        // 토큰이 없으면 인증되지 않은 상태
        setIsAuthenticated(false);
        setUserInfo(null);
        setIsLoading(false);
        return;
      }

      try {
        // 토큰이 있으면 사용자 정보 조회
        const userData = await authService.getCurrentUser();
        authService.saveUserInfo(userData);
        setUserInfo(userData);
        setIsAuthenticated(true);
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error);
        // 사용자 정보 조회 실패 시 로컬 정보 확인
        const localUserInfo = authService.getUserInfo();
        if (localUserInfo) {
          setUserInfo(localUserInfo);
          setIsAuthenticated(true);
        } else {
          // 인증 실패로 처리
          setIsAuthenticated(false);
          setUserInfo(null);
          authService.clearAllAuthData();
        }
      } finally {
        setIsLoading(false);
      }
    };

    checkAuthStatus();
  }, []);

  return {
    isAuthenticated,
    isLoading,
    userInfo
  };
};
