/**
 * 구성원 기본 정보 수정 모달 컴포넌트
 * 이름, 생일, 생일타입(음력/양력)을 수정합니다.
 */

import React, { useState, useEffect } from 'react';
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
import { useModifyMemberInfo } from '@/hooks/queries/useFamilyQueries';
import { useToast } from '@/hooks/use-toast';
import { BirthdayType } from '@/api/services/familyService';
import { formatThisYearSolarBirthday } from '@/utils/lunar';

interface MemberEditModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  familyId: number | string;
  memberId: number | string;
  currentName: string;
  currentBirthday?: string;
  currentBirthdayType?: BirthdayType;
  onSuccess?: () => void;
}

const birthdayTypeOptions = [
  { value: 'SOLAR', label: '양력' },
  { value: 'LUNAR', label: '음력' },
];

export const MemberEditModal: React.FC<MemberEditModalProps> = ({
  open,
  onOpenChange,
  familyId,
  memberId,
  currentName,
  currentBirthday,
  currentBirthdayType,
  onSuccess,
}) => {
  const [name, setName] = useState(currentName);
  const [birthday, setBirthday] = useState('');
  const [birthdayType, setBirthdayType] = useState<BirthdayType>(currentBirthdayType || 'SOLAR');

  const { toast } = useToast();
  const modifyInfoMutation = useModifyMemberInfo();

  const isSubmitDisabled = !name.trim() || modifyInfoMutation.isPending;

  // 음력일 때 양력 변환 날짜 계산
  const solarBirthdayDisplay = birthdayType === 'LUNAR' && birthday
    ? formatThisYearSolarBirthday(birthday)
    : null;

  const resetForm = () => {
    setName(currentName);
    setBirthday(currentBirthday ? currentBirthday.split('T')[0] : '');
    setBirthdayType(currentBirthdayType || 'SOLAR');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) return;

    try {
      await modifyInfoMutation.mutateAsync({
        familyId,
        memberId,
        request: {
          name: name.trim(),
          birthday: birthday ? `${birthday}T00:00:00` : undefined,
          birthdayType: birthday ? birthdayType : undefined,
        },
      });

      toast({ title: '정보가 수정되었습니다.' });
      onOpenChange(false);
      onSuccess?.();
    } catch {
      toast({ title: '정보 수정에 실패했습니다.', variant: 'destructive' });
    }
  };

  const handleOpenChange = (isOpen: boolean) => {
    if (!isOpen) {
      resetForm();
    }
    onOpenChange(isOpen);
  };

  // 모달이 열릴 때 현재 값으로 초기화
  useEffect(() => {
    if (open) {
      setName(currentName);
      setBirthday(currentBirthday ? currentBirthday.split('T')[0] : '');
      setBirthdayType(currentBirthdayType || 'SOLAR');
    }
  }, [open, currentName, currentBirthday, currentBirthdayType]);

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="sm:max-w-[400px]">
        <DialogHeader>
          <DialogTitle>구성원 정보 수정</DialogTitle>
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
              maxLength={50}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="birthday">생일</Label>
            <Input
              id="birthday"
              type="date"
              value={birthday}
              onChange={(e) => setBirthday(e.target.value)}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="birthdayType">생일 유형</Label>
            <Select
              value={birthdayType || 'SOLAR'}
              onValueChange={(value) => setBirthdayType(value as BirthdayType)}
            >
              <SelectTrigger id="birthdayType">
                <SelectValue placeholder="생일 유형 선택" />
              </SelectTrigger>
              <SelectContent>
                {birthdayTypeOptions.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {solarBirthdayDisplay && (
              <p className="text-sm text-muted-foreground">
                올해 양력: {solarBirthdayDisplay}
              </p>
            )}
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
              {modifyInfoMutation.isPending ? '저장 중...' : '저장'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};
