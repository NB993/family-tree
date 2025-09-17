import React, { createContext, useState, useEffect, useMemo, useContext, ReactNode, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../api/services/authService';
import { AuthState, UserInfo } from '../types/auth';
import { useApiError } from '../hooks/useApiError';
import { ApiClient } from '../api/client';
import { ErrorHandlers } from '../types/error';

// 인증 관련 에러를 처리하는 핸들러
const authErrorHandlers: ErrorHandlers = {
  // 인증 실패 (잘못된 토큰 등)
  A001: (error) => {
    console.error('인증 실패:', error.message);
    // 기본 핸들러나 401 상태 코드 핸들러가 로그인 페이지로 보내주므로 중복 작업은 불필요.
    // 여기서는 에러 로깅만 담당.
  },
  // 리프레시 토큰으로 유저 조회 실패
  A002: (error) => {
    console.error('인증 싫패. 다시 로그인해주세요:', error.message);
    window.location.href = '/login'; // 재로그인 유도
  },
  // 리프레시 토큰 없음 또는 만료
  A006: (error) => {
    console.error('세션이 만료되었습니다. 다시 로그인해주세요:', error.message);
    window.location.href = '/login'; // 재로그인 유도
  },
};

interface AuthContextType extends AuthState {
  logout: () => Promise<void>;
}

// Context 생성
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// AuthProvider 컴포넌트
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const { handleError } = useApiError(authErrorHandlers);

  useEffect(() => {
    // API 클라이언트에 에러 핸들러를 설정합니다.
    const apiClient = ApiClient.getInstance();
    apiClient.setErrorHandler(handleError);

    const checkAuthStatus = async () => {
      const authService = AuthService.getInstance();
      try {
        const userData = await authService.getCurrentUser();
        authService.saveUserInfo(userData);
        setUserInfo(userData);
        setIsAuthenticated(true);
      } catch (error) {
        // 에러는 useApiError 훅에 의해 전역적으로 처리됩니다.
        // 여기서는 인증 상태를 false로 설정하는 작업만 수행합니다.
        authService.clearAllAuthData();
        setUserInfo(null);
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuthStatus();
  }, [handleError]);

  const logout = useCallback(async () => {
    const authService = AuthService.getInstance();
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout API call failed, proceeding with client-side logout', error);
    } finally {
      authService.clearAllAuthData();
      setIsAuthenticated(false);
      setUserInfo(null);
      navigate('/login');
    }
  }, [navigate]);

  const authState = useMemo(() => ({
    isAuthenticated,
    isLoading,
    userInfo,
    logout,
  }), [isAuthenticated, isLoading, userInfo, logout]);

  return (
    <AuthContext.Provider value={authState}>
      {children}
    </AuthContext.Provider>
  );
};

// useAuth 커스텀 훅
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
