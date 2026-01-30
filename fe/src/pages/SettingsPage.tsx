/**
 * 프로필 설정 페이지
 * 사용자의 이름, 생일, 생일유형을 수정할 수 있습니다.
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
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
import { Skeleton } from '@/components/ui/skeleton';
import { useCurrentUser, useModifyUser } from '@/hooks/queries/useUserQueries';
import { useToast } from '@/hooks/use-toast';
import { BirthdayType } from '@/api/services/familyService';
import { formatThisYearSolarBirthday } from '@/utils/lunar';

const birthdayTypeOptions = [
  { value: 'SOLAR', label: '양력' },
  { value: 'LUNAR', label: '음력' },
];

const SettingsPage: React.FC = () => {
  const navigate = useNavigate();
  const { toast } = useToast();

  const { data: user, isLoading: isUserLoading } = useCurrentUser();
  const modifyUserMutation = useModifyUser();

  const [name, setName] = useState('');
  const [birthday, setBirthday] = useState('');
  const [birthdayType, setBirthdayType] = useState<BirthdayType>('SOLAR');

  // 사용자 정보 로드 시 폼 초기화
  useEffect(() => {
    if (user) {
      setName(user.name || '');
      setBirthday(user.birthday ? user.birthday.split('T')[0] : '');
      setBirthdayType(user.birthdayType || 'SOLAR');
    }
  }, [user]);

  const isSubmitDisabled = !name.trim() || modifyUserMutation.isPending;

  // 음력일 때 양력 변환 날짜 계산
  const solarBirthdayDisplay = birthdayType === 'LUNAR' && birthday
    ? formatThisYearSolarBirthday(birthday)
    : null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) return;

    // 생일이 있으면 생일 유형도 필수
    if (birthday && !birthdayType) {
      toast({ title: '생일 유형을 선택해주세요.', variant: 'destructive' });
      return;
    }

    try {
      await modifyUserMutation.mutateAsync({
        name: name.trim(),
        birthday: birthday ? `${birthday}T00:00:00` : undefined,
        birthdayType: birthday ? birthdayType : undefined,
      });

      toast({ title: '프로필이 수정되었습니다.' });
    } catch {
      toast({ title: '프로필 수정에 실패했습니다.', variant: 'destructive' });
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  if (isUserLoading) {
    return (
      <div className="app-shell h-screen flex flex-col overflow-hidden">
        {/* Header Skeleton */}
        <header className="shrink-0 flex items-center gap-3 px-3 py-1.5 border-b border-border bg-card">
          <Skeleton className="w-6 h-6" />
          <Skeleton className="h-4 w-20" />
        </header>

        {/* Form Skeleton */}
        <div className="flex-1 overflow-auto p-3 space-y-4">
          <div className="space-y-1.5">
            <Skeleton className="h-3 w-10" />
            <Skeleton className="h-6 w-full" />
          </div>
          <div className="space-y-1.5">
            <Skeleton className="h-3 w-8" />
            <Skeleton className="h-6 w-full" />
          </div>
          <div className="space-y-1.5">
            <Skeleton className="h-3 w-14" />
            <Skeleton className="h-6 w-full" />
          </div>
          <Skeleton className="h-6 w-full" />
        </div>
      </div>
    );
  }

  return (
    <div className="app-shell h-screen flex flex-col overflow-hidden">
      {/* Header */}
      <header className="shrink-0 flex items-center gap-2 px-3 py-1.5 border-b border-border bg-card">
        <Button
          variant="ghost"
          size="icon"
          onClick={handleBack}
          aria-label="뒤로 가기"
        >
          <ArrowLeft className="h-4 w-4" strokeWidth={1.5} />
        </Button>
        <h1 className="text-sm font-semibold">프로필 설정</h1>
      </header>

      {/* Form */}
      <form onSubmit={handleSubmit} className="flex-1 overflow-auto p-3 space-y-6">
        {/* 회원정보 수정 섹션 */}
        <section className="bg-card border border-border rounded-lg p-4 space-y-4">
          <h2 className="text-sm font-semibold text-foreground">회원정보 수정</h2>

          <div className="space-y-1">
            <Label htmlFor="name" className="text-xs text-left block">
              이름 <span className="text-destructive">*</span>
            </Label>
            <Input
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="이름을 입력하세요"
              maxLength={50}
              aria-required="true"
            />
            {!name.trim() && (
              <p className="text-xs text-destructive">이름을 입력해주세요</p>
            )}
          </div>

          <div className="space-y-1">
            <Label htmlFor="birthday" className="text-xs text-left block">생일</Label>
            <Input
              id="birthday"
              type="date"
              value={birthday}
              onChange={(e) => setBirthday(e.target.value)}
            />
          </div>

          <div className="space-y-1">
            <Label htmlFor="birthdayType" className="text-xs text-left block">생일 유형</Label>
            <Select
              value={birthdayType || 'SOLAR'}
              onValueChange={(value) => setBirthdayType(value as BirthdayType)}
            >
              <SelectTrigger id="birthdayType" className="h-6 text-xs">
                <SelectValue placeholder="생일 유형 선택" />
              </SelectTrigger>
              <SelectContent>
                {birthdayTypeOptions.map((option) => (
                  <SelectItem key={option.value} value={option.value} className="py-1 text-xs">
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {solarBirthdayDisplay && (
              <p className="text-xs text-muted-foreground">
                올해 양력: {solarBirthdayDisplay}
              </p>
            )}
          </div>

          <Button
            type="submit"
            className="w-full h-10"
            disabled={isSubmitDisabled}
          >
            {modifyUserMutation.isPending ? '저장 중...' : '저장'}
          </Button>
        </section>

        {/* 회원 탈퇴 섹션 */}
        <section className="border border-destructive/30 rounded-lg p-4 space-y-3">
          <h2 className="text-sm font-semibold text-destructive">회원 탈퇴</h2>
          <p className="text-xs text-muted-foreground">
            계정을 삭제하면 모든 데이터가 영구적으로 삭제됩니다.
          </p>
          <Button
            type="button"
            variant="destructive"
            className="w-full h-10"
            disabled
          >
            회원 탈퇴
          </Button>
          <p className="text-xs text-muted-foreground text-center">
            (준비 중)
          </p>
        </section>
      </form>
    </div>
  );
};

export default SettingsPage;
