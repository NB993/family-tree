import React, { useEffect } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useApiError } from './hooks/useApiError';
import { ApiClient } from './api/client';
import { ErrorHandlers } from './types/error';
import { AppLayout } from './components/layout/AppLayout';
import { ProtectedRoute, RootRedirect } from './components/Auth';

// Pages
import HomePage from './pages/HomePage';
import FamilyPage from './pages/FamilyPage';
import FamilyMembersPage from './pages/FamilyMembersPage';
import CreateFamilyPage from './pages/CreateFamilyPage';
import { FamilySearchPage } from './pages/FamilySearch';
import NotFoundPage from './pages/NotFoundPage';
import LoginPage from './pages/LoginPage';
import OAuth2CallbackPage from './pages/OAuth2CallbackPage';
import AuthCallbackPage from './pages/AuthCallbackPage';

// Styles
import './styles/App.css';
import './design-system/global.css';

// React Query 클라이언트 설정
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5분
    },
    mutations: {
      retry: 1,
    },
  },
});

const customHandlers: ErrorHandlers = {
  S001: (error) => console.error('커스텀 핸들러:', error.message),
  F001: (error) => console.error('가족 관련 에러:', error.message),
  A001: (error) => console.error('인증 관련 에러:', error.message),
};

function App() {
  const { handleError } = useApiError(customHandlers);

  useEffect(() => {
    const apiClient = ApiClient.getInstance();
    apiClient.setErrorHandler(handleError);
  }, [handleError]);

  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="App">
          <AppLayout maxWidth="mobile" padding>
            <Routes>
              <Route path="/" element={<RootRedirect />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/auth/callback" element={<AuthCallbackPage />} />
              <Route path="/login/oauth2/code/kakao" element={<OAuth2CallbackPage />} />
              <Route path="/login/oauth2/code/google" element={<OAuth2CallbackPage />} />
              <Route path="/home" element={
                <ProtectedRoute>
                  <HomePage />
                </ProtectedRoute>
              } />
              <Route path="/families/create" element={
                <ProtectedRoute>
                  <CreateFamilyPage />
                </ProtectedRoute>
              } />
              <Route path="/families/search" element={<FamilySearchPage />} />
              <Route path="/families/:familyId" element={<FamilyPage />} />
              <Route path="/families/:familyId/members" element={
                <ProtectedRoute>
                  <FamilyMembersPage />
                </ProtectedRoute>
              } />
              <Route path="/404" element={<NotFoundPage />} />
              <Route path="*" element={<Navigate to="/404" replace />} />
            </Routes>
          </AppLayout>
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App; 
