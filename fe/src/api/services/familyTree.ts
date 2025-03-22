import { ApiClient } from "../client";
import {
  Family,
  FamilyMember,
  CreateFamilyMemberDto,
  UpdateFamilyMemberDto,
} from "../../types/familyTree";
import { PaginatedResponse } from "../../types/api";

export class FamilyTreeService {
  private static instance: FamilyTreeService;
  private apiClient: ApiClient;
  private readonly BASE_URL = "/api/family-tree";

  private constructor() {
    this.apiClient = ApiClient.getInstance();
  }

  public static getInstance(): FamilyTreeService {
    if (!FamilyTreeService.instance) {
      FamilyTreeService.instance = new FamilyTreeService();
    }
    return FamilyTreeService.instance;
  }

  public async getFamily(): Promise<Family> {
    return this.apiClient.get<Family>(this.BASE_URL);
  }

  public async getFamilyMember(id: string): Promise<FamilyMember> {
    return this.apiClient.get<FamilyMember>(`${this.BASE_URL}/members/${id}`);
  }

  public async getFamilyMembers(
    page: number = 0,
    size: number = 20
  ): Promise<PaginatedResponse<FamilyMember>> {
    return this.apiClient.get<PaginatedResponse<FamilyMember>>(
      `${this.BASE_URL}/members`,
      {
        params: { page, size },
      }
    );
  }

  public async createFamilyMember(
    member: CreateFamilyMemberDto
  ): Promise<FamilyMember> {
    return this.apiClient.post<FamilyMember>(
      `${this.BASE_URL}/members`,
      member
    );
  }

  public async updateFamilyMember(
    member: UpdateFamilyMemberDto
  ): Promise<FamilyMember> {
    return this.apiClient.put<FamilyMember>(
      `${this.BASE_URL}/members/${member.id}`,
      member
    );
  }

  public async deleteFamilyMember(id: string): Promise<void> {
    return this.apiClient.delete(`${this.BASE_URL}/members/${id}`);
  }
}
