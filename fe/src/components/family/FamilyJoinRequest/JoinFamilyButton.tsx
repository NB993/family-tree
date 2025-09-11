import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateFamilyJoinRequest } from '../../../hooks/queries/useFamilyQueries';
import { useAuth } from '../../../contexts/AuthContext';

interface JoinFamilyButtonProps {
  familyId: number | string;
  familyName?: string;
  disabled?: boolean;
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}

export const JoinFamilyButton: React.FC<JoinFamilyButtonProps> = ({
  familyId,
  familyName,
  disabled = false,
  onSuccess,
  onError,
}) => {
  const [isRequesting, setIsRequesting] = useState(false);
  const createJoinRequest = useCreateFamilyJoinRequest();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleJoinRequest = async () => {
    if (disabled || isRequesting) return;

    // 게스트인 경우 로그인 페이지로 이동
    if (!isAuthenticated) {
      const shouldLogin = window.confirm(
        '가족 가입 요청을 하려면 로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?'
      );
      if (shouldLogin) {
        navigate('/login', { state: { from: `/families/${familyId}` } });
      }
      return;
    }

    const confirm = window.confirm(
      familyName 
        ? `"${familyName}" 가족에 가입 요청을 보내시겠습니까?`
        : '이 가족에 가입 요청을 보내시겠습니까?'
    );

    if (!confirm) return;

    setIsRequesting(true);
    
    try {
      await createJoinRequest.mutateAsync(familyId);
      onSuccess?.();
      alert('가입 요청이 성공적으로 전송되었습니다. 관리자의 승인을 기다려주세요.');
    } catch (error) {
      console.error('가입 요청 중 오류가 발생했습니다:', error);
      onError?.(error as Error);
      alert('가입 요청 중 오류가 발생했습니다. 다시 시도해주세요.');
    } finally {
      setIsRequesting(false);
    }
  };

  return (
    <button
      onClick={handleJoinRequest}
      disabled={disabled || isRequesting || createJoinRequest.isPending}
      className={`
        w-full px-4 py-3 rounded-xl font-medium text-sm transition-all duration-200
        ${disabled || isRequesting || createJoinRequest.isPending
          ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
          : 'family-button-primary hover:scale-[1.02] active:scale-[0.98]'
        }
      `}
    >
      {isRequesting || createJoinRequest.isPending ? (
        <div className="flex items-center justify-center gap-2">
          <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          요청 중...
        </div>
      ) : (
        '가족 가입 요청'
      )}
    </button>
  );
};