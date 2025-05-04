import { ApiClient } from '../client';
import { User } from '../../types/user';

/**
 * 사용자 관련 API 요청을 처리하는 클래스
 */
export class UserService {
  private static instance: UserService;
  private apiClient: ApiClient;

  private constructor() {
    this.apiClient = ApiClient.getInstance();
  }

  /**
   * UserApi의 싱글턴 인스턴스를 반환합니다.
   */
  public static getInstance(): UserService {
    if (!UserService.instance) {
      UserService.instance = new UserService();
    }
    return UserService.instance;
  }

  /**
   * 현재 인증된 사용자의 프로필 정보를 가져옵니다.
   * @returns 사용자 프로필 정보
   */
  public async getCurrentUser(): Promise<User> {
    return this.apiClient.get<User>('/api/user/me');
  }
}
