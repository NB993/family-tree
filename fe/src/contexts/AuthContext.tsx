import React, { createContext, useState, useEffect, useMemo, useContext, ReactNode, useCallback, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthService } from '../api/services/authService';
import { AuthState, UserInfo } from '../types/auth';
import { useApiError } from '../hooks/useApiError';
import { ApiClient } from '../api/client';
import { ErrorHandlers } from '../types/error';
import { logger } from '../utils/logger';

// 인증이 필요 없는 공개 페이지 경로 패턴
const PUBLIC_PATHS = [
  '/login',
  '/auth/callback',
  '/login/oauth2/code/kakao',
  '/login/oauth2/code/google',
  /^\/invite\/[^/]+$/, // /invite/:inviteCode
  /^\/invite\/[^/]+\/callback$/, // /invite/:inviteCode/callback
];

// 현재 경로가 공개 페이지인지 확인하는 함수
const isPublicPath = (pathname: string): boolean => {
  return PUBLIC_PATHS.some(path => {
    if (typeof path === 'string') {
      return pathname === path;
    }
    return path.test(pathname);
  });
};

// 인증 관련 에러를 처리하는 핸들러
const authErrorHandlers: ErrorHandlers = {
  // 인증 실패 (잘못된 토큰 등)
  A001: (error) => {
    logger.error('인증 실패:', error.message);
    // 기본 핸들러나 401 상태 코드 핸들러가 로그인 페이지로 보내주므로 중복 작업은 불필요.
    // 여기서는 에러 로깅만 담당.
  },
  // 리프레시 토큰으로 유저 조회 실패
  A002: (error) => {
    logger.error('인증 실패. 다시 로그인해주세요:', error.message);
    window.location.href = '/login'; // 재로그인 유도
  },
  // 리프레시 토큰 없음 또는 만료
  A006: (error) => {
    logger.error('세션이 만료되었습니다. 다시 로그인해주세요:', error.message);
    window.location.href = '/login'; // 재로그인 유도
  },
};

interface AuthContextType extends AuthState {
  logout: () => Promise<void>;
  confirmAuthentication: (userData: UserInfo) => void;
}

// Context 생성
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// AuthProvider 컴포넌트
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const isAuthenticatedRef = useRef<boolean>(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { handleError } = useApiError(authErrorHandlers);

  // isAuthenticated 상태가 변경될 때 ref도 업데이트
  useEffect(() => {
    isAuthenticatedRef.current = isAuthenticated;
  }, [isAuthenticated]);

  // 인증 완료를 명시적으로 확정하는 메서드
  const confirmAuthentication = useCallback((userData: UserInfo) => {
    const authService = AuthService.getInstance();
    authService.saveUserInfo(userData);
    setUserInfo(userData);
    setIsAuthenticated(true);
    setIsLoading(false);
    // ref를 동기적으로 업데이트하여 navigate 직후 checkAuthStatus에서 스킵되도록 함
    isAuthenticatedRef.current = true;
  }, []);

  useEffect(() => {
    // API 클라이언트에 에러 핸들러를 설정합니다.
    const apiClient = ApiClient.getInstance();
    apiClient.setErrorHandler(handleError);

    const checkAuthStatus = async () => {
      // 공개 페이지에서는 인증 체크를 건너뜁니다.
      if (isPublicPath(location.pathname)) {
        setIsLoading(false);
        return;
      }

      // 이미 인증된 상태면 스킵
      if (isAuthenticatedRef.current) {
        setIsLoading(false);
        return;
      }

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
  }, [handleError, location.pathname]);

  const logout = useCallback(async () => {
    const authService = AuthService.getInstance();
    try {
      await authService.logout();
    } catch (error) {
      logger.error('Logout API call failed, proceeding with client-side logout', error);
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
    confirmAuthentication,
  }), [isAuthenticated, isLoading, userInfo, logout, confirmAuthentication]);

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
