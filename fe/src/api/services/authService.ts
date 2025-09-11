/**
 * 인증 관련 API 서비스
 * JWT 토큰 갱신, 로그아웃 등 인증 관련 기능 제공
 */

import type { AccessTokenResponse, LogoutResponse, UserInfo } from '../../types/auth';

export class AuthService {
  private static instance: AuthService;

  private constructor() {
    // 순환 참조를 막기 위해 생성자에서 ApiClient 인스턴스화를 제거합니다.
  }

  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  private async getApiClient() {
    // 동적 import를 사용하여 순환 참조를 해결합니다.
    const { ApiClient } = await import('../client');
    return ApiClient.getInstance();
  }

  /**
   * Access Token을 갱신합니다.
   * Refresh Token은 HttpOnly 쿠키로 자동 전송됩니다.
   */
  public async refreshAccessToken(): Promise<AccessTokenResponse> {
    const apiClient = await this.getApiClient();
    return apiClient.post<AccessTokenResponse>('/api/auth/refresh');
  }

  /**
   * 로그아웃을 수행합니다.
   * 서버에서 Refresh Token을 무효화하고 쿠키를 삭제합니다.
   */
  public async logout(): Promise<LogoutResponse> {
    const apiClient = await this.getApiClient();
    return apiClient.post<LogoutResponse>('/api/auth/logout');
  }

  /**
   * 현재 인증된 사용자 정보를 조회합니다.
   */
  public async getCurrentUser(): Promise<UserInfo> {
    const apiClient = await this.getApiClient();
    return apiClient.get<UserInfo>('/api/user/me');
  }

  /**
   * Access Token을 localStorage에 저장합니다.
   */
  public saveAccessToken(accessToken: string): void {
    localStorage.setItem('accessToken', accessToken);
  }

  /**
   * Access Token을 localStorage에서 가져옵니다.
   */
  public getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  /**
   * Access Token을 localStorage에서 제거합니다.
   */
  public clearAccessToken(): void {
    localStorage.removeItem('accessToken');
  }

  /**
   * 사용자 정보를 localStorage에 저장합니다.
   */
  public saveUserInfo(userInfo: UserInfo): void {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  }

  /**
   * 사용자 정보를 localStorage에서 가져옵니다.
   */
  public getUserInfo(): UserInfo | null {
    const userInfoStr = localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    
    try {
      return JSON.parse(userInfoStr);
    } catch (error) {
      console.error('사용자 정보 파싱 오류:', error);
      return null;
    }
  }

  /**
   * 사용자 정보를 localStorage에서 제거합니다.
   */
  public clearUserInfo(): void {
    localStorage.removeItem('userInfo');
  }

  /**
   * 모든 인증 정보를 localStorage에서 제거합니다.
   */
  public clearAllAuthData(): void {
    this.clearAccessToken();
    this.clearUserInfo();
    // refreshToken은 제거하지 않음 (localStorage에 저장하지 않도록 변경됨)
  }
}
