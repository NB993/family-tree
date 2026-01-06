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
import { FamilyMemberRelationshipType } from '@/types/family';
import { useCreateFamilyMember } from '@/hooks/queries/useFamilyQueries';

interface CreateFamilyMemberModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  familyId: number;
  onSuccess?: () => void;
}

// TODO: 기본 프로필 이미지 경로 - 추후 실제 이미지로 교체
// const DEFAULT_PROFILE_IMAGE = '/images/default-profile.png';

const relationshipOptions = Object.entries(FamilyMemberRelationshipType).map(
  ([key, value]) => ({
    key,
    label: value,
  })
);

const CreateFamilyMemberModal: React.FC<CreateFamilyMemberModalProps> = ({
  open,
  onOpenChange,
  familyId,
  onSuccess,
}) => {
  const [name, setName] = useState('');
  const [relationship, setRelationship] = useState('');
  const [customRelationship, setCustomRelationship] = useState('');
  const [birthday, setBirthday] = useState('');

  const createMemberMutation = useCreateFamilyMember();

  const isCustomRelationship = relationship === 'CUSTOM';
  const isSubmitDisabled = !name.trim() || createMemberMutation.isPending;

  const resetForm = () => {
    setName('');
    setRelationship('');
    setCustomRelationship('');
    setBirthday('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) return;

    try {
      await createMemberMutation.mutateAsync({
        familyId,
        form: {
          name: name.trim(),
          birthday: birthday ? `${birthday}T00:00:00` : undefined,
          relationshipType: relationship || undefined,
          customRelationship: isCustomRelationship ? customRelationship : undefined,
        },
      });

      resetForm();
      onOpenChange(false);
      onSuccess?.();
    } catch {
      // API 에러는 useCreateFamilyMember에서 처리
      console.error('FamilyMember 생성 실패');
    }
  };

  const handleOpenChange = (isOpen: boolean) => {
    if (!isOpen) {
      resetForm();
    }
    onOpenChange(isOpen);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-[400px]">
        <DialogHeader>
          <DialogTitle>가족 구성원 등록</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">
              이름 <span className="text-destructive">*</span>
            </Label>
            <Input
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="이름을 입력하세요"
              autoFocus
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="relationship">관계</Label>
            <Select value={relationship} onValueChange={setRelationship}>
              <SelectTrigger id="relationship">
                <SelectValue placeholder="관계를 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                {relationshipOptions.map((option) => (
                  <SelectItem key={option.key} value={option.key}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {isCustomRelationship && (
            <div className="space-y-2">
              <Label htmlFor="customRelationship">직접 입력</Label>
              <Input
                id="customRelationship"
                value={customRelationship}
                onChange={(e) => setCustomRelationship(e.target.value)}
                placeholder="관계를 직접 입력하세요"
              />
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="birthday">생년월일</Label>
            <Input
              id="birthday"
              type="date"
              value={birthday}
              onChange={(e) => setBirthday(e.target.value)}
            />
          </div>

          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              type="button"
              variant="outline"
              onClick={() => handleOpenChange(false)}
            >
              취소
            </Button>
            <Button type="submit" disabled={isSubmitDisabled}>
              {createMemberMutation.isPending ? '등록 중...' : '등록'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CreateFamilyMemberModal;
