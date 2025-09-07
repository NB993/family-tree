import { ApiClient } from '../client';

export interface InviteInfo {
  inviteCode: string;
  requesterId: number;
  expiresAt: string;
  status: 'ACTIVE' | 'EXPIRED' | 'COMPLETED';
  createdAt: string;
}

export interface CreateInviteResponse {
  inviteCode: string;
  inviteUrl: string;
}

export interface MyInvite {
  id: number;
  inviteCode: string;
  expiresAt: string;
  maxUses: number | null;
  usedCount: number;
  status: 'ACTIVE' | 'EXPIRED' | 'COMPLETED';
  createdAt: string;
  modifiedAt: string;
}

class InviteApi {
  private client: ApiClient;

  constructor() {
    this.client = ApiClient.getInstance();
  }

  /**
   * 새로운 초대 링크를 생성합니다.
   */
  async createInvite(): Promise<CreateInviteResponse> {
    return await this.client.post<CreateInviteResponse>('/api/invites');
  }

  /**
   * 초대 코드로 초대 정보를 조회합니다.
   */
  async getInviteInfo(inviteCode: string): Promise<InviteInfo> {
    return await this.client.get<InviteInfo>(`/api/invites/${inviteCode}`);
  }

  /**
   * 내가 생성한 초대 목록을 조회합니다.
   */
  async getMyInvites(): Promise<MyInvite[]> {
    return await this.client.get<MyInvite[]>('/api/invites/my');
  }
}

export const inviteApi = new InviteApi();
