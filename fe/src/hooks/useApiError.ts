import { useCallback } from 'react';
import { ApiError, ErrorHandlers } from '../types/error';

const defaultHandlers: ErrorHandlers = {
  C001: (error) => console.error('파라미터 누락:', error.message),
  C002: (error) => console.error('유효성 검사 실패:', error.message),
  A001: (error) => {
      console.error('인증 실패:', error.message);
      window.location.href = '/login';
  },
  A002: (error) => console.error('권한 없음:', error.message),
};

export const useApiError = (customHandlers?: ErrorHandlers) => {
  const handleError = useCallback((error: ApiError) => {
    const code = error.code;
    const logPrefix = error.traceId ? `[TraceId: ${error.traceId}]` : '';

    // 커스텀 핸들러 확인
    if (customHandlers?.[code]) {
      customHandlers[code](error);
      return;
    }

    // 기본 핸들러 확인
    if (defaultHandlers[code]) {
      defaultHandlers[code](error);
      return;
    }

    // HTTP 상태 코드 기반 처리
    switch (error.status) {
      case 400:
        console.error(`${logPrefix} 잘못된 요청:`, error.message);
        break;
      case 401:
        console.error(`${logPrefix} 인증이 필요합니다.`);
        window.location.href = '/login';
        break;
      case 403:
        console.error(`${logPrefix} 접근 권한이 없습니다.`);
        break;
      case 404:
        console.error(`${logPrefix} 요청한 리소스를 찾을 수 없습니다.`);
        break;
      case 500:
        console.error(`${logPrefix} 서버 오류가 발생했습니다.`);
        break;
      default:
        console.error(`${logPrefix} 알 수 없는 오류가 발생했습니다.`);
    }
  }, [customHandlers]);

  return { handleError };
}; 
