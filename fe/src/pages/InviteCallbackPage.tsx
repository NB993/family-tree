import React, { useEffect, useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { logger } from '../utils/logger';
import { CheckCircle, XCircle, AlertCircle } from 'lucide-react';

type PageState = 'loading' | 'success' | 'error';

export const InviteCallbackPage: React.FC = () => {
  const { inviteCode } = useParams<{ inviteCode?: string }>();
  const [searchParams] = useSearchParams();
  const [state, setState] = useState<PageState>('loading');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [hasProcessed, setHasProcessed] = useState(false);

  useEffect(() => {
    if (hasProcessed) return;
    setHasProcessed(true);

    const success = searchParams.get('success');
    const error = searchParams.get('error');

    logger.debug('초대 콜백 페이지:', { inviteCode, success, error });

    if (error) {
      logger.error('초대 수락 실패:', error);
      setErrorMessage(decodeURIComponent(error));
      setState('error');
      return;
    }

    if (success === 'true') {
      logger.info('초대 수락 완료');
      setState('success');
      return;
    }

    logger.warn('잘못된 접근: success/error 파라미터 없음');
    setErrorMessage('잘못된 접근입니다.');
    setState('error');
  }, [searchParams, inviteCode, hasProcessed]);

  if (state === 'loading') {
    return (
      <div className="app-shell flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary border-t-transparent mx-auto" />
          <p className="text-[10px] text-muted-foreground mt-2">처리 중...</p>
        </div>
      </div>
    );
  }

  if (state === 'success') {
    return (
      <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
        <CheckCircle className="w-8 h-8 text-green-600 mb-2" strokeWidth={1.5} />
        <h2 className="text-sm font-medium text-foreground">정보 제공 완료</h2>
        <p className="text-[10px] text-muted-foreground mt-0.5 mb-3">
          멤버로 등록되었습니다. 감사합니다!
        </p>

        <div className="w-full p-2 bg-secondary/50 rounded text-[10px] text-muted-foreground mb-4">
          이 창을 닫으셔도 됩니다
        </div>

        <div className="w-full p-3 bg-muted rounded border border-border">
          <span className="text-xs font-medium text-foreground">오래오래</span>
          <p className="text-[10px] text-muted-foreground mt-0.5">
            소중한 사람들의 연락처를 기록하고 공유하는 서비스입니다
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="app-shell flex flex-col items-center justify-center p-4 text-center">
      <XCircle className="w-8 h-8 text-destructive mb-2" strokeWidth={1.5} />
      <h2 className="text-sm font-medium text-foreground">초대 수락 실패</h2>

      <div className="w-full my-3 p-2 bg-destructive/10 border border-destructive/20 rounded">
        <div className="flex items-center gap-1 justify-center">
          <AlertCircle className="w-3 h-3 text-destructive" strokeWidth={1.5} />
          <span className="text-[10px] text-destructive">{errorMessage}</span>
        </div>
      </div>

      <div className="space-y-2 w-full">
        <Button
          onClick={() => window.location.href = `/invite/${inviteCode}`}
          className="w-full"
          size="sm"
        >
          다시 시도
        </Button>
        <p className="text-[10px] text-muted-foreground">
          문제가 계속되면 초대를 보낸 분에게 문의해주세요
        </p>
      </div>
    </div>
  );
};
