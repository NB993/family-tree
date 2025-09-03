/**
 * 가족 관련 타입 정의
 * 백엔드 엔티티와 일치하도록 정의
 */

export enum FamilyMemberRelationshipType {
  // 직계 가족 (Direct Family)
  FATHER = '아버지',
  MOTHER = '어머니',
  SON = '아들',
  DAUGHTER = '딸',

  // 조부모/손자 (Grandparents/Grandchildren)
  GRANDFATHER = '할아버지',
  GRANDMOTHER = '할머니',
  GRANDSON = '손자',
  GRANDDAUGHTER = '손녀',

  // 형제자매 (Siblings)
  ELDER_BROTHER = '형',
  ELDER_SISTER = '누나/언니',
  YOUNGER_BROTHER = '남동생',
  YOUNGER_SISTER = '여동생',

  // 배우자 (Spouse)
  HUSBAND = '남편',
  WIFE = '아내',

  // 친척 (Relatives)
  UNCLE = '삼촌/외삼촌',
  AUNT = '고모/이모',
  NEPHEW = '조카',
  NIECE = '조카딸',
  COUSIN = '사촌',

  // 확장된 관계 (Extended Relations)
  FATHER_IN_LAW = '시아버지/장인',
  MOTHER_IN_LAW = '시어머니/장모',
  SON_IN_LAW = '사위',
  DAUGHTER_IN_LAW = '며느리',
  BROTHER_IN_LAW = '처남/형부/시숙/매형',
  SISTER_IN_LAW = '처제/형수/시누이/올케',

  // 사용자 정의 (Custom)
  CUSTOM = '직접 입력',
}

export enum FamilyMemberRole {
  OWNER = 'OWNER',
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER',
  SUSPENDED = 'SUSPENDED',
}

export enum FamilyMemberStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  PENDING = 'PENDING',
}

export interface FamilyMember {
  id: number;
  name: string;
  profileImage?: string;
  age: number;
  birthDate: string;
  phoneNumber?: string;
  relationship?: FamilyMemberRelationshipType | string;
  customRelationship?: string;
  isKakaoSynced?: boolean;
  role: FamilyMemberRole;
  status: FamilyMemberStatus;
  address?: string;
  occupation?: string;
  email?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  familyId: number;
  userId?: number;
  joinedAt: string;
  lastActiveAt?: string;
}

export interface Family {
  id: number;
  name: string;
  description?: string;
  isPublic: boolean;
  memberCount: number;
  ownerId: number;
  createdAt: string;
  updatedAt: string;
}

export interface FamilyMemberRelationship {
  id: number;
  fromMemberId: number;
  toMemberId: number;
  relationshipType: FamilyMemberRelationshipType;
  customRelationship?: string;
  createdAt: string;
  updatedAt: string;
}

export interface RelationshipSuggestion {
  relationship: FamilyMemberRelationshipType;
  confidence: number;
  reasoning: string;
}

export interface FamilyJoinRequest {
  id: number;
  familyId: number;
  userId: number;
  requestMessage?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  processedAt?: string;
  processedBy?: number;
}

export interface Announcement {
  id: number;
  familyId: number;
  title: string;
  content: string;
  authorId: number;
  isImportant: boolean;
  createdAt: string;
  updatedAt: string;
}

// API Response 타입
export interface FindFamilyResponse {
  families: Family[];
  totalCount: number;
  hasMore: boolean;
}

export interface FindFamilyMemberResponse {
  members: FamilyMember[];
  totalCount: number;
  hasMore: boolean;
}

// Form 타입
export interface CreateFamilyForm {
  name: string;
  description?: string;
  isPublic: boolean;
}

export interface CreateFamilyMemberForm {
  name: string;
  birthDate: string;
  phoneNumber?: string;
  relationship?: FamilyMemberRelationshipType | string;
  customRelationship?: string;
  address?: string;
  occupation?: string;
  email?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
}

export interface UpdateFamilyMemberForm extends Partial<CreateFamilyMemberForm> {
  id: number;
}

// 유틸리티 타입
export type FamilyMemberWithRelationships = FamilyMember & {
  relationships: FamilyMemberRelationship[];
};

export type FamilyWithMembers = Family & {
  members: FamilyMember[];
  owner: FamilyMember;
};