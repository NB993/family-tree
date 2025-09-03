import React from 'react';
import * as Avatar from '@radix-ui/react-avatar';
import { FindFamilyJoinRequestResponse, FamilyJoinRequestStatus } from '../../../api/services/familyService';

interface FamilyJoinRequestCardProps {
  request: FindFamilyJoinRequestResponse;
  onApprove?: (requestId: number, message?: string) => void;
  onReject?: (requestId: number, message?: string) => void;
}

const statusLabels = {
  PENDING: '대기 중',
  APPROVED: '승인됨',
  REJECTED: '거절됨',
};

const statusColors = {
  PENDING: 'bg-amber-100 text-amber-800',
  APPROVED: 'bg-green-100 text-green-800', 
  REJECTED: 'bg-red-100 text-red-800',
};

export const FamilyJoinRequestCard: React.FC<FamilyJoinRequestCardProps> = ({
  request,
  onApprove,
  onReject,
}) => {
  const [approveMessage, setApproveMessage] = React.useState('');
  const [rejectMessage, setRejectMessage] = React.useState('');
  const [showApproveInput, setShowApproveInput] = React.useState(false);
  const [showRejectInput, setShowRejectInput] = React.useState(false);

  const handleApprove = () => {
    if (showApproveInput) {
      onApprove?.(request.id, approveMessage || undefined);
      setApproveMessage('');
      setShowApproveInput(false);
    } else {
      setShowApproveInput(true);
    }
  };

  const handleReject = () => {
    if (showRejectInput) {
      onReject?.(request.id, rejectMessage || undefined);
      setRejectMessage('');
      setShowRejectInput(false);
    } else {
      setShowRejectInput(true);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="family-card mb-4">
      <div className="flex items-start gap-4">
        {/* 프로필 이미지 */}
        <Avatar.Root className="inline-flex h-12 w-12 select-none items-center justify-center overflow-hidden rounded-full bg-orange-100">
          <Avatar.Fallback className="leading-1 flex h-full w-full items-center justify-center bg-gradient-to-br from-orange-400 to-amber-500 text-[15px] font-medium text-white">
            {request.requesterName.charAt(0)}
          </Avatar.Fallback>
        </Avatar.Root>

        {/* 요청자 정보 */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between mb-2">
            <div>
              <h3 className="text-lg font-medium text-gray-900 truncate">
                {request.requesterName}
              </h3>
              {request.requesterEmail && (
                <p className="text-sm text-gray-500 truncate">
                  {request.requesterEmail}
                </p>
              )}
            </div>
            <span className={`px-2 py-1 text-xs font-medium rounded-full ${statusColors[request.status]}`}>
              {statusLabels[request.status]}
            </span>
          </div>

          {/* 요청 메시지 */}
          {request.requestMessage && (
            <div className="mb-3">
              <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
                {request.requestMessage}
              </p>
            </div>
          )}

          {/* 응답 메시지 */}
          {request.responseMessage && (
            <div className="mb-3">
              <p className="text-sm text-gray-600 bg-blue-50 p-3 rounded-lg border-l-4 border-blue-400">
                <span className="font-medium">응답: </span>
                {request.responseMessage}
              </p>
            </div>
          )}

          {/* 날짜 정보 */}
          <div className="text-xs text-gray-500 mb-3">
            <p>요청일: {formatDate(request.createdAt)}</p>
            {request.processedAt && (
              <p>처리일: {formatDate(request.processedAt)}</p>
            )}
          </div>

          {/* 승인/거절 메시지 입력 */}
          {showApproveInput && (
            <div className="mb-3">
              <textarea
                value={approveMessage}
                onChange={(e) => setApproveMessage(e.target.value)}
                placeholder="승인 메시지를 입력하세요 (선택사항)"
                className="w-full p-3 border border-gray-300 rounded-lg resize-none text-sm"
                rows={3}
              />
            </div>
          )}

          {showRejectInput && (
            <div className="mb-3">
              <textarea
                value={rejectMessage}
                onChange={(e) => setRejectMessage(e.target.value)}
                placeholder="거절 사유를 입력하세요 (선택사항)"
                className="w-full p-3 border border-gray-300 rounded-lg resize-none text-sm"
                rows={3}
              />
            </div>
          )}

          {/* 액션 버튼 */}
          {request.status === 'PENDING' && (
            <div className="flex gap-2">
              <button
                onClick={handleApprove}
                className="family-button-secondary text-sm px-4 py-2"
              >
                {showApproveInput ? '승인 확정' : '승인'}
              </button>
              <button
                onClick={handleReject}
                className="px-4 py-2 text-sm font-medium text-red-600 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
              >
                {showRejectInput ? '거절 확정' : '거절'}
              </button>
              {(showApproveInput || showRejectInput) && (
                <button
                  onClick={() => {
                    setShowApproveInput(false);
                    setShowRejectInput(false);
                    setApproveMessage('');
                    setRejectMessage('');
                  }}
                  className="px-4 py-2 text-sm font-medium text-gray-600 bg-gray-50 border border-gray-200 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  취소
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};