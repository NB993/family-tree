import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { inviteApi } from '../api/services/inviteService';

export const InviteResponsePage: React.FC = () => {
  const { inviteCode } = useParams<{ inviteCode: string }>();
  const navigate = useNavigate();

  const { data: inviteInfo, isLoading, error } = useQuery({
    queryKey: ['invite', inviteCode],
    queryFn: () => inviteApi.getInviteInfo(inviteCode!),
    enabled: !!inviteCode,
    retry: false,
  });

  const handleKakaoLogin = () => {
    // 카카오 OAuth 로그인으로 리다이렉트
    const redirectUrl = `${window.location.origin}/invite/${inviteCode}/callback`;
    const kakaoAuthUrl = `/api/oauth2/authorization/kakao?redirect_uri=${encodeURIComponent(redirectUrl)}`;
    window.location.href = kakaoAuthUrl;
  };

  if (isLoading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh' 
      }}>
        <div>로딩 중...</div>
      </div>
    );
  }

  if (error || !inviteInfo) {
    return (
      <div>
        <div style={{ 
          padding: '16px', 
          borderBottom: '1px solid #e0e0e0',
          display: 'flex',
          alignItems: 'center'
        }}>
          <h1 style={{ margin: 0, fontSize: '20px' }}>초대 응답</h1>
        </div>
        <div style={{ padding: '20px' }}>
          <div style={{ padding: '24px', border: '1px solid #e0e0e0', borderRadius: '8px', background: '#fff' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', alignItems: 'center' }}>
              <h2 style={{ margin: 0, color: '#666' }}>
                유효하지 않은 초대 링크입니다
              </h2>
              <p style={{ textAlign: 'center', color: '#666', margin: 0 }}>
                초대 링크가 만료되었거나 존재하지 않습니다.<br />
                초대를 보낸 사람에게 새로운 링크를 요청해주세요.
              </p>
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
                onClick={() => navigate('/')}
              >
                홈으로 이동
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (inviteInfo.status === 'EXPIRED') {
    return (
      <div>
        <div style={{ 
          padding: '16px', 
          borderBottom: '1px solid #e0e0e0',
          display: 'flex',
          alignItems: 'center'
        }}>
          <h1 style={{ margin: 0, fontSize: '20px' }}>초대 응답</h1>
        </div>
        <div style={{ padding: '20px' }}>
          <div style={{ padding: '24px', border: '1px solid #e0e0e0', borderRadius: '8px', background: '#fff' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', alignItems: 'center' }}>
              <h2 style={{ margin: 0, color: '#666' }}>
                만료된 초대 링크입니다
              </h2>
              <p style={{ textAlign: 'center', color: '#666', margin: 0 }}>
                이 초대 링크는 만료되었습니다.<br />
                초대를 보낸 사람에게 새로운 링크를 요청해주세요.
              </p>
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
                onClick={() => navigate('/')}
              >
                홈으로 이동
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (inviteInfo.status === 'COMPLETED') {
    return (
      <div>
        <div style={{ 
          padding: '16px', 
          borderBottom: '1px solid #e0e0e0',
          display: 'flex',
          alignItems: 'center'
        }}>
          <h1 style={{ margin: 0, fontSize: '20px' }}>초대 응답</h1>
        </div>
        <div style={{ padding: '20px' }}>
          <div style={{ padding: '24px', border: '1px solid #e0e0e0', borderRadius: '8px', background: '#fff' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', alignItems: 'center' }}>
              <h2 style={{ margin: 0, color: '#007bff' }}>
                이미 응답 완료된 초대입니다
              </h2>
              <p style={{ textAlign: 'center', color: '#666', margin: 0 }}>
                이 초대에 대한 응답이 이미 제출되었습니다.<br />
                감사합니다!
              </p>
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
                onClick={() => navigate('/')}
              >
                홈으로 이동
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div style={{ 
        padding: '16px', 
        borderBottom: '1px solid #e0e0e0',
        display: 'flex',
        alignItems: 'center'
      }}>
        <h1 style={{ margin: 0, fontSize: '20px' }}>가족 정보 제공</h1>
      </div>
      
      <div style={{ padding: '20px' }}>
        <div style={{ padding: '24px', border: '1px solid #e0e0e0', borderRadius: '8px', background: '#fff' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', alignItems: 'center' }}>
              <h1 style={{ margin: 0, fontSize: '28px', color: '#007bff' }}>
                안녕하세요! 👋
              </h1>
              <p style={{ textAlign: 'center', color: '#333', margin: 0 }}>
                가족 구성원 정보 수집을 위한 초대를 받으셨습니다.
              </p>
              <p style={{ textAlign: 'center', color: '#666', fontSize: '14px', margin: 0 }}>
                아래 버튼을 눌러 간단한 정보를 제공해주세요.
              </p>
            </div>

            <div style={{ 
              background: '#f8f9fa', 
              padding: '16px', 
              borderRadius: '8px',
              border: '1px solid #e9ecef'
            }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <span style={{ fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
                  📋 수집되는 정보
                </span>
                <span style={{ fontSize: '14px', color: '#666' }}>
                  • 이름<br />
                  • 프로필 사진 (카카오 프로필)<br />
                  • 생년월일 (선택)<br />
                  • 연락처 (선택)<br />
                  • 가족 관계 (선택)
                </span>
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
                  🔒 개인정보 보호
                </span>
                <span style={{ fontSize: '14px', color: '#666' }}>
                  제공하신 정보는 가족 트리 생성 목적으로만 사용되며,<br />
                  안전하게 보호됩니다.
                </span>
              </div>
            </div>

            <button
              onClick={handleKakaoLogin}
              style={{
                padding: '14px 28px',
                fontSize: '16px',
                background: '#FEE500',
                color: '#000000',
                border: 'none',
                borderRadius: '4px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
                fontWeight: '600',
                cursor: 'pointer',
                width: '100%'
              }}
            >
              카카오 로그인으로 시작하기
            </button>

            <p style={{ fontSize: '12px', color: '#999', textAlign: 'center', margin: 0 }}>
              카카오 로그인을 통해 안전하게 정보를 제공할 수 있습니다.
            </p>
          </div>
        </div>

        <div style={{ marginTop: '20px', padding: '16px', background: '#f8f9fa', borderRadius: '8px' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <span style={{ fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
              ❓ 도움이 필요하신가요?
            </span>
            <span style={{ fontSize: '14px', color: '#666' }}>
              초대를 보낸 가족에게 문의해주세요.
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};