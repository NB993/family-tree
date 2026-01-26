import { BirthdayType } from '../api/services/familyService';

/**
 * 사용자 정보 타입 (FindUserResponse)
 */
export interface User {
  id: number;
  email: string;
  name: string;
  profileUrl?: string;
  birthday?: string;
  birthdayType?: BirthdayType;
  createdAt?: string;
  modifiedAt?: string;
}

/**
 * 사용자 프로필 수정 요청 타입 (ModifyUserRequest)
 */
export interface ModifyUserRequest {
  name: string;
  birthday?: string;
  birthdayType?: BirthdayType;
}

/**
 * 사용자 프로필 수정 응답 타입 (ModifyUserResponse)
 */
export interface ModifyUserResponse {
  id: number;
  name: string;
  email: string;
  profileUrl?: string;
  birthday?: string;
  birthdayType?: BirthdayType;
}
