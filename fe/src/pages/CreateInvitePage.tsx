import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { inviteApi, CreateInviteResponse } from '../api/services/inviteService';

export const CreateInvitePage: React.FC = () => {
  const navigate = useNavigate();
  const [inviteData, setInviteData] = useState<CreateInviteResponse | null>(null);
  const [copied, setCopied] = useState(false);

  const createInviteMutation = useMutation({
    mutationFn: () => inviteApi.createInvite(),
    onSuccess: (data) => {
      setInviteData(data);
      alert('초대 링크가 생성되었습니다!');
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
      alert('링크가 복사되었습니다!');
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleCreateNew = () => {
    setInviteData(null);
    setCopied(false);
  };

  return (
    <div>
      <div style={{ 
        padding: '16px', 
        borderBottom: '1px solid #e0e0e0',
        display: 'flex',
        alignItems: 'center',
        gap: '12px'
      }}>
        <button 
          onClick={() => navigate(-1)}
          style={{
            background: 'none',
            border: 'none',
            fontSize: '20px',
            cursor: 'pointer',
            padding: '4px'
          }}
        >
          ←
        </button>
        <h1 style={{ margin: 0, fontSize: '20px' }}>초대 링크 생성</h1>
      </div>
      
      <div style={{ padding: '20px' }}>
        {!inviteData ? (
          <div style={{ padding: '24px', border: '1px solid #e0e0e0', borderRadius: '8px', background: '#fff' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <h2 style={{ textAlign: 'center', margin: 0 }}>
                가족 구성원 정보 수집하기
              </h2>
              
              <p style={{ textAlign: 'center', color: '#666', margin: 0 }}>
                초대 링크를 생성하여 가족 구성원들의 정보를 쉽게 수집할 수 있습니다.
              </p>

              <div style={{ textAlign: 'center', color: '#999', fontSize: '14px' }}>
                • 생성된 링크는 24시간 동안 유효합니다<br />
                • 링크를 받은 사람은 카카오 로그인을 통해 정보를 제공합니다<br />
                • 수집된 정보는 가족 트리 생성에 활용됩니다
              </div>

              <button
                style={{
                  padding: '12px 24px',
                  fontSize: '16px',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: createInviteMutation.isPending ? 'not-allowed' : 'pointer',
                  opacity: createInviteMutation.isPending ? 0.6 : 1
                }}
                onClick={() => createInviteMutation.mutate()}
                disabled={createInviteMutation.isPending}
              >
                {createInviteMutation.isPending ? '생성 중...' : '초대 링크 생성하기'}
              </button>
            </div>
          </div>
        ) : (
          <div style={{ padding: '24px', border: '1px solid #e0e0e0', borderRadius: '8px', background: '#fff' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <h2 style={{ textAlign: 'center', margin: 0, color: '#007bff' }}>
                초대 링크가 생성되었습니다!
              </h2>

              <div style={{ 
                background: '#f8f9fa', 
                padding: '16px', 
                borderRadius: '8px',
                border: '1px solid #e9ecef'
              }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  <span style={{ fontSize: '14px', color: '#666' }}>초대 링크</span>
                  <div style={{ 
                    display: 'flex', 
                    gap: '8px',
                    alignItems: 'stretch'
                  }}>
                    <input
                      value={inviteData.inviteUrl}
                      readOnly
                      style={{ 
                        flex: 1,
                        fontSize: '14px',
                        fontFamily: 'monospace',
                        padding: '8px',
                        border: '1px solid #ccc',
                        borderRadius: '4px'
                      }}
                    />
                    <button
                      onClick={handleCopyLink}
                      style={{ 
                        padding: '8px 16px',
                        backgroundColor: copied ? '#6c757d' : '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        whiteSpace: 'nowrap'
                      }}
                    >
                      {copied ? '복사됨!' : '복사하기'}
                    </button>
                  </div>
                </div>
              </div>

              <div style={{ 
                background: '#fff9e6', 
                padding: '12px', 
                borderRadius: '8px',
                border: '1px solid #ffe066'
              }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <span style={{ fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
                    📢 안내사항
                  </span>
                  <span style={{ fontSize: '14px', color: '#666' }}>
                    • 이 링크는 24시간 동안 유효합니다<br />
                    • 카카오톡, 메시지 등으로 가족들에게 공유해주세요<br />
                    • 응답한 가족은 자동으로 Family에 추가됩니다
                  </span>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <button
                  style={{
                    padding: '12px 24px',
                    fontSize: '16px',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                  onClick={() => navigate('/families')}
                >
                  Family 목록 보기
                </button>
                <button
                  style={{
                    padding: '12px 24px',
                    fontSize: '16px',
                    backgroundColor: 'transparent',
                    color: '#007bff',
                    border: '1px solid #007bff',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                  onClick={handleCreateNew}
                >
                  새 초대 링크 생성하기
                </button>
              </div>
            </div>
          </div>
        )}

        <div style={{ marginTop: '20px', padding: '16px', background: '#f8f9fa', borderRadius: '8px' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <span style={{ fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
              💡 초대 링크 활용 팁
            </span>
            <span style={{ fontSize: '14px', color: '#666' }}>
              가족 모임이나 명절에 링크를 공유하면 더 많은 정보를 수집할 수 있어요!
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};