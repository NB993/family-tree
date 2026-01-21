import React, { useState } from 'react';
import { Check, ChevronDown, Filter } from 'lucide-react';
import { useTags } from '@/hooks/queries/useTagQueries';
import { cn } from '@/lib/utils';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Button } from '@/components/ui/button';

interface TagFilterProps {
  familyId: number;
  selectedTagIds: number[];
  onSelectionChange: (tagIds: number[]) => void;
  filteredCount?: number;
  className?: string;
}

export const TagFilter: React.FC<TagFilterProps> = ({
  familyId,
  selectedTagIds,
  onSelectionChange,
  filteredCount,
  className,
}) => {
  const { data: tags, isLoading } = useTags(familyId);
  const [open, setOpen] = useState(false);

  const handleToggle = (tagId: number) => {
    if (selectedTagIds.includes(tagId)) {
      onSelectionChange(selectedTagIds.filter((id) => id !== tagId));
    } else {
      onSelectionChange([...selectedTagIds, tagId]);
    }
  };

  const handleClearSelection = () => {
    onSelectionChange([]);
  };

  // 태그가 없거나 로딩 중이면 렌더링하지 않음
  if (isLoading || !tags || tags.length === 0) {
    return null;
  }

  const hasSelection = selectedTagIds.length > 0;

  return (
    <div className={cn('flex items-center gap-1', className)}>
      <Popover open={open} onOpenChange={setOpen}>
          <PopoverTrigger asChild>
            <Button
              variant="ghost"
              size="sm"
              className={cn(
                'h-7 gap-1 text-xs px-1.5',
                hasSelection && 'text-primary'
              )}
            >
              <Filter className="w-3 h-3" />
              {hasSelection && <span>{selectedTagIds.length}개</span>}
              <ChevronDown className="w-3 h-3 opacity-50" />
            </Button>
          </PopoverTrigger>
          <PopoverContent align="start" className="w-56 p-2">
            {/* 태그 목록 */}
            <div className="flex flex-col gap-1 max-h-64 overflow-y-auto">
              {tags.map((tag) => {
                const isSelected = selectedTagIds.includes(tag.id);
                return (
                  <button
                    key={tag.id}
                    type="button"
                    onClick={() => handleToggle(tag.id)}
                    className={cn(
                      'flex items-center gap-2 px-2 py-1.5 rounded-md text-sm',
                      'hover:bg-secondary transition-colors text-left w-full',
                      isSelected && 'bg-secondary/80'
                    )}
                  >
                    {/* 체크박스 */}
                    <div
                      className={cn(
                        'w-4 h-4 rounded border flex items-center justify-center flex-shrink-0',
                        isSelected
                          ? 'bg-primary border-primary'
                          : 'border-muted-foreground/30'
                      )}
                    >
                      {isSelected && (
                        <Check className="w-3 h-3 text-primary-foreground" />
                      )}
                    </div>
                    {/* 색상 원 */}
                    <span
                      className="w-3 h-3 rounded-full flex-shrink-0"
                      style={{ backgroundColor: tag.color }}
                    />
                    {/* 태그 이름 */}
                    <span className="truncate">{tag.name}</span>
                  </button>
                );
              })}
            </div>

            {/* 선택 취소 */}
            {hasSelection && (
              <>
                <div className="border-t border-border my-2" />
                <button
                  type="button"
                  onClick={handleClearSelection}
                  className="w-full px-2 py-1.5 text-sm text-muted-foreground hover:text-foreground hover:bg-secondary rounded-md transition-colors text-left"
                >
                  선택 취소
                </button>
              </>
            )}
          </PopoverContent>
        </Popover>

      {/* 필터 상태 표시 */}
      {hasSelection && filteredCount !== undefined && (
        <span className="text-xs text-muted-foreground mr-1">
          {filteredCount}명
        </span>
      )}
    </div>
  );
};