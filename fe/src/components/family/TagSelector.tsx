/**
 * 태그 선택/관리 팝오버 컴포넌트
 * PRD-006: FamilyMember 태그 기능
 * 노션 스타일 인라인 태그 관리
 */

import React, { useState } from 'react';
import { Plus, Check, MoreHorizontal, Pencil, Trash2, Loader2 } from 'lucide-react';
import { Popover, PopoverContent, PopoverTrigger } from '../ui/popover';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '../ui/alert-dialog';
import { ColorPicker } from './ColorPicker';
import { TagBadge } from './TagBadge';
import { useTags, useSaveTag, useModifyTag, useDeleteTag, useModifyMemberTags } from '../../hooks/queries/useTagQueries';
import { Tag, TagSimple, TAG_COLOR_PALETTE, TagColor } from '../../types/tag';
import { useToast } from '../../hooks/use-toast';

interface TagSelectorProps {
  familyId: number | string;
  memberId: number | string;
  memberTags: TagSimple[];
  onTagsChange?: (tags: TagSimple[]) => void;
  children: React.ReactNode;
}

export const TagSelector: React.FC<TagSelectorProps> = ({
  familyId,
  memberId,
  memberTags,
  onTagsChange,
  children,
}) => {
  const { toast } = useToast();
  const { data: tagData, isLoading } = useTags(familyId);
  const saveTagMutation = useSaveTag();
  const modifyTagMutation = useModifyTag();
  const deleteTagMutation = useDeleteTag();
  const modifyMemberTagsMutation = useModifyMemberTags();

  const [isOpen, setIsOpen] = useState(false);
  const [mode, setMode] = useState<'select' | 'create' | 'edit'>('select');

  // 새 태그 생성
  const [newTagName, setNewTagName] = useState('');

  // 태그 수정
  const [editingTag, setEditingTag] = useState<Tag | null>(null);
  const [editTagName, setEditTagName] = useState('');
  const [editTagColor, setEditTagColor] = useState<TagColor>(TAG_COLOR_PALETTE[0]);

  // 삭제 확인
  const [deletingTag, setDeletingTag] = useState<Tag | null>(null);

  const allTags = tagData ?? [];
  const totalCount = allTags.length;
  const maxCount = 10; // 백엔드에서 정의한 최대 태그 수
  const selectedTagIds = new Set(memberTags.map(t => t.id));

  // 태그 선택/해제 토글
  const handleToggleTag = async (tag: Tag) => {
    const newTagIds = selectedTagIds.has(tag.id)
      ? memberTags.filter(t => t.id !== tag.id).map(t => t.id)
      : [...memberTags.map(t => t.id), tag.id];

    try {
      const result = await modifyMemberTagsMutation.mutateAsync({
        familyId,
        memberId,
        tagIds: newTagIds,
      });
      onTagsChange?.(result.tags);
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 변경에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  // 새 태그 생성
  const handleCreateTag = async () => {
    if (!newTagName.trim()) return;

    try {
      const newTag = await saveTagMutation.mutateAsync({
        familyId,
        request: { name: newTagName.trim() },
      });
      toast({ title: '태그가 생성되었습니다.' });
      setNewTagName('');
      setMode('select');

      // 생성 후 바로 선택
      const newTagIds = [...memberTags.map(t => t.id), newTag.id];
      const result = await modifyMemberTagsMutation.mutateAsync({
        familyId,
        memberId,
        tagIds: newTagIds,
      });
      onTagsChange?.(result.tags);
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 생성에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  // 수정 모드 진입
  const openEditMode = (tag: Tag) => {
    setEditingTag(tag);
    setEditTagName(tag.name);
    setEditTagColor(tag.color as TagColor);
    setMode('edit');
  };

  // 태그 수정
  const handleEditTag = async () => {
    if (!editingTag || !editTagName.trim()) return;

    try {
      await modifyTagMutation.mutateAsync({
        familyId,
        tagId: editingTag.id,
        request: {
          name: editTagName.trim(),
          color: editTagColor,
        },
      });
      toast({ title: '태그가 수정되었습니다.' });
      setEditingTag(null);
      setMode('select');
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 수정에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  // 태그 삭제
  const handleDeleteTag = async () => {
    if (!deletingTag) return;

    try {
      await deleteTagMutation.mutateAsync({
        familyId,
        tagId: deletingTag.id,
      });
      toast({ title: '태그가 삭제되었습니다.' });
      setDeletingTag(null);

      // 삭제된 태그가 선택되어 있었다면 제거
      if (selectedTagIds.has(deletingTag.id)) {
        const newTags = memberTags.filter(t => t.id !== deletingTag.id);
        onTagsChange?.(newTags);
      }
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 삭제에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  const resetAndClose = () => {
    setMode('select');
    setNewTagName('');
    setEditingTag(null);
    setIsOpen(false);
  };

  return (
    <>
      <Popover open={isOpen} onOpenChange={(open) => {
        if (!open) resetAndClose();
        else setIsOpen(true);
      }}>
        <PopoverTrigger asChild>
          {children}
        </PopoverTrigger>
        <PopoverContent className="w-64 p-0" align="start">
          {isLoading ? (
            <div className="flex items-center justify-center py-8">
              <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
            </div>
          ) : mode === 'select' ? (
            // 태그 선택 모드
            <div className="py-1">
              <div className="px-3 py-2 border-b">
                <p className="text-xs font-medium text-muted-foreground">
                  태그 ({totalCount}/{maxCount})
                </p>
              </div>

              {allTags.length === 0 ? (
                <div className="px-3 py-4 text-center text-sm text-muted-foreground">
                  태그가 없습니다
                </div>
              ) : (
                <div className="max-h-48 overflow-auto">
                  {allTags.map((tag) => (
                    <div
                      key={tag.id}
                      className="flex items-center gap-2 px-3 py-1.5 hover:bg-secondary/50 cursor-pointer group"
                    >
                      <button
                        className="flex-1 flex items-center gap-2 text-left"
                        onClick={() => handleToggleTag(tag)}
                      >
                        <div
                          className="w-3 h-3 rounded-sm flex-shrink-0"
                          style={{ backgroundColor: tag.color }}
                        />
                        <span className="text-sm truncate flex-1">{tag.name}</span>
                        {selectedTagIds.has(tag.id) && (
                          <Check className="h-4 w-4 text-primary flex-shrink-0" />
                        )}
                      </button>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="h-6 w-6 p-0 opacity-0 group-hover:opacity-100"
                          >
                            <MoreHorizontal className="h-3 w-3" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => openEditMode(tag)}>
                            <Pencil className="h-3 w-3 mr-2" />
                            수정
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            onClick={() => setDeletingTag(tag)}
                            className="text-destructive"
                          >
                            <Trash2 className="h-3 w-3 mr-2" />
                            삭제
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  ))}
                </div>
              )}

              {totalCount < maxCount && (
                <div className="border-t">
                  <button
                    className="w-full flex items-center gap-2 px-3 py-2 text-sm text-muted-foreground hover:bg-secondary/50"
                    onClick={() => setMode('create')}
                  >
                    <Plus className="h-4 w-4" />
                    새 태그 만들기
                  </button>
                </div>
              )}
            </div>
          ) : mode === 'create' ? (
            // 새 태그 생성 모드
            <div className="p-3 space-y-3">
              <div className="flex items-center gap-2">
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-6 px-2"
                  onClick={() => setMode('select')}
                >
                  취소
                </Button>
                <span className="text-sm font-medium">새 태그</span>
              </div>
              <Input
                placeholder="태그 이름 (최대 10자)"
                maxLength={10}
                value={newTagName}
                onChange={(e) => setNewTagName(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && newTagName.trim()) {
                    handleCreateTag();
                  }
                }}
                autoFocus
              />
              <Button
                size="sm"
                className="w-full"
                onClick={handleCreateTag}
                disabled={!newTagName.trim() || saveTagMutation.isPending}
              >
                {saveTagMutation.isPending && (
                  <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                )}
                만들기
              </Button>
            </div>
          ) : (
            // 태그 수정 모드
            <div className="p-3 space-y-3">
              <div className="flex items-center gap-2">
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-6 px-2"
                  onClick={() => setMode('select')}
                >
                  취소
                </Button>
                <span className="text-sm font-medium">태그 수정</span>
              </div>
              <Input
                placeholder="태그 이름 (최대 10자)"
                maxLength={10}
                value={editTagName}
                onChange={(e) => setEditTagName(e.target.value)}
                autoFocus
              />
              <div>
                <label className="text-xs text-muted-foreground">색상</label>
                <div className="mt-1">
                  <ColorPicker
                    selectedColor={editTagColor}
                    onColorSelect={setEditTagColor}
                  />
                </div>
              </div>
              <div>
                <label className="text-xs text-muted-foreground">미리보기</label>
                <div className="mt-1">
                  <TagBadge
                    name={editTagName || '태그 이름'}
                    color={editTagColor}
                    size="md"
                  />
                </div>
              </div>
              <Button
                size="sm"
                className="w-full"
                onClick={handleEditTag}
                disabled={!editTagName.trim() || modifyTagMutation.isPending}
              >
                {modifyTagMutation.isPending && (
                  <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                )}
                저장
              </Button>
            </div>
          )}
        </PopoverContent>
      </Popover>

      {/* 삭제 확인 다이얼로그 */}
      <AlertDialog open={!!deletingTag} onOpenChange={(open) => !open && setDeletingTag(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>태그를 삭제하시겠습니까?</AlertDialogTitle>
            <AlertDialogDescription>
              "{deletingTag?.name}" 태그를 삭제하면 모든 구성원에게서 이 태그가 제거됩니다.
              이 작업은 되돌릴 수 없습니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteTag}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {deleteTagMutation.isPending && (
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
              )}
              삭제
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
};