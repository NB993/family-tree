/**
 * 관계 설정 모달 컴포넌트
 * FamilyMember의 relationshipType을 설정합니다.
 */

import React, { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { FamilyMemberRelationshipType, FamilyMemberRelationshipLabels } from '@/types/family';
import { useModifyMemberRelationship } from '@/hooks/queries/useFamilyQueries';

interface SetRelationshipModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  familyId: number | string;
  memberId: number | string;
  memberName: string;
  currentRelationshipType?: FamilyMemberRelationshipType;
  currentCustomRelationship?: string;
  onSuccess?: () => void;
}

const relationshipOptions = Object.values(FamilyMemberRelationshipType).map(
  (value) => ({
    value,
    label: FamilyMemberRelationshipLabels[value],
  })
);

export const SetRelationshipModal: React.FC<SetRelationshipModalProps> = ({
  open,
  onOpenChange,
  familyId,
  memberId,
  memberName,
  currentRelationshipType,
  currentCustomRelationship,
  onSuccess,
}) => {
  const [relationshipType, setRelationshipType] = useState(currentRelationshipType || '');
  const [customRelationship, setCustomRelationship] = useState(currentCustomRelationship || '');

  const modifyRelationshipMutation = useModifyMemberRelationship();

  const isCustomRelationship = relationshipType === FamilyMemberRelationshipType.CUSTOM;
  const isSubmitDisabled =
    !relationshipType ||
    (isCustomRelationship && !customRelationship.trim()) ||
    modifyRelationshipMutation.isPending;

  const resetForm = () => {
    setRelationshipType(currentRelationshipType || '');
    setCustomRelationship(currentCustomRelationship || '');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!relationshipType) return;

    try {
      await modifyRelationshipMutation.mutateAsync({
        familyId,
        memberId,
        relationshipType: relationshipType as FamilyMemberRelationshipType,
        customRelationship: isCustomRelationship ? customRelationship.trim() : undefined,
      });

      onOpenChange(false);
      onSuccess?.();
    } catch {
      console.error('관계 설정 실패');
    }
  };

  const handleOpenChange = (isOpen: boolean) => {
    if (!isOpen) {
      resetForm();
    }
    onOpenChange(isOpen);
  };

  // 모달이 열릴 때 현재 값으로 초기화
  React.useEffect(() => {
    if (open) {
      setRelationshipType(currentRelationshipType || '');
      setCustomRelationship(currentCustomRelationship || '');
    }
  }, [open, currentRelationshipType, currentCustomRelationship]);

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-[400px]">
        <DialogHeader>
          <DialogTitle>{memberName}님과의 관계 설정</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="relationshipType">
              관계 <span className="text-destructive">*</span>
            </Label>
            <Select value={relationshipType} onValueChange={setRelationshipType}>
              <SelectTrigger id="relationshipType">
                <SelectValue placeholder="관계를 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                {relationshipOptions.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {isCustomRelationship && (
            <div className="space-y-2">
              <Label htmlFor="customRelationship">
                직접 입력 <span className="text-destructive">*</span>
              </Label>
              <Input
                id="customRelationship"
                value={customRelationship}
                onChange={(e) => setCustomRelationship(e.target.value)}
                placeholder="관계를 직접 입력하세요"
                maxLength={50}
              />
              <p className="text-xs text-muted-foreground">
                {customRelationship.length}/50자
              </p>
            </div>
          )}

          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              type="button"
              variant="outline"
              onClick={() => handleOpenChange(false)}
            >
              취소
            </Button>
            <Button type="submit" disabled={isSubmitDisabled}>
              {modifyRelationshipMutation.isPending ? '설정 중...' : '설정'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};
