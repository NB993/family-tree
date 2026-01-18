/**
 * 태그 관련 API 서비스
 * PRD-006: FamilyMember 태그 기능
 */

import { ApiClient } from '../client';
import {
  Tag,
  SaveTagRequest,
  ModifyTagRequest,
  MemberTagsResponse,
} from '../../types/tag';

export class TagService {
  private static instance: TagService;
  private apiClient: ApiClient;

  private constructor() {
    this.apiClient = ApiClient.getInstance();
  }

  public static getInstance(): TagService {
    if (!TagService.instance) {
      TagService.instance = new TagService();
    }
    return TagService.instance;
  }

  /**
   * 태그 목록을 조회합니다.
   */
  public async findTags(familyId: number | string): Promise<Tag[]> {
    return this.apiClient.get<Tag[]>(`/api/families/${familyId}/tags`);
  }

  /**
   * 새로운 태그를 생성합니다.
   */
  public async saveTag(familyId: number | string, request: SaveTagRequest): Promise<Tag> {
    return this.apiClient.post<Tag>(`/api/families/${familyId}/tags`, request);
  }

  /**
   * 태그를 수정합니다.
   */
  public async modifyTag(
    familyId: number | string,
    tagId: number | string,
    request: ModifyTagRequest
  ): Promise<Tag> {
    return this.apiClient.put<Tag>(`/api/families/${familyId}/tags/${tagId}`, request);
  }

  /**
   * 태그를 삭제합니다.
   */
  public async deleteTag(familyId: number | string, tagId: number | string): Promise<void> {
    return this.apiClient.delete<void>(`/api/families/${familyId}/tags/${tagId}`);
  }

  /**
   * 멤버에 태그를 할당합니다.
   */
  public async modifyMemberTags(
    familyId: number | string,
    memberId: number | string,
    tagIds: number[]
  ): Promise<MemberTagsResponse> {
    return this.apiClient.put<MemberTagsResponse>(
      `/api/families/${familyId}/members/${memberId}/tags`,
      { tagIds }
    );
  }
}