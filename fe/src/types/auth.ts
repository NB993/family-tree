/**
 * 인증 관련 타입 정의
 */

/**
 * OAuth2 로그인 성공 시 응답
 */
export interface OAuth2Response {
  success: boolean;
  message: string;
  tokenInfo?: {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
  };
  userInfo?: UserInfo;
  errorMessage?: string;
}

/**
 * Access Token 갱신 응답
 */
export interface AccessTokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

/**
 * 로그아웃 응답
 */
export interface LogoutResponse {
  success: boolean;
  message: string;
}

/**
 * 사용자 정보
 */
export interface UserInfo {
  id: number;
  email: string;
  name: string;
  profileImageUrl?: string;
  provider?: string;
  role?: string;
}

/**
 * 인증 상태
 */
export interface AuthState {
  isAuthenticated: boolean | null;
  isLoading: boolean;
  userInfo: UserInfo | null;
}