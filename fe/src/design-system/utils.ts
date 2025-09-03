/**
 * 디자인 시스템 유틸리티 함수들
 */

import { colors, spacing, typography, borderRadius, shadows, transitions } from './tokens';

/**
 * 클래스명을 결합합니다.
 */
export function cn(...classes: (string | undefined | null | false)[]): string {
  return classes.filter(Boolean).join(' ');
}

/**
 * 색상 값을 투명도와 함께 반환합니다.
 */
export function alpha(color: string, opacity: number): string {
  // HEX to RGB 변환
  const hex = color.replace('#', '');
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);
  
  return `rgba(${r}, ${g}, ${b}, ${opacity})`;
}

/**
 * rem 단위로 변환합니다.
 */
export function rem(px: number): string {
  return `${px / 16}rem`;
}

/**
 * 스페이싱 값을 반환합니다.
 */
export function space(value: keyof typeof spacing): string {
  return spacing[value];
}

/**
 * 미디어 쿼리를 생성합니다.
 */
export const media = {
  mobile: (styles: string) => `
    @media (max-width: 767px) {
      ${styles}
    }
  `,
  tablet: (styles: string) => `
    @media (min-width: 768px) and (max-width: 1023px) {
      ${styles}
    }
  `,
  desktop: (styles: string) => `
    @media (min-width: 1024px) {
      ${styles}
    }
  `,
} as const;

/**
 * 포커스 스타일을 생성합니다.
 */
export function focusRing(color: string = colors.primary[500]): string {
  return `
    outline: none;
    box-shadow: 0 0 0 2px ${alpha(color, 0.2)}, 0 0 0 4px ${alpha(color, 0.1)};
  `;
}

/**
 * 텍스트 생략 스타일을 생성합니다.
 */
export function truncate(lines: number = 1): string {
  if (lines === 1) {
    return `
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    `;
  }
  
  return `
    display: -webkit-box;
    -webkit-line-clamp: ${lines};
    -webkit-box-orient: vertical;
    overflow: hidden;
  `;
}

/**
 * 그라데이션을 생성합니다.
 */
export function gradient(from: string, to: string, direction: string = 'to right'): string {
  return `linear-gradient(${direction}, ${from}, ${to})`;
}

/**
 * 애니메이션 클래스를 생성합니다.
 */
export const animation = {
  fadeIn: `
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
    animation: fadeIn 300ms ease-out;
  `,
  slideUp: `
    @keyframes slideUp {
      from { transform: translateY(10px); opacity: 0; }
      to { transform: translateY(0); opacity: 1; }
    }
    animation: slideUp 300ms ease-out;
  `,
  slideDown: `
    @keyframes slideDown {
      from { transform: translateY(-10px); opacity: 0; }
      to { transform: translateY(0); opacity: 1; }
    }
    animation: slideDown 300ms ease-out;
  `,
  scaleIn: `
    @keyframes scaleIn {
      from { transform: scale(0.95); opacity: 0; }
      to { transform: scale(1); opacity: 1; }
    }
    animation: scaleIn 300ms ease-out;
  `,
} as const;

/**
 * 접근성을 위한 스크린 리더 전용 스타일
 */
export const srOnly = `
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
`;

/**
 * 컨테이너 최대 너비 설정
 */
export const container = `
  width: 100%;
  max-width: 390px;
  margin: 0 auto;
  padding: 0 ${spacing[4]};
`;

/**
 * 카드 스타일
 */
export function card(variant: 'default' | 'elevated' | 'outlined' = 'default'): string {
  const baseStyles = `
    background: ${colors.background.primary};
    border-radius: ${borderRadius.lg};
    padding: ${spacing[4]};
  `;
  
  switch (variant) {
    case 'elevated':
      return `${baseStyles} box-shadow: ${shadows.lg};`;
    case 'outlined':
      return `${baseStyles} border: 1px solid ${colors.neutral[200]};`;
    default:
      return `${baseStyles} box-shadow: ${shadows.md};`;
  }
}

/**
 * 버튼 베이스 스타일
 */
export const buttonBase = `
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: ${typography.fontWeight.medium};
  transition: all ${transitions.duration.normal} ${transitions.easing.easeInOut};
  cursor: pointer;
  user-select: none;
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  &:focus-visible {
    ${focusRing()}
  }
`;