/**
 * 색상 팔레트 선택 컴포넌트
 * PRD-006: FamilyMember 태그 기능
 */

import React from 'react';
import { Check } from 'lucide-react';
import { TAG_COLOR_PALETTE, TagColor } from '../../types/tag';
import { cn } from '../../lib/utils';

interface ColorPickerProps {
  selectedColor: string;
  onColorSelect: (color: TagColor) => void;
  className?: string;
}

export const ColorPicker: React.FC<ColorPickerProps> = ({
  selectedColor,
  onColorSelect,
  className,
}) => {
  return (
    <div className={cn('flex flex-wrap gap-2', className)}>
      {TAG_COLOR_PALETTE.map((color) => (
        <button
          key={color}
          type="button"
          onClick={() => onColorSelect(color)}
          className={cn(
            'w-6 h-6 rounded-full flex items-center justify-center transition-transform hover:scale-110',
            selectedColor === color && 'ring-2 ring-offset-2 ring-gray-400'
          )}
          style={{ backgroundColor: color }}
        >
          {selectedColor === color && (
            <Check className="h-3 w-3 text-gray-600" />
          )}
        </button>
      ))}
    </div>
  );
};