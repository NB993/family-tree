import { ApiClient } from "../client";
import { FamilyTree, FamilyTreeNode, FamilyTreeMetadata } from "../../types/familyTree";
import { FamilyMember } from "../../types/family";
import { PaginatedResponse } from "../../types/api";

export class FamilyTreeService {
  private static instance: FamilyTreeService;
  private apiClient: ApiClient;

  private constructor() {
    this.apiClient = ApiClient.getInstance();
  }

  public static getInstance(): FamilyTreeService {
    if (!FamilyTreeService.instance) {
      FamilyTreeService.instance = new FamilyTreeService();
    }
    return FamilyTreeService.instance;
  }

  /**
   * 가족 트리 전체 구조를 조회합니다.
   */
  public async getFamilyTree(familyId: string): Promise<FamilyTree> {
    return this.apiClient.get<FamilyTree>(`/api/families/${familyId}/tree`);
  }

  /**
   * 가족 트리 메타데이터를 조회합니다.
   */
  public async getFamilyTreeMetadata(familyId: string): Promise<FamilyTreeMetadata> {
    return this.apiClient.get<FamilyTreeMetadata>(`/api/families/${familyId}/tree/metadata`);
  }

  /**
   * 특정 구성원을 중심으로 한 부분 트리를 조회합니다.
   */
  public async getFamilySubTree(familyId: string, memberId: string, depth: number = 2): Promise<FamilyTreeNode> {
    return this.apiClient.get<FamilyTreeNode>(
      `/api/families/${familyId}/tree/members/${memberId}?depth=${depth}`
    );
  }

  /**
   * 레거시 API - 가족 구성원 목록 조회 (하위 호환성)
   */
  public async getFamilyMembers(
    familyId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PaginatedResponse<FamilyMember>> {
    return this.apiClient.get<PaginatedResponse<FamilyMember>>(
      `/api/families/${familyId}/members?page=${page}&size=${size}`
    );
  }
}
