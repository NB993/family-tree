/**
 * 디자인 시스템 메인 export 파일
 */

export * from './tokens';
export * from './utils';

// 타입 정의
export type ColorScheme = 'light' | 'dark';
export type Size = 'sm' | 'md' | 'lg';
export type Variant = 'primary' | 'secondary' | 'ghost' | 'outline';
export type Status = 'default' | 'success' | 'warning' | 'error' | 'info';

// 디자인 시스템 설정
export const designSystemConfig = {
  // 기본 테마
  defaultColorScheme: 'light' as ColorScheme,
  
  // 모바일 퍼스트 설정
  mobileFirst: true,
  maxContainerWidth: '390px',
  
  // 애니메이션 설정
  enableAnimations: true,
  reducedMotion: false,
  
  // 접근성 설정
  focusVisible: true,
  highContrast: false,
  
  // 로케일 설정
  locale: 'ko-KR',
  
  // 폰트 로딩 설정
  fontDisplay: 'swap' as const,
} as const;