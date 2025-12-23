import React from 'react';
import { useFamilyJoinRequests, useProcessFamilyJoinRequest } from '../../../hooks/queries/useFamilyQueries';
import { FamilyJoinRequestCard } from './FamilyJoinRequestCard';
import { FamilyJoinRequestStatus } from '../../../api/services/familyService';

interface FamilyJoinRequestListProps {
  familyId: number | string;
}

export const FamilyJoinRequestList: React.FC<FamilyJoinRequestListProps> = ({ familyId }) => {
  const { data: joinRequests, isLoading, error } = useFamilyJoinRequests(familyId);
  const processRequest = useProcessFamilyJoinRequest();

  const handleApprove = async (requestId: number, message?: string) => {
    try {
      await processRequest.mutateAsync({
        familyId,
        requestId,
        status: 'APPROVED' as FamilyJoinRequestStatus,
        message,
      });
    } catch (error) {
      console.error('참여 요청 승인 중 오류가 발생했습니다:', error);
    }
  };

  const handleReject = async (requestId: number, message?: string) => {
    try {
      await processRequest.mutateAsync({
        familyId,
        requestId,
        status: 'REJECTED' as FamilyJoinRequestStatus,
        message,
      });
    } catch (error) {
      console.error('참여 요청 거절 중 오류가 발생했습니다:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        {[...Array(3)].map((_, i) => (
          <div key={i} className="family-card animate-pulse">
            <div className="flex items-start gap-4">
              <div className="h-12 w-12 bg-gray-200 rounded-full"></div>
              <div className="flex-1">
                <div className="h-4 bg-gray-200 rounded w-1/3 mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2 mb-2"></div>
                <div className="h-16 bg-gray-200 rounded mb-3"></div>
                <div className="flex gap-2">
                  <div className="h-8 bg-gray-200 rounded w-16"></div>
                  <div className="h-8 bg-gray-200 rounded w-16"></div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="family-card bg-red-50 border-red-200">
        <div className="text-center py-8">
          <p className="text-red-600 mb-2">참여 요청을 불러오는 중 오류가 발생했습니다.</p>
          <p className="text-sm text-red-500">{error.message}</p>
        </div>
      </div>
    );
  }

  if (!joinRequests || joinRequests.length === 0) {
    return (
      <div className="family-card">
        <div className="text-center py-12">
          <div className="text-5xl mb-4">📝</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            참여 요청이 없습니다
          </h3>
          <p className="text-gray-500">
            새로운 멤버의 참여 요청을 기다리고 있습니다.
          </p>
        </div>
      </div>
    );
  }

  // 요청을 상태별로 정렬 (PENDING -> APPROVED -> REJECTED)
  const sortedRequests = [...joinRequests].sort((a, b) => {
    const statusOrder = { PENDING: 0, APPROVED: 1, REJECTED: 2 };
    return statusOrder[a.status] - statusOrder[b.status];
  });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-900">
          참여 요청 관리
        </h2>
        <div className="text-sm text-gray-500">
          총 {joinRequests.length}건
          {joinRequests.filter(r => r.status === 'PENDING').length > 0 && (
            <span className="ml-2 px-2 py-1 bg-amber-100 text-amber-800 rounded-full text-xs font-medium">
              대기 {joinRequests.filter(r => r.status === 'PENDING').length}건
            </span>
          )}
        </div>
      </div>

      {sortedRequests.map((request) => (
        <FamilyJoinRequestCard
          key={request.id}
          request={request}
          onApprove={handleApprove}
          onReject={handleReject}
        />
      ))}
    </div>
  );
};