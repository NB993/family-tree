import React, { createContext, useState, useEffect, useMemo, useContext, ReactNode, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../api/services/authService';
import { AuthState, UserInfo } from '../types/auth';

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

  useEffect(() => {
    const checkAuthStatus = async () => {
      const authService = AuthService.getInstance();
      try {
        const userData = await authService.getCurrentUser();
        authService.saveUserInfo(userData);
        setUserInfo(userData);
        setIsAuthenticated(true);
      } catch (error) {
        authService.clearAllAuthData();
        setUserInfo(null);
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuthStatus();
  }, []);

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