/**
 * 태그 관리 화면 컴포넌트
 * PRD-006: FamilyMember 태그 기능
 */

import React, { useState } from 'react';
import { Plus, MoreHorizontal, Pencil, Trash2, Loader2 } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '../ui/dialog';
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
import { TagBadge } from './TagBadge';
import { ColorPicker } from './ColorPicker';
import { useTags, useSaveTag, useModifyTag, useDeleteTag } from '../../hooks/queries/useTagQueries';
import { Tag, TAG_COLOR_PALETTE, TagColor } from '../../types/tag';
import { useToast } from '../../hooks/use-toast';

interface TagManagementProps {
  familyId: number | string;
}

export const TagManagement: React.FC<TagManagementProps> = ({ familyId }) => {
  const { toast } = useToast();
  const { data: tagData, isLoading } = useTags(familyId);
  const saveTagMutation = useSaveTag();
  const modifyTagMutation = useModifyTag();
  const deleteTagMutation = useDeleteTag();

  // 생성 모달 상태
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [newTagName, setNewTagName] = useState('');

  // 수정 모달 상태
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [editingTag, setEditingTag] = useState<Tag | null>(null);
  const [editTagName, setEditTagName] = useState('');
  const [editTagColor, setEditTagColor] = useState<TagColor>(TAG_COLOR_PALETTE[0]);

  // 삭제 확인 다이얼로그 상태
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [deletingTag, setDeletingTag] = useState<Tag | null>(null);

  const tags = tagData?.tags ?? [];
  const totalCount = tagData?.totalCount ?? 0;
  const maxCount = tagData?.maxCount ?? 10;

  // 태그 생성
  const handleCreate = async () => {
    if (!newTagName.trim()) return;

    try {
      await saveTagMutation.mutateAsync({
        familyId,
        request: { name: newTagName.trim() },
      });
      toast({ title: '태그가 생성되었습니다.' });
      setNewTagName('');
      setIsCreateOpen(false);
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 생성에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  // 수정 모달 열기
  const openEditModal = (tag: Tag) => {
    setEditingTag(tag);
    setEditTagName(tag.name);
    setEditTagColor(tag.color as TagColor);
    setIsEditOpen(true);
  };

  // 태그 수정
  const handleEdit = async () => {
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
      setIsEditOpen(false);
      setEditingTag(null);
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 수정에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  // 삭제 확인 다이얼로그 열기
  const openDeleteDialog = (tag: Tag) => {
    setDeletingTag(tag);
    setIsDeleteOpen(true);
  };

  // 태그 삭제
  const handleDelete = async () => {
    if (!deletingTag) return;

    try {
      await deleteTagMutation.mutateAsync({
        familyId,
        tagId: deletingTag.id,
      });
      toast({ title: '태그가 삭제되었습니다.' });
      setIsDeleteOpen(false);
      setDeletingTag(null);
    } catch (error: any) {
      const message = error?.response?.data?.message || '태그 삭제에 실패했습니다.';
      toast({ title: message, variant: 'destructive' });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-8">
        <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold">태그 관리</h2>
          <p className="text-sm text-muted-foreground">
            {totalCount}/{maxCount}개 사용중
          </p>
        </div>
        <Button
          size="sm"
          onClick={() => setIsCreateOpen(true)}
          disabled={totalCount >= maxCount}
        >
          <Plus className="h-4 w-4 mr-1" />
          새 태그
        </Button>
      </div>

      {/* 태그 목록 */}
      {tags.length === 0 ? (
        <div className="text-center py-8 text-muted-foreground">
          <p>아직 태그가 없습니다.</p>
          <p className="text-sm">태그를 만들어 구성원을 분류해보세요.</p>
        </div>
      ) : (
        <div className="space-y-2">
          {tags.map((tag) => (
            <div
              key={tag.id}
              className="flex items-center justify-between p-3 rounded-lg border bg-card"
            >
              <div className="flex items-center gap-3">
                <div
                  className="w-4 h-4 rounded-full"
                  style={{ backgroundColor: tag.color }}
                />
                <span className="font-medium">{tag.name}</span>
                <span className="text-sm text-muted-foreground">
                  {tag.memberCount}명
                </span>
              </div>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="sm">
                    <MoreHorizontal className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem onClick={() => openEditModal(tag)}>
                    <Pencil className="h-4 w-4 mr-2" />
                    수정
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    onClick={() => openDeleteDialog(tag)}
                    className="text-destructive"
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    삭제
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          ))}
        </div>
      )}

      {/* 생성 모달 */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>새 태그 만들기</DialogTitle>
            <DialogDescription>
              구성원을 분류할 태그를 만들어보세요.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Input
              placeholder="태그 이름 (최대 10자)"
              maxLength={10}
              value={newTagName}
              onChange={(e) => setNewTagName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' && newTagName.trim()) {
                  handleCreate();
                }
              }}
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateOpen(false)}>
              취소
            </Button>
            <Button
              onClick={handleCreate}
              disabled={!newTagName.trim() || saveTagMutation.isPending}
            >
              {saveTagMutation.isPending && (
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
              )}
              만들기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 수정 모달 */}
      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>태그 수정</DialogTitle>
            <DialogDescription>
              태그 이름과 색상을 변경할 수 있습니다.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div>
              <label className="text-sm font-medium">이름</label>
              <Input
                className="mt-1"
                placeholder="태그 이름 (최대 10자)"
                maxLength={10}
                value={editTagName}
                onChange={(e) => setEditTagName(e.target.value)}
              />
            </div>
            <div>
              <label className="text-sm font-medium">색상</label>
              <div className="mt-2">
                <ColorPicker
                  selectedColor={editTagColor}
                  onColorSelect={setEditTagColor}
                />
              </div>
            </div>
            <div>
              <label className="text-sm font-medium">미리보기</label>
              <div className="mt-2">
                <TagBadge
                  name={editTagName || '태그 이름'}
                  color={editTagColor}
                  size="md"
                />
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditOpen(false)}>
              취소
            </Button>
            <Button
              onClick={handleEdit}
              disabled={!editTagName.trim() || modifyTagMutation.isPending}
            >
              {modifyTagMutation.isPending && (
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
              )}
              저장
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 삭제 확인 다이얼로그 */}
      <AlertDialog open={isDeleteOpen} onOpenChange={setIsDeleteOpen}>
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
              onClick={handleDelete}
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
    </div>
  );
};