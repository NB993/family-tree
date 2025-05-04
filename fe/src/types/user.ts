/**
 * 사용자 정보 타입
 */
export interface User {
  id: number;
  name: string;
  email: string;
  authType: string;
  provider: string;
}
