import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { inviteApi, CreateInviteResponse } from '../api/services/inviteService';
import { Card, CardContent, CardHeader } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { ArrowLeft, Copy, Info, Lightbulb } from 'lucide-react';

export const CreateInvitePage: React.FC = () => {
  const navigate = useNavigate();
  const [inviteData, setInviteData] = useState<CreateInviteResponse | null>(null);
  const [copied, setCopied] = useState(false);

  const createInviteMutation = useMutation({
    mutationFn: () => inviteApi.createInvite(),
    onSuccess: (data) => {
      setInviteData(data);
      // Using a more subtle notification instead of alert
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

  const renderInitialState = () => (
    <Card>
      <CardHeader>
        <h2 className="text-xl font-bold text-center text-gray-800">
          멤버 정보 수집하기
        </h2>
      </CardHeader>
      <CardContent className="flex flex-col gap-6">
        <p className="text-center text-gray-600">
          초대 링크를 생성하여 멤버들의 연락처를 쉽게 수집할 수 있습니다.
        </p>
        <div className="text-left text-sm text-gray-500 bg-gray-50 p-4 rounded-lg border border-gray-200">
          <ul className="space-y-2">
            <li>• 생성된 링크는 24시간 동안 유효합니다.</li>
            <li>• 링크를 받은 사람은 카카오 로그인을 통해 정보를 제공합니다.</li>
            <li>• 수집된 정보는 연락처 관리에 활용됩니다.</li>
          </ul>
        </div>
        <Button
          onClick={() => createInviteMutation.mutate()}
          loading={createInviteMutation.isPending}
          fullWidth
          size="lg"
        >
          {createInviteMutation.isPending ? '생성 중...' : '초대 링크 생성하기'}
        </Button>
      </CardContent>
    </Card>
  );

  const renderSuccessState = () => (
    <Card>
      <CardHeader>
        <h2 className="text-xl font-bold text-center text-primary-500">
          초대 링크가 생성되었습니다!
        </h2>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
          <label className="text-sm font-medium text-gray-600 mb-2 block">초대 링크</label>
          <div className="flex gap-2">
            <input
              value={inviteData!.inviteUrl}
              readOnly
              className="w-full family-input text-sm flex-1"
            />
            <Button onClick={handleCopyLink} variant="secondary" className="whitespace-nowrap">
              <Copy className="w-4 h-4 mr-2" />
              {copied ? '복사됨!' : '복사하기'}
            </Button>
          </div>
        </div>

        <div className="bg-amber-50 p-4 rounded-lg border border-amber-200 text-amber-800 text-sm">
          <div className="flex items-start gap-3">
            <Info className="w-5 h-5 mt-0.5 flex-shrink-0" />
            <div>
              <h3 className="font-semibold mb-1">안내사항</h3>
              <ul className="space-y-1 list-disc list-inside">
                <li>이 링크는 24시간 동안 유효합니다.</li>
                <li>카카오톡, 메시지 등으로 멤버들에게 공유해주세요.</li>
                <li>응답한 멤버는 자동으로 그룹에 추가됩니다.</li>
              </ul>
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-3 mt-4">
          <Button onClick={() => navigate('/families')} size="lg" fullWidth>
            그룹 목록 보기
          </Button>
          <Button onClick={handleCreateNew} variant="outline" size="lg" fullWidth>
            새 초대 링크 생성하기
          </Button>
        </div>
      </CardContent>
    </Card>
  );

  return (
    <div className="py-6">
      <div className="flex items-center mb-6">
        <Button variant="ghost" size="sm" className="mr-2" onClick={() => navigate(-1)}>
          <ArrowLeft className="w-5 h-5" />
        </Button>
        <h1 className="text-xl font-bold">초대 링크 생성</h1>
      </div>
      
      <div className="space-y-6">
        {!inviteData ? renderInitialState() : renderSuccessState()}

        <div className="bg-gray-100 p-4 rounded-lg text-gray-700 text-sm">
          <div className="flex items-start gap-3">
            <Lightbulb className="w-5 h-5 mt-0.5 text-amber-500 flex-shrink-0" />
            <div>
              <h3 className="font-semibold mb-1">초대 링크 활용 팁</h3>
              <p>모임이나 행사에 링크를 공유하면 더 많은 연락처를 쉽게 수집할 수 있어요!</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
