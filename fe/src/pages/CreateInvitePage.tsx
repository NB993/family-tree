import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { inviteApi, CreateInviteResponse } from '../api/services/inviteService';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { ArrowLeft, Copy, Check, Link } from 'lucide-react';

export const CreateInvitePage: React.FC = () => {
  const navigate = useNavigate();
  const [inviteData, setInviteData] = useState<CreateInviteResponse | null>(null);
  const [copied, setCopied] = useState(false);

  const createInviteMutation = useMutation({
    mutationFn: () => inviteApi.createInvite(),
    onSuccess: (data) => {
      setInviteData(data);
    },
    onError: (error) => {
      console.error('초대 링크 생성 실패:', error);
      alert('초대 링크 생성에 실패했습니다.');
    },
  });

  const handleCopyLink = () => {
    if (inviteData?.inviteUrl) {
      navigator.clipboard.writeText(inviteData.inviteUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleCreateNew = () => {
    setInviteData(null);
    setCopied(false);
  };

  return (
    <div className="app-shell flex flex-col">
      {/* Header */}
      <header className="flex items-center gap-2 px-3 py-2 border-b border-border">
        <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
          <ArrowLeft className="h-3.5 w-3.5" strokeWidth={1.5} />
        </Button>
        <h1 className="text-sm font-medium">초대 링크</h1>
      </header>

      {!inviteData ? (
        <div className="flex-1 flex flex-col p-3">
          <div className="flex-1 flex flex-col items-center justify-center text-center">
            <Link className="w-8 h-8 text-primary mb-2" strokeWidth={1} />
            <h2 className="text-sm font-medium text-foreground">멤버 정보 수집</h2>
            <p className="text-[10px] text-muted-foreground mt-0.5">
              초대 링크로 멤버들의 연락처를 수집합니다
            </p>
            <ul className="text-[10px] text-muted-foreground mt-3 space-y-0.5">
              <li>• 24시간 유효</li>
              <li>• 최대 5명 초대 가능</li>
            </ul>
          </div>
          <Button
            onClick={() => createInviteMutation.mutate()}
            disabled={createInviteMutation.isPending}
            className="w-full"
          >
            {createInviteMutation.isPending ? '생성 중...' : '링크 생성'}
          </Button>
        </div>
      ) : (
        <div className="flex-1 flex flex-col p-3">
          <div className="flex-1 flex flex-col items-center justify-center text-center">
            <Check className="w-8 h-8 text-green-600 mb-2" strokeWidth={1} />
            <h2 className="text-sm font-medium text-foreground">생성 완료</h2>
            <p className="text-[10px] text-muted-foreground mt-0.5">링크를 복사하여 공유하세요</p>
          </div>

          <div className="space-y-2">
            <div className="flex gap-1">
              <Input value={inviteData.inviteUrl} readOnly className="flex-1 text-xs" />
              <Button onClick={handleCopyLink} variant={copied ? "default" : "outline"} size="icon">
                {copied ? <Check className="h-3.5 w-3.5" strokeWidth={1.5} /> : <Copy className="h-3.5 w-3.5" strokeWidth={1.5} />}
              </Button>
            </div>
            {copied && <p className="text-center text-[10px] text-green-600">복사됨</p>}

            <Button onClick={() => navigate('/home')} className="w-full">홈으로</Button>
            <Button onClick={handleCreateNew} variant="outline" className="w-full">새 링크</Button>
          </div>
        </div>
      )}
    </div>
  );
};
