/**
 * 태그 뱃지 컴포넌트
 * PRD-006: FamilyMember 태그 기능
 */

import React from 'react';
import { X } from 'lucide-react';
import { cn } from '../../lib/utils';

interface TagBadgeProps {
  name: string;
  color: string;
  size?: 'sm' | 'md';
  onRemove?: () => void;
  onClick?: () => void;
  className?: string;
}

/**
 * 배경색의 밝기를 계산하여 텍스트 색상 결정
 */
function getContrastTextColor(hexColor: string): string {
  // HEX를 RGB로 변환
  const hex = hexColor.replace('#', '');
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);

  // 밝기 계산 (YIQ 공식)
  const brightness = (r * 299 + g * 587 + b * 114) / 1000;

  // 밝은 배경에는 어두운 텍스트, 어두운 배경에는 밝은 텍스트
  return brightness > 128 ? '#374151' : '#ffffff';
}

export const TagBadge: React.FC<TagBadgeProps> = ({
  name,
  color,
  size = 'sm',
  onRemove,
  onClick,
  className,
}) => {
  const textColor = getContrastTextColor(color);

  const sizeClasses = {
    sm: 'px-1.5 py-0.5 text-[10px]',
    md: 'px-2 py-1 text-xs',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 rounded-full font-medium transition-opacity',
        sizeClasses[size],
        onClick && 'cursor-pointer hover:opacity-80',
        className
      )}
      style={{ backgroundColor: color, color: textColor }}
      onClick={onClick}
    >
      {name}
      {onRemove && (
        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            onRemove();
          }}
          className="ml-0.5 hover:opacity-70"
          style={{ color: textColor }}
        >
          <X className="h-3 w-3" />
        </button>
      )}
    </span>
  );
};