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
      
      try {
        // HttpOnly 쿠키로 인증된 상태인지 확인하기 위해 사용자 정보 조회
        console.log('Checking authentication status...');
        const userData = await authService.getCurrentUser();
        console.log('User data fetched:', userData);
        
        // 사용자 정보를 성공적으로 가져왔으면 인증된 상태
        authService.saveUserInfo(userData);
        setUserInfo(userData);
        setIsAuthenticated(true);
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error);
        
        // 로컬에 저장된 사용자 정보가 있는지 확인
        const localUserInfo = authService.getUserInfo();
        console.log('Local user info:', localUserInfo);
        
        if (localUserInfo) {
          // 로컬 정보가 있으면 일단 사용 (하지만 실제 인증은 쿠키로 확인)
          setUserInfo(localUserInfo);
          setIsAuthenticated(true);
        } else {
          // 완전히 인증되지 않은 상태
          console.log('Not authenticated');
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
