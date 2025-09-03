import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader } from '../components/common/Card';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  
  const handleKakaoLogin = () => {
    // 백엔드의 OAuth2 인증 엔드포인트로 리다이렉트
    window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
  };

  const handleGoogleLogin = () => {
    // 백엔드의 OAuth2 인증 엔드포인트로 리다이렉트
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <div className="container">
      <div className="flex items-center justify-center min-h-screen">
        <Card className="w-full max-w-md">
          <CardHeader>
            <div className="text-center">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                🏠 Family Tree
              </h1>
              <p className="text-gray-600">
                가족의 이야기를 따뜻하게 기록하세요
              </p>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="text-center mb-6">
                <p className="text-sm text-gray-500">
                  소셜 계정으로 간편하게 시작하세요
                </p>
              </div>

              {/* 카카오 공식 로그인 버튼 */}
              <button
                onClick={handleKakaoLogin}
                className="w-full h-11 bg-[#FEE500] hover:bg-[#FDD835] text-black font-normal rounded-md transition-colors duration-200 flex items-center justify-center text-sm"
              >
                <svg
                  className="w-4 h-4 mr-2 text-black"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path d="M12 3C6.477 3 2 6.477 2 11c0 2.89 1.922 5.464 4.811 6.89l-.736 2.698a.3.3 0 00.437.333l3.195-2.11A9.847 9.847 0 0012 19c5.523 0 10-3.477 10-8s-4.477-8-10-8z"/>
                </svg>
                카카오 로그인
              </button>

              {/* 구글 공식 로그인 버튼 */}
              <button
                onClick={handleGoogleLogin}
                className="w-full h-11 bg-white hover:bg-gray-50 text-[#3c4043] font-normal rounded-md border border-[#dadce0] transition-colors duration-200 flex items-center justify-center text-sm"
              >
                <svg
                  className="w-4 h-4 mr-2"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="#4285F4"
                    d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  />
                  <path
                    fill="#34A853"
                    d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  />
                  <path
                    fill="#FBBC05"
                    d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  />
                  <path
                    fill="#EA4335"
                    d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  />
                </svg>
                Google 계정으로 로그인
              </button>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-gray-500">또는</span>
                </div>
              </div>
              
              {/* 게스트 모드 버튼 */}
              <button
                onClick={() => navigate('/families/search')}
                className="w-full h-11 bg-gray-100 hover:bg-gray-200 text-gray-700 font-normal rounded-md transition-colors duration-200 flex items-center justify-center text-sm"
              >
                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                게스트로 둥러보기
              </button>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
              </div>

              <div className="text-center">
                <p className="text-sm text-gray-600">
                  계속 진행하면{' '}
                  <button 
                    type="button"
                    className="text-orange-500 hover:text-orange-600 underline bg-transparent border-none cursor-pointer"
                    onClick={() => console.log('서비스 약관')}
                  >
                    서비스 약관
                  </button>
                  {' 및 '}
                  <button 
                    type="button"
                    className="text-orange-500 hover:text-orange-600 underline bg-transparent border-none cursor-pointer"
                    onClick={() => console.log('개인정보 처리방침')}
                  >
                    개인정보 처리방침
                  </button>
                  에 동의하게 됩니다.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default LoginPage;