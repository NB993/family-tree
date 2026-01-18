/**
 * 색상 팔레트 선택 컴포넌트
 * PRD-006: FamilyMember 태그 기능
 */

import React from 'react';
import { Check } from 'lucide-react';
import { TAG_COLOR_PALETTE, TagColor } from '../../types/tag';
import { cn } from '../../lib/utils';

const COLOR_LABELS: Record<TagColor, string> = {
  '#E3E2E0': '회색',
  '#EEE0DA': '갈색',
  '#FADEC9': '주황색',
  '#FDECC8': '노란색',
  '#DBEDDB': '초록색',
  '#D3E5EF': '파란색',
  '#E8DEEE': '보라색',
  '#F5E0E9': '분홍색',
  '#FFE2DD': '빨간색',
};

interface ColorPickerProps {
  selectedColor: TagColor;
  onColorSelect: (color: TagColor) => void;
  className?: string;
}

export const ColorPicker: React.FC<ColorPickerProps> = ({
  selectedColor,
  onColorSelect,
  className,
}) => {
  return (
    <div className={cn('flex flex-wrap gap-2', className)} role="radiogroup" aria-label="색상 선택">
      {TAG_COLOR_PALETTE.map((color) => (
        <button
          key={color}
          type="button"
          role="radio"
          aria-checked={selectedColor === color}
          aria-label={`${COLOR_LABELS[color]} 선택`}
          onClick={() => onColorSelect(color)}
          className={cn(
            'w-6 h-6 rounded-full flex items-center justify-center transition-transform hover:scale-110',
            selectedColor === color && 'ring-2 ring-offset-2 ring-gray-400'
          )}
          style={{ backgroundColor: color }}
        >
          {selectedColor === color && (
            <Check className="h-3 w-3 text-gray-600" aria-hidden="true" />
          )}
        </button>
      ))}
    </div>
  );
};