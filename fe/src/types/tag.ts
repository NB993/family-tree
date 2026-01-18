/**
 * 태그 관련 타입 정의
 * PRD-006: FamilyMember 태그 기능
 */

// 태그 색상 팔레트 (노션 스타일, 백엔드와 동일)
export const TAG_COLOR_PALETTE = [
  '#E3E2E0', // Light Gray
  '#EEE0DA', // Brown
  '#FADEC9', // Orange
  '#FDECC8', // Yellow
  '#DBEDDB', // Green
  '#D3E5EF', // Blue
  '#E8DEEE', // Purple
  '#F5E0E9', // Pink
  '#FFE2DD', // Red
] as const;

export type TagColor = typeof TAG_COLOR_PALETTE[number];

/**
 * 태그 기본 정보 (목록 조회 시)
 */
export interface Tag {
  id: number;
  name: string;
  color: string;
  memberCount: number;
  createdAt: string;
}

/**
 * 태그 간략 정보 (멤버에 할당된 태그 표시용)
 */
export interface TagSimple {
  id: number;
  name: string;
  color: string;
}

/**
 * 태그 목록 조회 응답
 */
export interface TagListResponse {
  tags: Tag[];
  totalCount: number;
  maxCount: number;
}

/**
 * 태그 생성 요청
 */
export interface SaveTagRequest {
  name: string;
}

/**
 * 태그 수정 요청
 */
export interface ModifyTagRequest {
  name: string;
  color?: string;
}

/**
 * 멤버 태그 할당 요청
 */
export interface ModifyMemberTagsRequest {
  tagIds: number[];
}

/**
 * 멤버 태그 할당 응답
 */
export interface MemberTagsResponse {
  memberId: number;
  memberName: string;
  tags: TagSimple[];
}