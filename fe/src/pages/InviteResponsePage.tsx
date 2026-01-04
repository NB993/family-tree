import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { inviteApi } from '../api/services/inviteService';
import { Button } from '@/components/ui/button';
import { UserCheck, Clock, AlertCircle, CheckCircle, Home, Loader2 } from 'lucide-react';

export const InviteResponsePage: React.FC = () => {
  const { inviteCode } = useParams<{ inviteCode: string }>();
  const navigate = useNavigate();
  const [isRedirecting, setIsRedirecting] = useState(false);

  const { data: inviteInfo, isLoading, error } = useQuery({
    queryKey: ['invite', inviteCode],
    queryFn: () => inviteApi.getInviteInfo(inviteCode!),
    enabled: !!inviteCode,
    retry: false,
  });

  const handleKakaoLogin = () => {
    if (isRedirecting) return;
    setIsRedirecting(true);
    const oauthUrl = `${process.env.REACT_APP_API_URL}/oauth2/authorization/kakao?invite_code=${inviteCode}`;
    window.location.href = oauthUrl;
  };

  if (isLoading) {
    return (
      <div className="app-shell flex items-center justify-center">
        <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  // Error/Expired/Completed States
  if (error || !inviteInfo || inviteInfo.status === 'EXPIRED' || inviteInfo.status === 'COMPLETED') {
    const isExpired = inviteInfo?.status === 'EXPIRED';
    const isCompleted = inviteInfo?.status === 'COMPLETED';
    return (
      <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
        {isCompleted ? (
          <CheckCircle className="w-8 h-8 text-green-600 mb-2" strokeWidth={1} />
        ) : isExpired ? (
          <Clock className="w-8 h-8 text-muted-foreground mb-2" strokeWidth={1} />
        ) : (
          <AlertCircle className="w-8 h-8 text-destructive mb-2" strokeWidth={1} />
        )}
        <h2 className="text-sm font-medium text-foreground">
          {isCompleted ? '응답 완료' : isExpired ? '만료된 링크' : '유효하지 않은 링크'}
        </h2>
        <p className="text-[10px] text-muted-foreground mt-0.5 mb-4">
          {isCompleted ? '이미 제출되었습니다' : isExpired ? '새 링크를 요청하세요' : '존재하지 않습니다'}
        </p>
        <Button variant="outline" size="sm" onClick={() => navigate('/')}>
          <Home className="w-3.5 h-3.5" strokeWidth={1.5} /> 홈으로
        </Button>
      </div>
    );
  }

  // Active State
  return (
    <div className="app-shell flex flex-col">
      <div className="flex-1 flex flex-col items-center justify-center p-4 text-center">
        <UserCheck className="w-8 h-8 text-primary mb-2" strokeWidth={1} />
        <h2 className="text-sm font-medium text-foreground">정보 제공 요청</h2>
        <p className="text-[10px] text-muted-foreground mt-0.5">연락처 수집을 위한 초대입니다</p>
        <ul className="text-[10px] text-muted-foreground mt-3 space-y-0.5">
          <li>• 이름, 프로필 사진</li>
          <li>• 생년월일, 연락처 (선택)</li>
        </ul>
      </div>

      <div className="p-3 border-t border-border space-y-2">
        <Button
          onClick={handleKakaoLogin}
          disabled={isRedirecting}
          className="w-full bg-[#FEE500] hover:bg-[#FDD835] text-[#191919] disabled:opacity-70"
        >
          {isRedirecting ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              처리 중...
            </>
          ) : (
            <>
              <svg className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 3C6.477 3 2 6.477 2 11c0 2.89 1.922 5.464 4.811 6.89l-.736 2.698a.3.3 0 00.437.333l3.195-2.11A9.847 9.847 0 0012 19c5.523 0 10-3.477 10-8s-4.477-8-10-8z"/>
              </svg>
              카카오로 정보 제공
            </>
          )}
        </Button>
        <p className="text-[10px] text-center text-muted-foreground">
          정보는 연락처 관리 목적으로만 사용됩니다
        </p>
      </div>
    </div>
  );
};
