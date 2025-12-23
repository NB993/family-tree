import React, { useEffect, useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { logger } from '../utils/logger';

type PageState = 'loading' | 'success' | 'error';

export const InviteCallbackPage: React.FC = () => {
  const { inviteCode } = useParams<{ inviteCode?: string }>();
  const [searchParams] = useSearchParams();
  const [state, setState] = useState<PageState>('loading');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [hasProcessed, setHasProcessed] = useState(false);

  useEffect(() => {
    // 중복 실행 방지 (OAuth2CallbackPage 패턴)
    if (hasProcessed) return;
    setHasProcessed(true);

    const success = searchParams.get('success');
    const error = searchParams.get('error');

    logger.debug('초대 콜백 페이지:', { inviteCode, success, error });

    // 1. 백엔드에서 에러와 함께 리다이렉트한 경우
    if (error) {
      logger.error('초대 수락 실패:', error);
      setErrorMessage(decodeURIComponent(error));
      setState('error');
      return;
    }

    // 2. 백엔드에서 성공 리다이렉트한 경우
    if (success === 'true') {
      logger.info('초대 수락 완료');
      setState('success');
      return;
    }

    // 3. 파라미터 없이 직접 접근한 경우
    logger.warn('잘못된 접근: success/error 파라미터 없음');
    setErrorMessage('잘못된 접근입니다.');
    setState('error');
  }, [searchParams, inviteCode, hasProcessed]);

  // 로딩 중
  if (state === 'loading') {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        flexDirection: 'column',
        gap: '16px'
      }}>
        <div style={{
          width: '48px',
          height: '48px',
          border: '4px solid #f3f3f3',
          borderTop: '4px solid #007bff',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }} />
        <div style={{ fontSize: '16px', color: '#666' }}>
          초대를 처리하고 있습니다...
        </div>
        <style>{`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  // 성공
  if (state === 'success') {
    return (
      <div>
        <div style={{
          padding: '16px',
          borderBottom: '1px solid #e0e0e0',
          display: 'flex',
          alignItems: 'center'
        }}>
          <h1 style={{ margin: 0, fontSize: '20px' }}>초대 응답 완료</h1>
        </div>

        <div style={{ padding: '20px' }}>
          <div style={{
            padding: '32px 24px',
            border: '1px solid #e0e0e0',
            borderRadius: '8px',
            background: '#fff'
          }}>
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '24px',
              alignItems: 'center',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '64px' }}>✅</div>

              <h2 style={{ margin: 0, color: '#28a745', fontSize: '24px' }}>
                정보 제공이 완료되었습니다!
              </h2>

              <p style={{ color: '#666', margin: 0, lineHeight: '1.6' }}>
                멤버로 등록되었습니다.<br />
                감사합니다!
              </p>

              <div style={{
                width: '100%',
                padding: '16px',
                background: '#f8f9fa',
                borderRadius: '8px',
                marginTop: '8px'
              }}>
                <p style={{
                  fontSize: '14px',
                  color: '#666',
                  margin: 0,
                  lineHeight: '1.6'
                }}>
                  💡 이 창을 닫으셔도 됩니다.<br />
                  초대를 보낸 분이 여러분의 정보를 확인할 수 있습니다.
                </p>
              </div>
            </div>
          </div>

          {/* 서비스 소개 (선택적) */}
          <div style={{
            marginTop: '24px',
            padding: '20px',
            background: '#f8f9fa',
            borderRadius: '8px',
            border: '1px solid #e9ecef'
          }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <span style={{ fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
                📱 오래오래
              </span>
              <p style={{ fontSize: '14px', color: '#666', margin: 0, lineHeight: '1.6' }}>
                소중한 사람들의 연락처를 기록하고 공유하는 서비스입니다.<br />
                관심이 있으시다면 회원가입 후 이용해보세요!
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // 에러
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
        <div style={{
          padding: '32px 24px',
          border: '1px solid #e0e0e0',
          borderRadius: '8px',
          background: '#fff'
        }}>
          <div style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '24px',
            alignItems: 'center',
            textAlign: 'center'
          }}>
            <div style={{ fontSize: '64px' }}>❌</div>

            <h2 style={{ margin: 0, color: '#dc3545', fontSize: '24px' }}>
              초대 수락에 실패했습니다
            </h2>

            <div style={{
              width: '100%',
              padding: '16px',
              background: '#fff3cd',
              border: '1px solid #ffc107',
              borderRadius: '8px'
            }}>
              <p style={{
                fontSize: '14px',
                color: '#856404',
                margin: 0
              }}>
                {errorMessage}
              </p>
            </div>

            <div style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '12px',
              width: '100%',
              marginTop: '8px'
            }}>
              <button
                onClick={() => window.location.href = `/invite/${inviteCode}`}
                style={{
                  padding: '12px 24px',
                  fontSize: '16px',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontWeight: '600'
                }}
              >
                다시 시도
              </button>

              <p style={{
                fontSize: '12px',
                color: '#999',
                margin: 0
              }}>
                문제가 계속되면 초대를 보낸 분에게 문의해주세요.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
