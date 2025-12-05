/**
 * 프론트엔드 로깅 유틸리티
 *
 * 환경별로 로그 출력을 제어합니다.
 * - 개발 환경: debug, error 모두 출력
 * - 프로덕션: error만 출력
 */

const isDev = process.env.NODE_ENV === 'development';

export const logger = {
  /**
   * 디버그 로그 (개발 환경에서만 출력)
   */
  debug: (...args: any[]): void => {
    if (isDev) {
      console.log('[DEBUG]', ...args);
    }
  },

  /**
   * 정보 로그 (개발 환경에서만 출력)
   */
  info: (...args: any[]): void => {
    if (isDev) {
      console.info('[INFO]', ...args);
    }
  },

  /**
   * 경고 로그 (모든 환경에서 출력)
   */
  warn: (...args: any[]): void => {
    console.warn('[WARN]', ...args);
  },

  /**
   * 에러 로그 (모든 환경에서 출력)
   * TODO: 나중에 필요하면 Sentry 연동
   */
  error: (...args: any[]): void => {
    console.error('[ERROR]', ...args);

    // TODO: 프로덕션에서 Sentry로 에러 전송
    // if (!isDev && typeof Sentry !== 'undefined') {
    //   const error = args[0];
    //   if (error instanceof Error) {
    //     Sentry.captureException(error);
    //   }
    // }
  },
};