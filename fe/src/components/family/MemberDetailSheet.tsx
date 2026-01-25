/**
 * 멤버 상세 정보 시트 컴포넌트
 * PRD-006: FamilyMember 태그 기능
 * 멤버 행 클릭 시 열리는 바텀 시트
 */

import React, { useState } from 'react';
import { Phone, Calendar, User, Plus } from 'lucide-react';
import {
  Sheet,
  SheetContent,
} from '../ui/sheet';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { Button } from '../ui/button';
import { TagBadge } from './TagBadge';
import { TagSelector } from './TagSelector';
import { SetRelationshipModal } from './SetRelationshipModal';
import { FamilyMemberWithRelationship } from '../../api/services/familyService';
import { TagSimple } from '../../types/tag';
import { cn } from '../../lib/utils';

interface MemberDetailSheetProps {
  familyId: number | string;
  member: FamilyMemberWithRelationship | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export const MemberDetailSheet: React.FC<MemberDetailSheetProps> = ({
  familyId,
  member,
  open,
  onOpenChange,
}) => {
  const [localTags, setLocalTags] = useState<TagSimple[]>([]);
  const [isRelationshipModalOpen, setIsRelationshipModalOpen] = useState(false);

  // member가 변경되면 localTags 초기화
  React.useEffect(() => {
    if (member) {
      setLocalTags(member.tags || []);
    }
  }, [member]);

  if (!member) return null;

  // 태그 변경 시 로컬 상태만 업데이트 (React Query 캐시 무효화가 서버 동기화 처리)
  const handleTagsChange = (newTags: TagSimple[]) => {
    setLocalTags(newTags);
  };

  const formatBirthday = (birthday?: string, birthdayType?: string | null) => {
    if (!birthday) return null;
    const date = new Date(birthday);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const typeLabel = birthdayType === 'LUNAR' ? '음력' : '양력';
    return `${year}.${month}.${day} (${typeLabel})`;
  };

  const formatPhoneNumber = (phone?: string) => {
    if (!phone) return null;
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
      return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    }
    if (cleaned.length === 10) {
      return cleaned.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
    }
    return phone;
  };

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent side="bottom" className="h-auto max-h-[80vh] rounded-t-2xl p-0">
        <div className="px-4 pt-4 pb-6 space-y-4">
          {/* 핸들 바 */}
          <div className="flex justify-center">
            <div className="w-10 h-1 bg-muted-foreground/20 rounded-full" />
          </div>

          {/* 헤더: 프로필 + 이름 */}
          <div className="flex items-center gap-3">
            <Avatar className="w-14 h-14">
              <AvatarImage src={member.memberProfileImageUrl} alt={member.memberName} />
              <AvatarFallback className="bg-primary/10 text-primary text-lg font-semibold">
                {member.memberName.charAt(0)}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1 min-w-0">
              <h2 className="text-lg font-semibold text-foreground truncate">
                {member.memberName}
              </h2>
              <p className={cn(
                "text-sm",
                member.hasRelationship ? "text-muted-foreground" : "text-amber-600"
              )}>
                {member.relationshipGuideMessage}
              </p>
            </div>
          </div>

          {/* 태그 영역 */}
          <div className="space-y-2">
            <label className="text-xs font-medium text-muted-foreground">태그</label>
            <div className="flex flex-wrap items-center gap-1.5">
              {localTags.map((tag) => (
                <TagSelector
                  key={tag.id}
                  familyId={familyId}
                  memberId={member.memberId}
                  memberTags={localTags}
                  onTagsChange={handleTagsChange}
                >
                  <button type="button">
                    <TagBadge
                      name={tag.name}
                      color={tag.color}
                      size="md"
                      className="cursor-pointer hover:opacity-80"
                    />
                  </button>
                </TagSelector>
              ))}
              <TagSelector
                familyId={familyId}
                memberId={member.memberId}
                memberTags={localTags}
                onTagsChange={handleTagsChange}
              >
                <Button
                  variant="outline"
                  size="sm"
                  className="h-6 px-2 text-xs text-muted-foreground"
                >
                  <Plus className="h-3 w-3 mr-1" />
                  {localTags.length === 0 ? '태그 추가' : '추가'}
                </Button>
              </TagSelector>
            </div>
          </div>

          {/* 정보 섹션 */}
          <div className="space-y-3 pt-2 border-t">
            {/* 나이/생일 */}
            {(member.memberAge || member.memberBirthday) && (
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center flex-shrink-0">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-foreground">
                    {member.memberAge && `${member.memberAge}세`}
                    {member.memberAge && member.memberBirthday && ' · '}
                    {formatBirthday(member.memberBirthday, member.memberBirthdayType)}
                  </p>
                </div>
              </div>
            )}

            {/* 연락처 */}
            {member.memberPhoneNumber ? (
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-green-50 flex items-center justify-center flex-shrink-0">
                  <Phone className="h-4 w-4 text-green-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <a
                    href={`tel:${member.memberPhoneNumber.replace(/\D/g, '')}`}
                    className="text-sm text-foreground hover:underline"
                  >
                    {formatPhoneNumber(member.memberPhoneNumber)}
                  </a>
                </div>
              </div>
            ) : (
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center flex-shrink-0">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-muted-foreground">연락처 없음</p>
                </div>
              </div>
            )}

            {/* 관계 정보 */}
            <button
              type="button"
              className="flex items-center gap-3 w-full text-left hover:bg-secondary/50 rounded-lg p-1 -m-1 transition-colors"
              onClick={() => setIsRelationshipModalOpen(true)}
            >
              <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center flex-shrink-0">
                <User className="h-4 w-4 text-muted-foreground" />
              </div>
              <div className="flex-1 min-w-0">
                <p className={cn(
                  "text-sm",
                  member.hasRelationship ? "text-foreground" : "text-amber-600"
                )}>
                  {member.hasRelationship
                    ? member.relationshipDisplayName || member.customRelationshipName
                    : '관계를 설정해주세요'}
                </p>
              </div>
            </button>
          </div>
        </div>

        {/* 관계 설정 모달 */}
        <SetRelationshipModal
          open={isRelationshipModalOpen}
          onOpenChange={setIsRelationshipModalOpen}
          familyId={familyId}
          memberId={member.memberId}
          memberName={member.memberName}
          currentRelationshipType={member.relationshipType}
          currentCustomRelationship={member.customRelationshipName}
        />
      </SheetContent>
    </Sheet>
  );
};